package com.example.extrackapp

data class Expense(
    val id: Int,
    val categoryName: String,
    val expenseName: String,
    val amount: Double,
    val date: String, //change to Date
    val description: String,
    val startTime: String,
    val endTime: String,
    val photoPath: String
)