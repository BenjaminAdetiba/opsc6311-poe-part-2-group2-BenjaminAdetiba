package com.example.extrackapp


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.extrackapp.databinding.ActivityAddCategoryBinding


class AddCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCategoryBinding
    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1
    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        userId = intent.getIntExtra("userId", -1)

        binding.btnSaveCategory.setOnClickListener {
            val categoryName = binding.etCategoryName.text.toString().trim()

            if (categoryName.isEmpty()) {
                Toast.makeText(this, "Please enter a category name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val success = dbHelper.addCategory(userId, categoryName)
            if (success) {
                Toast.makeText(this, "Category saved", Toast.LENGTH_SHORT).show()
                binding.etCategoryName.text.clear()
            } else {
                Toast.makeText(this, "Category already exists", Toast.LENGTH_SHORT).show()
            }
        }

        setupButtonListeners()    }


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