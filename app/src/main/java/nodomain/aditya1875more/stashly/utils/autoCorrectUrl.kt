package nodomain.aditya1875more.stashly.utils

fun autoCorrectUrl(input: String): String {
    var url = input.trim().replace("\\s+".toRegex(), "")

    // Split into [scheme+host] and [path+query+fragment]
    val parts = url.split("/", limit = 4) // e.g. ["https:", "", "github.com", "Dev-Aditya-More/..."]

    if (parts.size >= 3) {
        val scheme = parts[0].lowercase() // "https:"
        val empty = parts[1] // "" after //
        val host = parts[2].lowercase()   // "github.com"
        val rest = if (parts.size == 4) parts[3] else ""

        url = buildString {
            append(scheme).append("//").append(host)
            if (rest.isNotEmpty()) append("/").append(rest)
        }
    }

    // Common corrections
    url = url
        .replace(".con", ".com")
        .replace(".cm", ".com")
        .replace("htp://", "http://")
        .replace("htttp://", "http://")
        .replace("htpps://", "https://")

    // If it doesn’t start with http/https, prepend https
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
        url = "https://$url"
    }

    return url
}
