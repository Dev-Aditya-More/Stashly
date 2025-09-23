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
