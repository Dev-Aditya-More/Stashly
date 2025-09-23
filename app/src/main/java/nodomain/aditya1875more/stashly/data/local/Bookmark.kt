package nodomain.aditya1875more.stashly.data.local

data class Bookmark(
    val url: String,
    val title: String?,
    val description: String?,
    val faviconUrl: String?,
    val previewImage: String? = null
)
