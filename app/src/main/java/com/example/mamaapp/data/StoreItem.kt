package com.example.mamaapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "storeitems")
data class StoreItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val cost: Int
)
