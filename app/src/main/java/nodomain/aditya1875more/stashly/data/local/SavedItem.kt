package nodomain.aditya1875more.stashly.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items")
data class SavedItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contentType: ContentType,
    val url: String? = null,       // for LINK
    var title: String? = null,     // optional metadata
    val text: String? = null,      // for TEXT snippets
    val filePath: String? = null,  // local storage reference for FILE
    val linkPreview: String? = null,
    val faviconUrl: String? = null,
    val isFavorite: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)