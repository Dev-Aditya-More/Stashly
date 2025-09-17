package com.example.anchor.ui.viewmodels

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anchor.data.local.Bookmark
import com.example.anchor.data.local.ContentType
import com.example.anchor.data.local.SavedItem
import com.example.anchor.data.remote.ItemRepository
import com.example.anchor.jsoup.BookmarkMetaData
import com.example.anchor.jsoup.LinkPreview
import com.example.anchor.jsoup.fetchBookmarkMetadata
import com.example.anchor.jsoup.fetchLinkPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ItemRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    val items: StateFlow<List<SavedItem>> =
        repository.getAllItems()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val favourites: StateFlow<List<SavedItem>> =
        repository.getFavourites()
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun toggleFavourite(item: SavedItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFavourite(item.id, !item.isFavorite)
        }
    }

    fun saveLink(urlItem: SavedItem, fetched: LinkPreview?) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                repository.saveItem(
                    urlItem.copy(
                        title = fetched?.title ?: "Untitled",
                        text = fetched?.description,
                        linkPreview = fetched?.imageUrl,
                        faviconUrl = fetched?.faviconUrl
                    )
                )
            } catch (e: Exception) {
                repository.saveItem(urlItem.copy(title = "Untitled"))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveText(text: SavedItem) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val title = generateTempTitle(text.text)
                repository.saveItem(
                    SavedItem(
                        title = title,
                        text = text.text,
                        contentType = ContentType.TEXT
                    )
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveFile(fileItem: SavedItem, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val uri = fileItem.filePath?.toUri()
                val fileName = uri?.let { getFileName(context, it) } ?: "File"
                repository.saveItem(
                    fileItem.copy(
                        title = fileName,
                        contentType = ContentType.FILE
                    )
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveBookmark(url: String, metadata: BookmarkMetaData?) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val bookmark = Bookmark(
                    url = url,
                    title = metadata?.title ?: url.toUri().host ?: "Untitled",
                    description = metadata?.description ?: "No description available",
                    faviconUrl = metadata?.faviconUrl,
                    previewImage = metadata?.previewImage
                )
                repository.saveItem(bookmark.toSavedItem())
            } catch (_: Exception) {
                repository.saveItem(
                    SavedItem(
                        contentType = ContentType.LINK,
                        url = url,
                        title = "Untitled"
                    )
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun editItem(item: SavedItem) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                repository.editItem(item)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeItem(item: SavedItem) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                repository.removeItem(item)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getItemById(id: Int): Flow<SavedItem?> = repository.getItemById(id)
}


fun generateTempTitle(text: String?, wordLimit: Int = 4): String {
    if (text.isNullOrBlank()) return "Untitled note"

    val cleaned = text
        .trim()
        .replace("\n", " ")       // remove newlines
        .replace("\\s+".toRegex(), " ") // collapse spaces

    // --- Context-aware rules ---

    if (cleaned.startsWith("-") || cleaned.startsWith("•") || cleaned.startsWith("1.")) {
        return "Checklist"
    }

    if (cleaned.contains("fun ") || cleaned.contains("class ") || cleaned.contains("{")) {
        return "Code snippet"
    }

    val dateRegex = "\\d{4}-\\d{2}-\\d{2}".toRegex()
    if (dateRegex.containsMatchIn(cleaned) || cleaned.contains("tomorrow", ignoreCase = true)) {
        return "Reminder"
    }

    if (cleaned.length <= 40) return cleaned

    val words = cleaned.split(" ")
    val base = if (words.size <= wordLimit) {
        cleaned
    } else {
        words.take(wordLimit).joinToString(" ") + "..."
    }

    return base
}


fun getFileName(context: Context, uri: Uri): String {
    var name: String? = null
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        if (it.moveToFirst()) {
            val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (index != -1) {
                name = it.getString(index)
            }
        }
    }
    return name ?: uri.lastPathSegment ?: "File"
}

fun Bookmark.toSavedItem(): SavedItem {
    return SavedItem(
        contentType = ContentType.LINK,
        url = this.url,
        title = this.title ?: "Untitled",
        text = this.description,
        faviconUrl = this.faviconUrl,
        linkPreview = this.previewImage
    )
}