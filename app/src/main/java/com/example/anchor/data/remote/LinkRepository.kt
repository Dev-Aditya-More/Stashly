package com.example.anchor.data.remote

import com.example.anchor.data.local.LinkDao
import com.example.anchor.data.local.LinkEntity
import kotlinx.coroutines.flow.Flow

interface LinkRepository {
    suspend fun saveLink(url: String)
    fun getAllLinks(): Flow<List<LinkEntity>>

    suspend fun removeLink(link: LinkEntity)
}

class LinkRepositoryImpl(
    private val dao: LinkDao
) : LinkRepository {
    override suspend fun saveLink(url: String) {
        dao.insertLink(LinkEntity(url = url))
    }

    override fun getAllLinks(): Flow<List<LinkEntity>> = dao.getAllLinks()

    override suspend fun removeLink(link: LinkEntity) {
        dao.deleteLink(link)
    }

}