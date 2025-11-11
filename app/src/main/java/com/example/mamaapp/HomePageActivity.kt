package com.example.mamaapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class omePageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        // Get username passed from login
        val username = intent.getStringExtra("username")
        val welcomeText = findViewById<TextView>(R.id.welcome_text)
        welcomeText.text = "Welcome, $username 👋"
    }
}
