package com.example.extrackapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider

import com.example.extrackapp.databinding.ActivityAddExpenseBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddExpenseBinding
    private lateinit var dbHelper: DatabaseHelper
    private var userId: Int = -1
    private var categoryMap = mutableMapOf<String, Int>()  // name -> id
    private var photoPath: String? = null
    private var username : String = ""
    private lateinit var tempPhotoUri : Uri

    private fun createImageFile(): File {
        val fileName = "IMG_${System.currentTimeMillis()}"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(fileName, ".jpg", storageDir)
    }

  // For photo capture/selection
   private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
          photoPath = it.toString()
           binding.ivExpensePhoto.setImageURI(uri)
            binding.ivExpensePhoto.visibility = android.view.View.VISIBLE
        }
   }
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
      if (success && photoPath != null) {
           binding.ivExpensePhoto.setImageURI(Uri.parse(photoPath))
            binding.ivExpensePhoto.visibility = android.view.View.VISIBLE
      }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = DatabaseHelper(this)
        userId = intent.getIntExtra("userId", -1)

        setupButtonListeners()
        // Load categories
        val categories = dbHelper.getCategories(userId)
        val categoryNames = categories.map {
            categoryMap[it.name] = it.categoryID
            it.name
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        binding.btnAddPhoto.setOnClickListener {
            showPhotoOptions()
        }

        binding.btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    private fun showPhotoOptions() {
        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Select Photo")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val photoFile = createImageFile()
        tempPhotoUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", photoFile)
        photoPath = tempPhotoUri.toString()
        takePicture.launch(tempPhotoUri)
    }

    private fun openGallery() {
        getContent.launch("image/*")
    }

    private fun saveExpense() {
        val categoryName = binding.spinnerCategory.selectedItem?.toString() ?: run {
            Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
            return
        }
        val categoryId = categoryMap[categoryName] ?: return

        val amount = binding.etAmount.text.toString().toDoubleOrNull()
        val date = binding.etDate.text.toString()
        val expenseName = binding.etName.text.toString()
        val startTime = binding.etStartTime.text.toString()
        val endTime = binding.etEndTime.text.toString()
        val description = binding.etDescription.text.toString()

        if (amount == null || date.isEmpty()) {
            Toast.makeText(this, "Please fill in amount and date", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate time format if times are provided
        if (startTime.isNotEmpty() && !isValidTime(startTime)) {
            Toast.makeText(this, "Invalid start time format (use HH:MM)", Toast.LENGTH_SHORT).show()
            return
        }

        if (endTime.isNotEmpty() && !isValidTime(endTime)) {
            Toast.makeText(this, "Invalid end time format (use HH:MM)", Toast.LENGTH_SHORT).show()
            return
        }

        val success = dbHelper.addExpense(
            userId = userId,
            categoryId = categoryId,
            expenseName = expenseName,
            amount = amount,
            date = date,
            startTime = if (startTime.isEmpty()) null else startTime,
            endTime = if (endTime.isEmpty()) null else endTime,
            description = if (description.isEmpty()) null else description,
            photoPath = photoPath
        )

        if (success) {
            Toast.makeText(this, "Expense added", Toast.LENGTH_SHORT).show()
            clearFields()
        } else {
            Toast.makeText(this, "Failed to save expense", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidTime(time: String): Boolean {
        return try {
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            timeFormat.isLenient = false
            timeFormat.parse(time)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun clearFields() {
        binding.etAmount.text.clear()
        binding.etDate.text.clear()
        binding.etName.text.clear()
        binding.etStartTime.text.clear()
        binding.etEndTime.text.clear()
        binding.etDescription.text.clear()
        binding.ivExpensePhoto.setImageURI(null)
        binding.ivExpensePhoto.visibility = android.view.View.GONE
        photoPath = null
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
        binding.btnCatsummary.setOnClickListener {
            val intent = Intent(this, CategorySummaryActivity::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.etStartTime.setOnClickListener {
            showTimePicker(isStartTime = true)
        }

        binding.etEndTime.setOnClickListener {
            showTimePicker(isStartTime = false)
        }



    }

    private fun startActivityWithUser(activityClass: Class<*>) {
        val intent = Intent(this, activityClass).apply {
            putExtra("userId", userId)
            putExtra("username", username)
        }
        startActivity(intent)
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                // Formatting month and day to always have 2 digits
                val formattedDate = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDayOfMonth)
                binding.etDate.setText(formattedDate)
            },
            year, month, day
        )
        datePickerDialog.show()
    }

    private fun showTimePicker(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this,
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                if (isStartTime) {
                    binding.etStartTime.setText(formattedTime)
                } else {
                    binding.etEndTime.setText(formattedTime)
                }
            },
            hour, minute, true  // 24-hour format = true
        )
        timePickerDialog.show()
    }




}