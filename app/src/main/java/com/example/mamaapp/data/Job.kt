package com.example.mamaapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class Job(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val farmerName: String,
    val farmSize: String,
    val date: String,
    val operatorName: String
)
