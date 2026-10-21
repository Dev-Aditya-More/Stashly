package nodomain.aditya1875more.stashly.utils

import nodomain.aditya1875more.stashly.data.local.SavedItem

/**
 * True when every word of [query] appears somewhere in the item's title, link, text or file path.
 * Matching per word means "kotlin flow" finds "Flow basics — Kotlin docs".
 */
fun SavedItem.matchesQuery(query: String): Boolean {
    val words = query.trim().split("\\s+".toRegex()).filter { it.isNotEmpty() }
    if (words.isEmpty()) return true

    val haystack = listOfNotNull(title, url, text, filePath).joinToString(" ")
    return words.all { haystack.contains(it, ignoreCase = true) }
}
