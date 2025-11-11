package com.example.mamaapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MamaDao {

    // Users
    @Query("SELECT * FROM users WHERE name = :name LIMIT 1")
    suspend fun getUser(name: String): User?

    @Insert
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>

    // FarmData
    @Insert
    suspend fun insertFarmData(farmData: FarmData)

    @Query("SELECT * FROM farmdata WHERE farmerName = :farmerName")
    suspend fun getFarmDataByFarmer(farmerName: String): List<FarmData>

    @Query("SELECT * FROM farmdata")
    suspend fun getAllFarmData(): List<FarmData>

    // Store items
    @Insert
    suspend fun insertStoreItem(item: StoreItem)

    @Query("SELECT * FROM storeitems")
    suspend fun getAllStoreItems(): List<StoreItem>

    // Transactions
    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE userName = :userName")
    suspend fun getTransactionsByUser(userName: String): List<Transaction>

    @Query("SELECT * FROM transactions")
    suspend fun getAllTransactions(): List<Transaction>

    // Jobs (example)
    @Insert
    suspend fun insertJob(job: Job)

    @Query("SELECT * FROM jobs WHERE operatorName = :operator")
    suspend fun getJobsByOperator(operator: String): List<Job>

    @Query("SELECT * FROM jobs")
    suspend fun getAllJobs(): List<Job>
}
