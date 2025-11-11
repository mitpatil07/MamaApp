package com.example.mamaapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farmdata")
data class FarmData(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmerName: String,
    val cropType: String?,
    val issue: String?,
    val date: String?,
    val timestamp: Long = System.currentTimeMillis()
)
