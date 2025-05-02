package com.example.extrackapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.extrackapp.databinding.ItemFilterExpenseBinding
import com.example.extrackapp.FilterExpense

class FilterExpenseAdapter(private val expenses: List<FilterExpense>) : RecyclerView.Adapter<FilterExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(val binding: ItemFilterExpenseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemFilterExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.binding.tvCategoryId.text = "Category ID: ${expense.categoryId}"
        holder.binding.tvCategoryId.text = "Category Name: ${expense.name}"
        holder.binding.tvName.text = "name: ${expense.name}"
        holder.binding.tvAmount.text = "Amount: $${expense.amount}"
        holder.binding.tvDate.text = "Date: ${expense.date}"
        holder.binding.tvDescription.text= "Description ${expense.description}"
    }

    override fun getItemCount(): Int = expenses.size
}
