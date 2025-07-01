package com.example.simplecalculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.lang.Exception

class CalculatorFragment : Fragment() {
    private lateinit var resultBox: TextView
    private var currentInput = StringBuilder()
    private var fullExpression = StringBuilder()
    private var firstOperand: Double? = null
    private var pendingOperation: String? = null
    private var resetInput = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_calculator, container, false)

        // Initialize views
        resultBox = view.findViewById(R.id.resultBox)

        // Set up number buttons (0-9)
        listOf(
            R.id.button2,  // 0
            R.id.button5,  // 3
            R.id.button6,  // 2
            R.id.button7, // 1
            R.id.button9,  // 6
            R.id.button10, // 5
            R.id.button11, // 4
            R.id.button13, // 9
            R.id.button14, // 8
            R.id.button15  // 7
        ).forEach { buttonId ->
            view.findViewById<Button>(buttonId).setOnClickListener { onNumberClick(it) }
        }

        // Set up operator buttons (+, -, *, /, =)
        listOf(
            R.id.button8,  // +
            R.id.button12, // -
            R.id.button16, // *
            R.id.button20, // /
            R.id.button4   // =
        ).forEach { buttonId ->
            view.findViewById<Button>(buttonId).setOnClickListener { onOperatorClick(it) }
        }

        // Set up decimal point button
        view.findViewById<Button>(R.id.button3).setOnClickListener { onDecimalClick(it) }

        // Set up parentheses buttons
        view.findViewById<Button>(R.id.button18).setOnClickListener { appendToInput("(") }
        view.findViewById<Button>(R.id.button19).setOnClickListener { appendToInput(")") }

        // Set up clear buttons
        view.findViewById<Button>(R.id.button).setOnClickListener { onAllClearClick() }  // AC
        view.findViewById<Button>(R.id.button17).setOnClickListener { onClearClick() }   // C

        return view
    }

    private fun onNumberClick(view: View) {
        val button = view as Button
        val number = button.text.toString()

        if (resetInput) {
            currentInput.clear()
            resetInput = false
        }

        // Prevent leading zeros
        if (number == "0" && currentInput.isEmpty()) {
            return
        }

        currentInput.append(number)
        fullExpression.append(number)
        updateDisplay(fullExpression.toString())
    }

    private fun onDecimalClick(view: View) {
        if (resetInput) {
            currentInput.clear()
            resetInput = false
        }

        // Prevent multiple decimals in a number
        if (!currentInput.contains(".")) {
            if (currentInput.isEmpty()) {
                currentInput.append("0")
                fullExpression.append("0")
            }
            currentInput.append(".")
            fullExpression.append(".")
            updateDisplay(fullExpression.toString())
        }
    }

    private fun onOperatorClick(view: View) {
        val button = view as Button
        val operation = button.text.toString()

        try {
            if (currentInput.isNotEmpty()) {
                val inputValue = currentInput.toString().toDouble()

                if (firstOperand == null) {
                    firstOperand = inputValue
                } else if (pendingOperation != null) {
                    firstOperand = performOperation(firstOperand!!, inputValue, pendingOperation!!)
                }

                if (operation != "=") {
                    pendingOperation = operation
                    fullExpression.append(operation)
                    updateDisplay(fullExpression.toString())
                } else {
                    if (pendingOperation != null) {
                        fullExpression.clear()
                        displayResult(firstOperand!!)
                    }
                    pendingOperation = null
                }
                resetInput = true
            } else if (operation == "=" && firstOperand != null && pendingOperation != null) {
                // Handle case like "5 + = " which should give "10" (5 + 5)
                firstOperand = performOperation(firstOperand!!, firstOperand!!, pendingOperation!!)
                fullExpression.clear()
                displayResult(firstOperand!!)
                pendingOperation = null
                resetInput = true
            }
        } catch (e: Exception) {
            resultBox.text = "Error"
            resetCalculator()
        }
    }

    private fun performOperation(first: Double, second: Double, operation: String): Double {
        return when (operation) {
            "+" -> first + second
            "-" -> first - second
            "*" -> first * second
            "/" -> {
                if (second == 0.0) {
                    resultBox.text = "Error"
                    resetCalculator()
                    throw ArithmeticException("Division by zero")
                }
                first / second
            }
            else -> throw IllegalArgumentException("Unknown operation")
        }
    }

    private fun displayResult(result: Double) {
        // Remove .0 if the result is an integer
        val displayText = if (result % 1 == 0.0) {
            result.toInt().toString()
        } else {
            result.toString()
        }
        resultBox.text = displayText
    }

    private fun appendToInput(value: String) {
        if (resetInput) {
            currentInput.clear()
            resetInput = false
        }
        currentInput.append(value)
        fullExpression.append(value)
        updateDisplay(fullExpression.toString())
    }

    private fun onAllClearClick() {
        resetCalculator()
        resultBox.text = "0"
    }

    private fun onClearClick() {
        if (currentInput.isNotEmpty()) {
            currentInput.deleteCharAt(currentInput.length - 1)
            fullExpression.deleteCharAt(fullExpression.length - 1)
            updateDisplay(fullExpression.toString())
        } else {
            resultBox.text = "0"
        }
    }

    private fun updateDisplay(text: String) {
        resultBox.text = if (text.isNotEmpty()) text else "0"
    }

    private fun resetCalculator() {
        currentInput.clear()
        fullExpression.clear()
        firstOperand = null
        pendingOperation = null
        resetInput = false
    }
}