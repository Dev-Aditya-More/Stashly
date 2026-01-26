package nodomain.aditya1875more.stashly.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nodomain.aditya1875more.stashly.data.local.SavedItem
import nodomain.aditya1875more.stashly.data.remote.ItemRepository

class FavouriteViewModel(
    private val repository: ItemRepository
) : ViewModel() {

    val favourites: StateFlow<List<SavedItem>> =
        repository.getFavourites()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun toggleFavourite(itemId: Int, isFavourite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFavourite(itemId, isFavourite)
        }
    }
}
