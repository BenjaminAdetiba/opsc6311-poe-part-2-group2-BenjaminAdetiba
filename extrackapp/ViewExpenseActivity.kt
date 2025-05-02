package com.example.extrackapp


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.extrackapp.databinding.ActivityViewExpenseBinding


class ViewExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewExpenseBinding
    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1
    private var username: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        userId = intent.getIntExtra("userId", -1)

        val expenses = dbHelper.getExpenses(userId)
        val adapter = ExpenseAdapter(expenses)

        binding.recyclerExpenses.layoutManager = LinearLayoutManager(this)
        binding.recyclerExpenses.adapter = adapter


      setupButtonListeners()
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
        binding.calcubutton.setOnClickListener {
            startActivityWithUser(CalculatorScreenActivity::class.java)
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
