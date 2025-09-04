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
import com.example.anchor.openai.generateTitleSuspend
import com.example.anchor.utils.Constants.apiKey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: LinkRepository
) : ViewModel() {

    val items: StateFlow<List<SavedItem>> =

        repository.getAllItems()
            .flowOn(Dispatchers.IO)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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
            val title = generateTitleSuspend(
                text.text ?: "",
                apiKey
            )

            repository.saveItem(
                SavedItem(
                    title = title,
                    text = text.text,
                    contentType = ContentType.TEXT
                )
            )
            Log.d("SavedItemDebug", "Saved TEXT item: $text")
        }
    }

    fun saveFile(fileItem: SavedItem) {
        viewModelScope.launch(Dispatchers.IO) {
            val fileName = fileItem.filePath?.toUri()?.lastPathSegment ?: "File"

            val item = SavedItem(
                filePath = fileItem.filePath,
                title = fileName,
                contentType = ContentType.FILE
            )

            repository.saveItem(item)
        }
    }


    fun removeItem(item: SavedItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeItem(item)
        }
    }
}
