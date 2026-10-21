package nodomain.aditya1875more.stashly.ui.viewmodels

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import nodomain.aditya1875more.stashly.data.local.ContentType
import nodomain.aditya1875more.stashly.data.local.SavedItem
import nodomain.aditya1875more.stashly.data.remote.ItemRepository
import nodomain.aditya1875more.stashly.jsoup.BookmarkMetaData
import nodomain.aditya1875more.stashly.jsoup.fetchBookmarkMetadata
import nodomain.aditya1875more.stashly.utils.extractUrl

sealed interface ShareSaveState {
    data object Idle : ShareSaveState
    data object Saving : ShareSaveState
    data object Success : ShareSaveState
    data object AlreadySaved : ShareSaveState
    data class Error(val message: String) : ShareSaveState
}

// Keep the share sheet snappy — a slow site shouldn't hold the user hostage
private const val METADATA_TIMEOUT_MS = 6_000

class ShareViewModel(private val repository: ItemRepository) : ViewModel() {

    private val _state = MutableStateFlow<ShareSaveState>(ShareSaveState.Idle)
    val state: StateFlow<ShareSaveState> = _state.asStateFlow()

    fun save(input: String, subject: String? = null) {
        // The ViewModel outlives rotation, so a recreated activity must not save twice
        if (_state.value != ShareSaveState.Idle) return
        // Set Saving synchronously so the first composition never sees Idle
        _state.value = ShareSaveState.Saving
        viewModelScope.launch {
            try {
                val url = extractUrl(input)
                if (url != null) {
                    if (repository.getItemByUrl(url) != null) {
                        _state.value = ShareSaveState.AlreadySaved
                        return@launch
                    }
                    val meta = fetchBookmarkMetadata(url, timeoutMs = METADATA_TIMEOUT_MS)
                    repository.saveItem(
                        meta?.toSavedItem(fallbackTitle = subject) ?: SavedItem(
                            contentType = ContentType.LINK,
                            url = url,
                            title = subject?.takeIf { it.isNotBlank() } ?: url.toUri().host ?: "Untitled"
                        )
                    )
                } else {
                    repository.saveItem(
                        SavedItem(
                            contentType = ContentType.TEXT,
                            text = input.trim(),
                            title = subject?.takeIf { it.isNotBlank() } ?: generateTempTitle(input)
                        )
                    )
                }
                _state.value = ShareSaveState.Success
            } catch (e: Exception) {
                _state.value = ShareSaveState.Error(e.message ?: "Could not save item")
            }
        }
    }
}

private fun BookmarkMetaData.toSavedItem(fallbackTitle: String?): SavedItem {
    // fetchBookmarkMetadata falls back to the bare host on failure; the share subject beats that
    val host = url.toUri().host
    val bestTitle = title?.takeIf { it.isNotBlank() && it != host }
        ?: fallbackTitle?.takeIf { it.isNotBlank() }
        ?: title
        ?: "Untitled"
    return SavedItem(
        contentType = ContentType.LINK,
        url = url,
        title = bestTitle,
        text = description,
        faviconUrl = faviconUrl,
        linkPreview = previewImage
    )
}
