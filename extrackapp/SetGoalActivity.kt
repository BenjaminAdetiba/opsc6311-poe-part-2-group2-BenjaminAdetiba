package com.example.extrackapp

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.extrackapp.databinding.ActivitySetGoalBinding
import java.util.*

class SetGoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetGoalBinding
    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1
    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        userId = intent.getIntExtra("userId", -1)

        val month = SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(Date())

        // Load current goal
        val currentGoal = dbHelper.getGoal(userId, month)
        binding.tvCurrentGoal.text = if (currentGoal != null)
            "Current Goal for $month: Min: \$${String.format("%.2f", currentGoal.first)} Max: \$${String.format("%.2f", currentGoal.second)}"
        else
            "No goal set for this month."

        binding.btnSaveGoal.setOnClickListener {
            val minGoalAmountStr = binding.etMinGoalAmount.text.toString()
            val maxGoalAmountStr = binding.etMaxGoalAmount.text.toString()

            // Check if both fields are not empty
            if (minGoalAmountStr.isNotEmpty() && maxGoalAmountStr.isNotEmpty()) {
                val minGoalAmount = minGoalAmountStr.toDouble()
                val maxGoalAmount = maxGoalAmountStr.toDouble()

                // Check if min is less than or equal to max
                if (minGoalAmount <= maxGoalAmount) {
                    dbHelper.setGoal(userId, month, minGoalAmount, maxGoalAmount)
                    Toast.makeText(this, "Goal Saved!", Toast.LENGTH_SHORT).show()
                    binding.tvCurrentGoal.text =
                        "Current Goal for $month: Min: \$${String.format("%.2f", minGoalAmount)} Max: \$${String.format("%.2f", maxGoalAmount)}"
                } else {
                    Toast.makeText(this, "Min goal must be less than or equal to Max goal", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter valid amounts for both Min and Max", Toast.LENGTH_SHORT).show()
            }
        }
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
