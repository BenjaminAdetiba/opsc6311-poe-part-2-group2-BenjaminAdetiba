package com.example.extrackapp


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.extrackapp.databinding.ActivityViewExpenseBinding


class ViewExpenseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewExpenseBinding
    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1

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



    }
}
