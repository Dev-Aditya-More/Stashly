package com.example.anchor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LinkEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun linkDao(): LinkDao
}
