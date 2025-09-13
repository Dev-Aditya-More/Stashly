package com.example.anchor.jsoup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import androidx.core.net.toUri
import java.net.URL

suspend fun fetchLinkPreview(url: String): LinkPreview? = withContext(Dispatchers.IO) {
    try {
        val doc = Jsoup.connect(url).get()

        val title = doc.select("meta[property=og:title]").attr("content")
            .ifBlank { doc.title() }

        val description = doc.select("meta[property=og:description]").attr("content")
            .ifBlank { doc.select("meta[name=description]").attr("content") }

        val image = doc.select("meta[property=og:image]").attr("content")

        // Try common favicon selectors
        var favicon = doc.select("link[rel=icon]").attr("href")
        if (favicon.isBlank()) {
            favicon = doc.select("link[rel=shortcut icon]").attr("href")
        }
        if (favicon.isBlank()) {
            favicon = doc.select("link[rel=apple-touch-icon]").attr("href")
        }

        // Resolve relative URLs → make absolute
        if (favicon.isNotBlank() && !favicon.startsWith("http")) {
            val baseUri = doc.baseUri()
            favicon = URL(URL(baseUri), favicon).toString()
        }

        // Fallback to default /favicon.ico
        if (favicon.isBlank()) {
            val domain = URL(url).protocol + "://" + URL(url).host
            favicon = "$domain/favicon.ico"
        }

        LinkPreview(
            title = title.ifBlank { null },
            description = description.ifBlank { null },
            imageUrl = image.ifBlank { null },
            faviconUrl = favicon.ifBlank { null },
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
    val faviconUrl: String?,
    val url: String
)
