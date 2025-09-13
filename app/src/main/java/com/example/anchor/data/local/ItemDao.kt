package com.example.anchor.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SavedItem)

    @Upsert
    suspend fun upsert(item: SavedItem)

    @Delete
    suspend fun delete(item: SavedItem)

    @Query("SELECT * FROM items ORDER BY createdAt DESC")
    fun getAll(): Flow<List<SavedItem>>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    fun getItemById(id: Int): Flow<SavedItem?>

    @Query("SELECT * FROM items WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavourites(): Flow<List<SavedItem>>

    @Query("UPDATE items SET isFavorite = :isFavourite WHERE id = :id")
    suspend fun toggleFavourite(id: Int, isFavourite: Boolean)
}