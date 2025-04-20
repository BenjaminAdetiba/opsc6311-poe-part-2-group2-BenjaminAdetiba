package com.example.extrackapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CalculatorScreenActivity : AppCompatActivity(){


    private lateinit var txtResult: TextView
    private lateinit var editText: EditText
    private var firstNumber: Double? = null
    private var secondNumber: Double? = null
    private var operator: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator_screen)

        txtResult = findViewById(R.id.txtResult)
        editText = findViewById(R.id.editTextText)

        val numberButtons = listOf(
            findViewById<Button>(R.id.btn0), findViewById(R.id.btn1), findViewById(R.id.btn2),
            findViewById(R.id.btn3), findViewById(R.id.btn4), findViewById(R.id.btn5),
            findViewById(R.id.btn6), findViewById(R.id.btn7), findViewById(R.id.btn8), findViewById(R.id.btn9)
        )

        for (button in numberButtons) {
            button.setOnClickListener {
                editText.append(button.text)
            }
        }

        findViewById<Button>(R.id.btnadd).setOnClickListener { setOperator("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { setOperator("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { setOperator("*") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { setOperator("/") }

        findViewById<Button>(R.id.btnequals).setOnClickListener { calculateResult() }
        findViewById<Button>(R.id.btnequals2).setOnClickListener { clearAll() }
    }

    private fun setOperator(op: String) {
        firstNumber = editText.text.toString().toDoubleOrNull()
        if (firstNumber != null) {
            operator = op
            editText.text.clear()
        }
    }

    private fun calculateResult() {
        secondNumber = editText.text.toString().toDoubleOrNull()
        if (firstNumber != null && secondNumber != null && operator != null) {
            val result = when (operator) {
                "+" -> firstNumber!! + secondNumber!!
                "-" -> firstNumber!! - secondNumber!!
                "*" -> firstNumber!! * secondNumber!!
                "/" -> if (secondNumber!! != 0.0) firstNumber!! / secondNumber!! else "Invalid Operation"
                else -> "Error"
            }
            txtResult.text = result.toString()
            editText.text.clear()
        }
    }

    private fun clearAll() {
        editText.text.clear()
        txtResult.text = ""
        firstNumber = null
        secondNumber = null
        operator = null
    }
}

