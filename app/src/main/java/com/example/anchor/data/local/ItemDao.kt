package com.example.anchor.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SavedItem)

    @Query("SELECT * FROM items ORDER BY createdAt DESC")
    fun getAll(): Flow<List<SavedItem>>

    @Delete
    suspend fun delete(item: SavedItem)

}