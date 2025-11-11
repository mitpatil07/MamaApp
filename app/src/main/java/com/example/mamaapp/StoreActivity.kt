package com.example.mamaapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mamaapp.data.MamaDatabase
import com.example.mamaapp.data.MamaRepository
import com.example.mamaapp.data.StoreItem
import com.example.mamaapp.data.Transaction
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch

class StoreActivity : AppCompatActivity() {

    private lateinit var repo: MamaRepository
    private lateinit var username: String
    private lateinit var balanceTv: TextView
    private lateinit var adapter: StoreAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store)

        // Setup toolbar
        val topBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(topBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mama Store"

        username = intent.getStringExtra("username") ?: ""
        repo = MamaRepository(MamaDatabase.getInstance(applicationContext).mamaDao())

        balanceTv = findViewById(R.id.tv_balance)
        val rv = findViewById<RecyclerView>(R.id.rv_store)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = StoreAdapter { item -> purchaseItem(item) }
        rv.adapter = adapter

        loadStore()
    }

    override fun onResume() {
        super.onResume()
        loadStore()
    }

    private fun loadStore() {
        lifecycleScope.launch {
            val items = repo.getStoreItems()
            val user = repo.getUser(username)
            runOnUiThread {
                adapter.submitList(items)
                balanceTv.text = "Tokens: ${user?.tokens ?: 0}"
            }
        }
    }

    private fun purchaseItem(item: StoreItem) {
        lifecycleScope.launch {
            val user = repo.getUser(username)
            if (user == null) {
                runOnUiThread { Toast.makeText(this@StoreActivity, "User not found", Toast.LENGTH_SHORT).show() }
                return@launch
            }
            if (user.tokens < item.cost) {
                runOnUiThread { Toast.makeText(this@StoreActivity, "Not enough tokens", Toast.LENGTH_SHORT).show() }
                return@launch
            }
            user.tokens -= item.cost
            repo.updateUser(user)
            repo.insertTransaction(Transaction(userName = username, description = "Bought ${item.name}", delta = -item.cost))
            runOnUiThread {
                Toast.makeText(this@StoreActivity, "Purchased ${item.name}", Toast.LENGTH_SHORT).show()
                balanceTv.text = "Tokens: ${user.tokens}"
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}