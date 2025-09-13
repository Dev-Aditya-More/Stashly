package com.example.anchor.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.anchor.utils.ContentTypeConverter

@Database(entities = [SavedItem::class], version = 6, exportSchema = false)
@TypeConverters(ContentTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
}
