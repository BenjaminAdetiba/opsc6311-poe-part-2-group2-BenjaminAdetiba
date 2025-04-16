package com.example.extrackapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.extrackapp.databinding.ItemExpenseBinding

class ExpenseAdapter(private val expenses: List<Expense>) :
    RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    inner class ExpenseViewHolder(val binding: ItemExpenseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]
        holder.binding.tvCategory.text = "Category: ${expense.categoryName}"
        holder.binding.tvAmount.text = "Amount: \$${expense.amount}"
        holder.binding.tvDate.text = "Date: ${expense.date}"
        holder.binding.tvDescription.text = "Note: ${expense.description}"

    }

    override fun getItemCount(): Int = expenses.size
}