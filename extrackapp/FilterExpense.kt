package com.example.extrackapp

data class FilterExpense (
    val id: Int,
    val userId: Int,
    val name: String,
    val categoryId: Int,
    val amount: Double,
    val date: String,
    val description: String
)
