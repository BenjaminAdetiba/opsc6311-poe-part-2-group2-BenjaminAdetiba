package com.example.extrackapp

import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.extrackapp.databinding.ItemExpenseBinding
import java.io.File

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
        holder.binding.tvExpensename.text = "Expense Name : ${expense.expenseName}"
        holder.binding.tvDescription.text = "Description: ${expense.description}"
        holder.binding.tvStartTime.text = "Start time : ${expense.startTime}"
        holder.binding.tvEndTime.text = "End time : ${expense.endTime}"

        if (expense.photoPath.isNotEmpty()) {
            try {
                val file = File(expense.photoPath)
                if (file.exists()) {
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    holder.binding.ivPhoto.setImageBitmap(bitmap)
                } else {
                    // Try loading as URI (for content URIs from gallery)
                    val uri = Uri.parse(expense.photoPath)
                    val inputStream = holder.binding.root.context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    holder.binding.ivPhoto.setImageBitmap(bitmap)
                    inputStream?.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                holder.binding.ivPhoto.setImageResource(android.R.drawable.ic_menu_report_image)
            }
        } else {
            holder.binding.ivPhoto.setImageResource(android.R.drawable.ic_menu_report_image)
        }
    }

    override fun getItemCount(): Int = expenses.size





}