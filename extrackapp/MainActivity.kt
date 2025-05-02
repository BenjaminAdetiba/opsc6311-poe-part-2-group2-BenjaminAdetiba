package com.example.extrackapp


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.extrackapp.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var userId: Int = -1
    private var username: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user info from login
        userId = intent.getIntExtra("userId", -1)
        username = intent.getStringExtra("username") ?: ""

        binding.tvWelcome.text = "Welcome, $username 👋"

     binding.btnViewReport.setOnClickListener {
          val intent = Intent(this, ViewReportActivity::class.java)
          intent.putExtra("userId", userId)
          startActivity(intent)
      }


    }
}