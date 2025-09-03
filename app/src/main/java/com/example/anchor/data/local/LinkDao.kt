package com.example.anchor.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LinkDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLink(link: LinkEntity)

    @Query("SELECT * FROM links ORDER BY createdAt DESC")
    fun getAllLinks(): Flow<List<LinkEntity>>

    @Delete
    suspend fun deleteLink(link: LinkEntity)

}