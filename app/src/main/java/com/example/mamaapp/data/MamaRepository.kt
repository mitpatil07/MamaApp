package com.example.mamaapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

class MamaRepository(private val dao: MamaDao) {

    // User operations
    suspend fun createUserIfNotExists(name: String, role: String) = withContext(Dispatchers.IO) {
        val existing = dao.getUser(name)
        if (existing == null) {
            dao.insertUser(User(name = name, role = role, tokens = 0))
        }
    }

    suspend fun getUser(name: String): User? = withContext(Dispatchers.IO) {
        dao.getUser(name)
    }

    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        dao.updateUser(user)
    }

    suspend fun getAllUsers(): List<User> = withContext(Dispatchers.IO) {
        dao.getAllUsers()
    }

    // Farm data operations
    suspend fun addFarmData(farmData: FarmData) = withContext(Dispatchers.IO) {
        dao.insertFarmData(farmData)
    }

    suspend fun getFarmDataByFarmer(farmerName: String): List<FarmData> = withContext(Dispatchers.IO) {
        dao.getFarmDataByFarmer(farmerName)
    }

    suspend fun getAllFarmData(): List<FarmData> = withContext(Dispatchers.IO) {
        dao.getAllFarmData()
    }

    // Job operations
    suspend fun addJob(job: Job) = withContext(Dispatchers.IO) {
        dao.insertJob(job)
    }

    suspend fun getJobsByOperator(operatorName: String): List<Job> = withContext(Dispatchers.IO) {
        dao.getJobsByOperator(operatorName)
    }

    suspend fun getAllJobs(): List<Job> = withContext(Dispatchers.IO) {
        dao.getAllJobs()
    }

    // Transaction operations
    suspend fun insertTransaction(transaction: Transaction) = withContext(Dispatchers.IO) {
        dao.insertTransaction(transaction)
    }

    suspend fun getTransactions(userName: String): List<Transaction> = withContext(Dispatchers.IO) {
        dao.getTransactionsByUser(userName)
    }

    suspend fun getAllTransactions(): List<Transaction> = withContext(Dispatchers.IO) {
        dao.getAllTransactions()
    }

    // Store operations
    suspend fun getStoreItems(): List<StoreItem> = withContext(Dispatchers.IO) {
        dao.getAllStoreItems()
    }

    suspend fun insertDefaultStoreItemsIfEmpty() = withContext(Dispatchers.IO) {
        if (dao.getAllStoreItems().isEmpty()) {
            val defaultItems = listOf(
                StoreItem(name = "Fertilizer Bag", cost = 25),
                StoreItem(name = "Seeds Pack", cost = 15),
                StoreItem(name = "Pesticide Spray", cost = 30),
                StoreItem(name = "Farm Tools Kit", cost = 50),
                StoreItem(name = "Water Pump", cost = 100),
                StoreItem(name = "Irrigation Pipe", cost = 40),
                StoreItem(name = "Gardening Gloves", cost = 10),
                StoreItem(name = "Premium Fertilizer", cost = 45)
            )
            defaultItems.forEach { dao.insertStoreItem(it) }
        }
    }

    // Analytics and stats
    suspend fun getUserStats(userName: String): UserStats = withContext(Dispatchers.IO) {
        val user = dao.getUser(userName)
        val transactions = dao.getTransactionsByUser(userName)

        // avoid sumOf ambiguity by using fold
        val totalEarned = transactions.filter { it.delta > 0 }
            .fold(0) { acc, t -> acc + t.delta }

        val totalSpent = transactions.filter { it.delta < 0 }
            .fold(0) { acc, t -> acc + abs(t.delta) }

        UserStats(
            userName = userName,
            currentBalance = user?.tokens ?: 0,
            totalEarned = totalEarned,
            totalSpent = totalSpent,
            transactionCount = transactions.size
        )
    }

    suspend fun getLeaderboard(): List<LeaderboardEntry> = withContext(Dispatchers.IO) {
        dao.getAllUsers()
            .sortedByDescending { it.tokens }
            .take(10)
            .map { LeaderboardEntry(it.name, it.tokens, it.role) }
    }
}

// Data classes for analytics
data class UserStats(
    val userName: String,
    val currentBalance: Int,
    val totalEarned: Int,
    val totalSpent: Int,
    val transactionCount: Int
)

data class LeaderboardEntry(
    val userName: String,
    val tokens: Int,
    val role: String
)
