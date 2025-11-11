package com.example.mamaapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userName: String,
    val description: String?,
    val delta: Int,
    val timestamp: Long = System.currentTimeMillis()
)
