package com.example.anchor.utils

import android.util.Patterns
import com.example.anchor.data.local.ContentType

fun classifyInput(input: String): ContentType {
    return if (Patterns.WEB_URL.matcher(input).matches()) {
        ContentType.LINK
    } else {
        ContentType.TEXT
    }
}


fun normalizeUrl(input: String): String {
    return if (!input.startsWith("http://") && !input.startsWith("https://")) {
        "https://$input"
    } else input
}
