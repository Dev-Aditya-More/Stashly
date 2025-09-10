package com.example.anchor.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class SavedItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contentType: ContentType,
    val url: String? = null,       // for LINK
    var title: String? = null,     // optional metadata
    val text: String? = null,      // for TEXT snippets
    val filePath: String? = null,  // local storage reference for FILE
    val createdAt: Long = System.currentTimeMillis()
)