package nodomain.aditya1875more.stashly.utils

import android.util.Patterns
import nodomain.aditya1875more.stashly.data.local.ContentType

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

/**
 * Pulls the first http(s) link out of shared text. Apps like YouTube, Reddit and X
 * share "Some title https://..." rather than a bare URL, so a plain match isn't enough.
 */
fun extractUrl(input: String): String? {
    val trimmed = input.trim()
    if (classifyInput(trimmed) == ContentType.LINK) return normalizeUrl(trimmed)

    val matcher = Patterns.WEB_URL.matcher(trimmed)
    while (matcher.find()) {
        val candidate = matcher.group()
        if (candidate.startsWith("http://") || candidate.startsWith("https://")) {
            return candidate
        }
    }
    return null
}
