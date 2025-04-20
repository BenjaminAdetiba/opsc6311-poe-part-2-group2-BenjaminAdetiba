package com.example.extrackapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.extrackapp.databinding.ActivityViewReportBinding


class ViewReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewReportBinding
    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1
    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize database helper
        dbHelper = DatabaseHelper(this)

        // Get user info from intent (make sure these are passed when starting this activity)
        userId = intent.getIntExtra("userId", -1)
        username = intent.getStringExtra("username") ?: ""

        // Set welcome message
        binding.tvWelcome.text = "Welcome to your dashboard👋"

        // Get and display monthly report
        val monthlyReport = dbHelper.getMonthlyReport(userId) ?: emptyMap()

        val reportText = StringBuilder().apply {
            if (monthlyReport.isEmpty()) {
                append("No monthly report data available")
            } else {
                for ((month, total) in monthlyReport) {
                    append("$month: $${String.format("%.2f", total)}\n")
                }
            }
        }

        binding.tvMonthlyReport.text = reportText.toString()

        // Button listeners - consistent with MainActivity
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

    }

    private fun startActivityWithUser(activityClass: Class<*>) {
        val intent = Intent(this, activityClass).apply {
            putExtra("userId", userId)
            putExtra("username", username)
        }
        startActivity(intent)
    }


}