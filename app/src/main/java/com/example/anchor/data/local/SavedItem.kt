package com.example.anchor.data.local

data class SavedItem(
    val url: String,
    val title: String? = null,
    val type: ContentType = ContentType.LINK
)
