package com.example.anchor.jsoup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

suspend fun fetchPageTitle(url: String?): String? = withContext(Dispatchers.IO) {
    try {
        val document = Jsoup.connect(url.toString()).get()
        document.title().takeIf { it.isNotBlank() }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
