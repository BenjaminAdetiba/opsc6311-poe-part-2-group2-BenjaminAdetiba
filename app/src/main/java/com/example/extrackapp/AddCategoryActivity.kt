package com.example.extrackapp


import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.extrackapp.databinding.ActivityAddCategoryBinding


class AddCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCategoryBinding
    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1

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
    }
}