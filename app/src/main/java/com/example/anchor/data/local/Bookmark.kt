package com.example.anchor.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Bookmark(
    val url: String,
    val title: String?,
    val description: String?,
    val faviconUrl: String?,
    val previewImage: String? = null
)
