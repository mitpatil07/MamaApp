package com.example.mamaapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [User::class, FarmData::class, StoreItem::class, Transaction::class, Job::class],
    version = 2,   // increment if you change entities
    exportSchema = true
)
abstract class MamaDatabase : RoomDatabase() {

    abstract fun mamaDao(): MamaDao

    companion object {
        @Volatile private var INSTANCE: MamaDatabase? = null

        fun getInstance(context: Context): MamaDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    MamaDatabase::class.java,
                    "mama_db"
                ).fallbackToDestructiveMigration() // during dev, ok — remove for production
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
