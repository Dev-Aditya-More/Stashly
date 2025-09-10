package com.example.anchor.data.remote

import android.content.Context
import com.example.anchor.data.local.ItemDao
import com.example.anchor.data.local.SavedItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ItemRepository {
    suspend fun saveItem(item: SavedItem)
    fun getAllItems(): Flow<List<SavedItem>>
    suspend fun removeItem(item: SavedItem)
    suspend fun editItem(item: SavedItem)

    fun getItemById(id: Int): Flow<SavedItem?>
}

class ItemRepositoryImpl(
    private val dao: ItemDao
) : ItemRepository {
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
    override fun getItemById(id: Int): Flow<SavedItem?> = dao.getItemById(id)
}
