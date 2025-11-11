package com.example.mamaapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mamaapp.data.FarmData
import com.example.mamaapp.data.MamaDatabase
import com.example.mamaapp.data.MamaRepository
import com.example.mamaapp.data.Transaction
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class FarmerActivity : AppCompatActivity() {
    private lateinit var repo: MamaRepository
    private lateinit var username: String

    // UI refs
    private lateinit var balanceTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_farmer)

        val topBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(topBar)

        username = intent.getStringExtra("username") ?: ""
        repo = MamaRepository(MamaDatabase.getInstance(applicationContext).mamaDao())

        // UI elements
        val cropEt = findViewById<EditText>(R.id.et_crop)
        val issueEt = findViewById<EditText>(R.id.et_issue)
        val dateEt = findViewById<EditText>(R.id.et_date)
        val submitBtn = findViewById<Button>(R.id.btn_submit_data)
        balanceTv = findViewById<TextView>(R.id.tv_balance)
        val openStoreBtn = findViewById<Button>(R.id.btn_open_store_farmer)

        // initial balance load
        refreshBalance()

        submitBtn.setOnClickListener {
            val crop = cropEt.text.toString().trim()
            val issue = issueEt.text.toString().trim()
            val date = dateEt.text.toString().trim()
            if (crop.isEmpty() || issue.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                repo.addFarmData(FarmData(farmerName = username, cropType = crop, issue = issue, date = date))
                val user = repo.getUser(username)
                if (user != null) {
                    user.tokens += 5
                    repo.updateUser(user)
                    repo.insertTransaction(Transaction(userName = username, description = "Farm data +5", delta = +5))
                }
                runOnUiThread {
                    Toast.makeText(this@FarmerActivity, "Data submitted. +5 tokens", Toast.LENGTH_SHORT).show()
                    balanceTv.text = "Tokens: ${user?.tokens ?: 0}"
                    cropEt.text.clear()
                    issueEt.text.clear()
                    dateEt.text.clear()
                }
            }
        }

        openStoreBtn.setOnClickListener {
            val intent = Intent(this, StoreActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshBalance()
    }

    private fun refreshBalance() {
        lifecycleScope.launch {
            val u = repo.getUser(username)
            runOnUiThread {
                balanceTv.text = "Tokens: ${u?.tokens ?: 0}"
            }
        }
    }

    // Inflate the menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return true
    }

    // Handle toolbar menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                intent.putExtra("username", username)
                startActivity(intent)
                true
            }
            R.id.menu_logout -> {
                val it = Intent(this, MainActivity::class.java)
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}