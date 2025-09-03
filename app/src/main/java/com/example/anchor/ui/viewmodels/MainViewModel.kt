package com.example.anchor.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anchor.data.local.LinkEntity
import com.example.anchor.data.remote.LinkRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: LinkRepository
) : ViewModel() {

    val links: StateFlow<List<LinkEntity>> =
        repository.getAllLinks()
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun saveLink(url: String) {
        viewModelScope.launch {
            repository.saveLink(url)
        }
    }

    fun removeLink(link: LinkEntity) {
        viewModelScope.launch {
            repository.removeLink(link)
        }

    }
}