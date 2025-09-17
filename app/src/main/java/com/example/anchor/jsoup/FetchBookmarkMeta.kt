package com.example.anchor.jsoup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import androidx.core.net.toUri

suspend fun fetchBookmarkMetadata(url: String): BookmarkMetaData? {
    return try {
        val doc = withContext(Dispatchers.IO) { Jsoup.connect(url).get() }

        val title = cleanTitle(
            doc.select("meta[property=og:title]").attr("content")
                .ifBlank { doc.title() }
                .ifBlank { url.toUri().host ?: url }
        )

        val description = doc.select("meta[name=description]").attr("content")
            .ifBlank { doc.select("meta[property=og:description]").attr("content") }
            .ifBlank { doc.select("p").firstOrNull()?.text() ?: "No description available" }

        val cleanDescription = cleanDescription(description)

        val favicon = doc.select("link[rel~=(?i)^(shortcut|icon|favicon)]").attr("href")
            .ifBlank { "/favicon.ico" }

        val previewImage = doc.select("meta[property=og:image]").attr("content")

        BookmarkMetaData(
            url = url,
            title = title,
            description = cleanDescription,
            faviconUrl = if (favicon.startsWith("http")) favicon
            else url.toUri().scheme + "://" + url.toUri().host + favicon,
            previewImage = previewImage
        )
    } catch (_: Exception) {
        BookmarkMetaData(
            url = url,
            title = url.toUri().host ?: url,
            description = "No description available",
            faviconUrl = null,
            previewImage = null
        )
    }
}

fun cleanTitle(raw: String): String {
    return raw
        .replace(Regex("\\s*-\\s*YouTube$"), "")
        .replace(Regex("\\s*-\\s*Twitter$"), "")
        .trim()
}
fun cleanDescription(raw: String?): String {
    return raw?.replace(Regex("\\s+"), " ")?.trim().orEmpty()
}



data class BookmarkMetaData(
    val url: String,
    val title: String?,
    val description: String?,
    val faviconUrl: String?,
    val previewImage: String? = null
)
