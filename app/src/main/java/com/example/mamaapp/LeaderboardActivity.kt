package com.example.mamaapp

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mamaapp.data.LeaderboardEntry
import com.example.mamaapp.data.MamaDatabase
import com.example.mamaapp.data.MamaRepository
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.launch
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class LeaderboardActivity : AppCompatActivity() {

    private lateinit var repo: MamaRepository
    private lateinit var rvLeaderboard: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leaderboard)

        val topBar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(topBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "🏆 Leaderboard"

        repo = MamaRepository(MamaDatabase.getInstance(applicationContext).mamaDao())
        rvLeaderboard = findViewById(R.id.rv_leaderboard)
        rvLeaderboard.layoutManager = LinearLayoutManager(this)

        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        lifecycleScope.launch {
            val leaderboard = repo.getLeaderboard()
            runOnUiThread {
                rvLeaderboard.adapter = LeaderboardAdapter(leaderboard)
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

class LeaderboardAdapter(private val entries: List<LeaderboardEntry>) :
    RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRank: TextView = view.findViewById(R.id.tv_rank)
        val tvName: TextView = view.findViewById(R.id.tv_name)
        val tvTokens: TextView = view.findViewById(R.id.tv_tokens)
        val tvRole: TextView = view.findViewById(R.id.tv_role)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        val rank = position + 1

        holder.tvRank.text = when (rank) {
            1 -> "🥇"
            2 -> "🥈"
            3 -> "🥉"
            else -> "#$rank"
        }

        holder.tvName.text = entry.userName
        holder.tvTokens.text = "${entry.tokens} tokens"
        holder.tvRole.text = if (entry.role == "Farmer") "👨‍🌾" else "🚜"
    }

    override fun getItemCount() = entries.size
}