package com.example.extrackapp

import android.icu.text.SimpleDateFormat
import android.os.Bundle
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
    }
}
