package com.example.anchor.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "links")
data class LinkEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val url: String?,
    val title: String? = null,
    val summary: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)