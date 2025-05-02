package com.example.extrackapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.extrackapp.databinding.ActivityCalculatorScreenBinding
import com.example.extrackapp.databinding.ActivityViewExpenseBinding

class CalculatorScreenActivity : AppCompatActivity(){

    private lateinit var binding: ActivityCalculatorScreenBinding
    private lateinit var txtResult: TextView
    private lateinit var editText: EditText
    private var firstNumber: Double? = null
    private var secondNumber: Double? = null
    private var operator: String? = null
    private var userId : Int = -1
    private var username : String= ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCalculatorScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        setupButtonListeners()
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



    private fun setupButtonListeners() {
        binding.btnAddCategory.setOnClickListener {
            startActivityWithUser(AddCategoryActivity::class.java)
        }

        binding.btnAddExpense.setOnClickListener {
            startActivityWithUser(AddExpenseActivity::class.java)
        }

        binding.btnViewExpenses.setOnClickListener {
            startActivityWithUser(ViewExpenseActivity::class.java)
        }

        binding.btnViewReport.setOnClickListener {
            startActivityWithUser(ViewReportActivity::class.java)
        }

        binding.btnSetGoals.setOnClickListener {
            startActivityWithUser(SetGoalActivity::class.java)
        }

        binding.btnCatsummary.setOnClickListener {
            val intent = Intent(this, CategorySummaryActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
        val btnOpenFilterExpenses = findViewById<Button>(R.id.btnOpenFilterExpenses)

        btnOpenFilterExpenses.setOnClickListener {
            val intent = Intent(this, FilterExpenseActivity::class.java)
            intent.putExtra("userId", userId) // Pass current logged-in userId
            startActivity(intent)
        }



    }

    private fun startActivityWithUser(activityClass: Class<*>) {
        val intent = Intent(this, activityClass).apply {
            putExtra("userId", userId)
            putExtra("username", username)
        }
        startActivity(intent)
    }
}

