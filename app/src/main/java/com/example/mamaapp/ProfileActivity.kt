package com.example.mamaapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mamaapp.data.MamaDatabase
import com.example.mamaapp.data.MamaRepository
import com.example.mamaapp.data.Transaction
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private lateinit var repo: MamaRepository
    private lateinit var username: String
    private lateinit var tvName: TextView
    private lateinit var tvRole: TextView
    private lateinit var tvWallet: TextView
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // Set up toolbar
        val topBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(topBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        username = intent.getStringExtra("username") ?: ""
        repo = MamaRepository(MamaDatabase.getInstance(applicationContext).mamaDao())

        tvName = findViewById(R.id.tv_user_name)
        tvRole = findViewById(R.id.tv_user_role)
        tvWallet = findViewById(R.id.chip_wallet)
        val rv = findViewById<RecyclerView>(R.id.rv_transactions)
        val btnTopup = findViewById<Button>(R.id.btn_topup)
        val btnEdit = findViewById<Button>(R.id.btn_edit_name)
        val btnLogout = findViewById<Button>(R.id.btn_logout)

        adapter = TransactionAdapter()
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        loadProfile()

        btnTopup.setOnClickListener {
            lifecycleScope.launch {
                val user = repo.getUser(username)
                if (user != null) {
                    user.tokens += 50
                    repo.updateUser(user)
                    repo.insertTransaction(Transaction(userName = username, description = "Top-up +50", delta = +50))
                    runOnUiThread {
                        Toast.makeText(this@ProfileActivity, "Top-up successful", Toast.LENGTH_SHORT).show()
                        loadProfile()
                    }
                }
            }
        }

        btnEdit.setOnClickListener {
            val edit = android.widget.EditText(this)
            edit.setText(username)
            AlertDialog.Builder(this)
                .setTitle("Edit name")
                .setView(edit)
                .setPositiveButton("Save") { _, _ ->
                    val newName = edit.text.toString().trim()
                    if (newName.isNotEmpty()) {
                        lifecycleScope.launch {
                            val user = repo.getUser(username)
                            if (user != null) {
                                val newUser = user.copy(name = newName)
                                repo.createUserIfNotExists(newName, newUser.role)
                                repo.updateUser(newUser)
                                runOnUiThread {
                                    Toast.makeText(this@ProfileActivity, "Name changed to $newName", Toast.LENGTH_SHORT).show()
                                    val it = Intent(this@ProfileActivity, MainActivity::class.java)
                                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(it)
                                    finish()
                                }
                            }
                        }
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnLogout.setOnClickListener {
            val it = Intent(this, MainActivity::class.java)
            it.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(it)
            finish()
        }
    }

    private fun loadProfile() {
        lifecycleScope.launch {
            val user = repo.getUser(username)
            val txs = repo.getTransactions(username)
            runOnUiThread {
                tvName.text = "Name: ${user?.name ?: "-"}"
                tvRole.text = "Role: ${user?.role ?: "-"}"
                tvWallet.text = "💰 Tokens: ${user?.tokens ?: 0}"
                adapter.submitList(txs)
            }
        }
    }

    // Handle toolbar menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.menu_profile -> {
                // Already in profile
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