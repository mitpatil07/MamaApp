package com.example.mamaapp

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mamaapp.data.MamaDatabase
import com.example.mamaapp.data.MamaRepository
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val usernameInput = findViewById<EditText>(R.id.username_input)
        val passwordInput = findViewById<EditText>(R.id.userpass_input)
        val loginBtn = findViewById<Button>(R.id.login_btn)
        val roleDropdown = findViewById<AutoCompleteTextView>(R.id.role_dropdown)

        // Roles for the exposed dropdown
        val roles = listOf("Farmer", "Operator")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, roles)
        roleDropdown.setAdapter(adapter)
        roleDropdown.setText(roles[0], false) // default selection

        // default credentials for quick demo/testing
        usernameInput.setText("admin")
        passwordInput.setText("admin")

        val repo = MamaRepository(MamaDatabase.getInstance(applicationContext).mamaDao())

        loginBtn.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val role = roleDropdown.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (role.isEmpty()) {
                Toast.makeText(this, "Please choose a role", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // For demo: we accept any credentials and create the local user entry
            lifecycleScope.launch {
                repo.createUserIfNotExists(username, role)
                repo.insertDefaultStoreItemsIfEmpty()

                // Navigate to correct dashboard
                val intent = if (role.equals("Operator", ignoreCase = true)) {
                    Intent(this@MainActivity, OperatorActivity::class.java)
                } else {
                    Intent(this@MainActivity, FarmerActivity::class.java)
                }
                intent.putExtra("username", username)
                startActivity(intent)
                finish()
            }
        }
    }
}
