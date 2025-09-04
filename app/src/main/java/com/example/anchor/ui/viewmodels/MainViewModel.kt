package com.example.anchor.ui.viewmodels

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anchor.data.local.ContentType
import com.example.anchor.data.local.SavedItem
import com.example.anchor.data.remote.LinkRepository
import com.example.anchor.jsoup.fetchPageTitle
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: LinkRepository
) : ViewModel() {

    val items: StateFlow<List<SavedItem>> =
        repository.getAllItems()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun saveLink(url: SavedItem) {
        viewModelScope.launch {
            val title = fetchPageTitle(url.url)
            repository.saveItem(
                SavedItem(
                    url = url.url,
                    title = title,
                    contentType = ContentType.LINK
                )
            )
        }
    }

    fun saveText(text: SavedItem) {
        viewModelScope.launch {
            repository.saveItem(
                SavedItem(
                    text = text.text,
                    contentType = ContentType.TEXT
                )
            )
            Log.d("SavedItemDebug", "Saving TEXT item: $text")
        }
    }

    fun saveFile(filePath: SavedItem) {
        viewModelScope.launch {
            val fileName = filePath.filePath?.toUri()?.lastPathSegment ?: "File"

            val item = SavedItem(
                filePath = filePath.toString(),
                title = fileName,
                contentType = ContentType.FILE
            )

            repository.saveItem(item)
        }
    }

    fun removeItem(item: SavedItem) {
        viewModelScope.launch {
            repository.removeItem(item)
        }
    }
}
