package com.example.anchor.ui.viewmodels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anchor.data.local.ContentType
import com.example.anchor.data.local.SavedItem
import com.example.anchor.data.remote.ItemRepository
import com.example.anchor.jsoup.fetchLinkPreview
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: ItemRepository
) : ViewModel() {

    val items: StateFlow<List<SavedItem>> =

        repository.getAllItems()
            .flowOn(Dispatchers.IO)
            .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun saveLink(urlItem: SavedItem) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val preview = fetchLinkPreview(urlItem.url ?: "")
                repository.saveItem(
                    urlItem.copy(
                        title = preview?.title ?: "Untitled",
                        text = preview?.description,
                        linkPreview = preview?.imageUrl,
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                repository.saveItem(
                    urlItem.copy(title = "Untitled")
                )
            }
        }
    }


    fun saveText(text: SavedItem) {
        viewModelScope.launch(Dispatchers.IO) {

            val title = generateTempTitle(text.text)
            val item = SavedItem(
                title = title ?: "Untitled",
                text = text.text,
                contentType = ContentType.TEXT
            )
            repository.saveItem(item)
        }
    }

    fun saveFile(fileItem: SavedItem, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val uri = fileItem.filePath?.toUri()
            val fileName = uri?.let { getFileName(context,it) } ?: "File"

            val item = SavedItem(
                filePath = fileItem.filePath,
                title = fileName,
                contentType = ContentType.FILE
            )

            repository.saveItem(item)
        }
    }

    fun editItem(item: SavedItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.editItem(item)
        }
    }

    fun removeItem(item: SavedItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeItem(item)
        }
    }
    fun getItemById(id: Int): Flow<SavedItem?> {
        return repository.getItemById(id)
    }
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
