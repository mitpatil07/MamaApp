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
import com.example.mamaapp.data.Job
import com.example.mamaapp.data.MamaDatabase
import com.example.mamaapp.data.MamaRepository
import com.example.mamaapp.data.Transaction
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class OperatorActivity : AppCompatActivity() {

    private lateinit var repo: MamaRepository
    private lateinit var username: String

    // UI refs
    private lateinit var balanceTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_operator)

        val topBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(topBar)

        username = intent.getStringExtra("username") ?: ""
        repo = MamaRepository(MamaDatabase.getInstance(applicationContext).mamaDao())

        val farmerNameEt = findViewById<EditText>(R.id.et_farmer_name)
        val farmSizeEt = findViewById<EditText>(R.id.et_farm_size)
        val dateEt = findViewById<EditText>(R.id.et_date)
        val submitBtn = findViewById<Button>(R.id.btn_record_job)
        balanceTv = findViewById<TextView>(R.id.tv_balance)
        val openStoreBtn = findViewById<Button>(R.id.btn_open_store)

        // load and show tokens
        refreshBalance()

        submitBtn.setOnClickListener {
            val farmer = farmerNameEt.text.toString().trim()
            val farmSize = farmSizeEt.text.toString().trim()
            val date = dateEt.text.toString().trim()
            if (farmer.isEmpty() || farmSize.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                // insert job
                repo.addJob(Job(farmerName = farmer, farmSize = farmSize, date = date, operatorName = username))

                // add tokens +10
                val user = repo.getUser(username)
                if (user != null) {
                    user.tokens += 10
                    repo.updateUser(user)
                    repo.insertTransaction(Transaction(userName = username, description = "Spray job +10", delta = +10))
                }

                runOnUiThread {
                    Toast.makeText(this@OperatorActivity, "Job recorded. +10 tokens", Toast.LENGTH_SHORT).show()
                    balanceTv.text = "Tokens: ${user?.tokens ?: 0}"
                    farmerNameEt.text.clear()
                    farmSizeEt.text.clear()
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
        // refresh balance when returning to this activity
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
                val it = Intent(this, ProfileActivity::class.java)
                it.putExtra("username", username)
                startActivity(it)
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