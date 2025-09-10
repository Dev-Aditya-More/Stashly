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
import com.example.anchor.jsoup.fetchPageTitle
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
                val title = fetchPageTitle(urlItem.url)
                repository.saveItem(
                    SavedItem(
                        url = urlItem.url,
                        title = title ?: "Untitled",
                        contentType = ContentType.LINK
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
                repository.saveItem(
                    SavedItem(
                        url = urlItem.url,
                        title = "Untitled",
                        contentType = ContentType.LINK
                    )
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
    val words = text?.trim()?.split("\\s+".toRegex())
    return words?.size?.let {
        if (it <= wordLimit) {
            text
        } else {
            words.take(wordLimit)?.joinToString(" ") + "..."
        }
    }.toString()
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
