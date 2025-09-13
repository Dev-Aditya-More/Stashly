package com.example.anchor.utils

fun autoCorrectUrl(input: String): String {
    var url = input.trim().lowercase()

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
