package com.example.extrackapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.extrackapp.databinding.ActivityFilterExpenseBinding
import java.util.*

class FilterExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFilterExpenseBinding
    private lateinit var dbHelper: FilterExpenseDatabaseHelper
    private lateinit var adapter: FilterExpenseAdapter
    private var userId: Int = -1
    private var username : String = ""
    private val expenses = mutableListOf<FilterExpense>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = FilterExpenseDatabaseHelper(this)
        userId = intent.getIntExtra("userId", -1)

        adapter = FilterExpenseAdapter(expenses)
        binding.rvFilteredExpenses.layoutManager = LinearLayoutManager(this)
        binding.rvFilteredExpenses.adapter = adapter

        binding.etStartDateFilter.hint = "Select Date"
        binding.etStartDateFilter.setOnClickListener { showDatePicker(binding.etStartDateFilter) }
        binding.etEndDateFilter.isEnabled = false // Disable end date (we don't use it anymore)

        binding.btnLoadFilteredExpenses.setOnClickListener { loadExpensesForSelectedDate() }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
                editText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun loadExpensesForSelectedDate() {
        val selectedDate = binding.etStartDateFilter.text.toString()

        if (selectedDate.isNotEmpty()) {
            expenses.clear()
            expenses.addAll(dbHelper.getExpensesOnDate(userId, selectedDate))
            adapter.notifyDataSetChanged()
            calculateTotals()
        }
    }

    private fun calculateTotals() {
        val categoryTotals = expenses.groupBy { it.name }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val totalsText = if (categoryTotals.isEmpty()) {
            "No expenses found for this day."
        } else {
            categoryTotals.entries.joinToString("\n") {
                "Category ${it.key}: $${String.format("%.2f", it.value)}"
            }
        }

        binding.tvFilteredCategoryTotals.text = totalsText

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
