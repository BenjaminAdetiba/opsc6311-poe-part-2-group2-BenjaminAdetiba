package com.example.extrackapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.extrackapp.databinding.ActivityCategorySummaryBinding
import com.example.extrackapp.databinding.ActivityViewReportBinding

class CategorySummaryActivity : AppCompatActivity() {
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var listView: ListView
    private var userId: Int = -1
    private lateinit var binding: ActivityCategorySummaryBinding
    private var username = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_summary)
        binding = ActivityCategorySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dbHelper = DatabaseHelper(this)
        userId = intent.getIntExtra("userId", -1)
        listView = findViewById(R.id.listViewCategorySummary)

        loadSummary()

    }

    private fun loadSummary() {
        val categoryTotals = dbHelper.getTotalSpentPerCategory(userId)
        val summaryList = categoryTotals.map { "${it.key}: \$${String.format("%.2f", it.value)}" }

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, summaryList)
        listView.adapter = adapter

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
