package com.example.anchor.data.remote

import android.content.Context
import com.example.anchor.data.local.ItemDao
import com.example.anchor.data.local.SavedItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface LinkRepository {
    suspend fun saveItem(item: SavedItem)
    fun getAllItems(): Flow<List<SavedItem>>
    suspend fun removeItem(item: SavedItem)
    suspend fun editItem(item: SavedItem)
}

class LinkRepositoryImpl(
    private val dao: ItemDao
) : LinkRepository {
    override suspend fun saveItem(item: SavedItem) {
        dao.insert(item)
    }

    override fun getAllItems(): Flow<List<SavedItem>> =
        dao.getAll().map { entities -> entities.map { it } }

    override suspend fun removeItem(item: SavedItem) {
        dao.delete(item)
    }
    override suspend fun editItem(item: SavedItem) {
        dao.update(item)
    }
}
