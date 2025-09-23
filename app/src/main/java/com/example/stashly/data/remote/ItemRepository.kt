package com.example.stashly.data.remote

import com.example.stashly.data.local.ItemDao
import com.example.stashly.data.local.SavedItem
import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    suspend fun saveItem(item: SavedItem)
    suspend fun removeItem(item: SavedItem)
    suspend fun editItem(item: SavedItem)
    fun getAllItems(): Flow<List<SavedItem>>
    fun getItemById(id: Int): Flow<SavedItem?>
    fun getFavourites(): Flow<List<SavedItem>>
    suspend fun toggleFavourite(id: Int, isFavourite: Boolean)
}

class ItemRepositoryImpl(
    private val dao: ItemDao
) : ItemRepository {

    override suspend fun saveItem(item: SavedItem) = dao.insert(item)

    override suspend fun removeItem(item: SavedItem) = dao.delete(item)

    override suspend fun editItem(item: SavedItem) = dao.upsert(item)

    override fun getAllItems(): Flow<List<SavedItem>> = dao.getAll()

    override fun getItemById(id: Int): Flow<SavedItem?> = dao.getItemById(id)

    override fun getFavourites(): Flow<List<SavedItem>> = dao.getFavourites()

    override suspend fun toggleFavourite(id: Int, isFavourite: Boolean) =
        dao.toggleFavourite(id, isFavourite)
}

