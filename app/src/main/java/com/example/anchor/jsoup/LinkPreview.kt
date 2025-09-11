package com.example.anchor.jsoup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import androidx.core.net.toUri

suspend fun fetchLinkPreview(url: String): LinkPreview? = withContext(Dispatchers.IO) {
    try {
        val doc = Jsoup.connect(url).get()

        val title = doc.select("meta[property=og:title]").attr("content")
            .ifBlank { doc.title() }

        val description = doc.select("meta[property=og:description]").attr("content")
            .ifBlank { doc.select("meta[name=description]").attr("content") }

        val image = doc.select("meta[property=og:image]").attr("content")

        LinkPreview(
            title = title.ifBlank { null },
            description = description.ifBlank { null },
            imageUrl = image.ifBlank { null },
            url = url
        )
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

data class LinkPreview(
    val title: String?,
    val description: String?,
    val imageUrl: String?,
    val url: String
)
