package nodomain.aditya1875more.stashly.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import nodomain.aditya1875more.stashly.data.preferences.ContrastMode
import nodomain.aditya1875more.stashly.data.preferences.DarkMode
import nodomain.aditya1875more.stashly.data.preferences.ThemePreferences
import nodomain.aditya1875more.stashly.data.preferences.ThemeSeed

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val themePreferences = ThemePreferences(application)

    val darkMode: StateFlow<DarkMode> = themePreferences.darkModeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DarkMode.SYSTEM
    )

    val dynamicColor: StateFlow<Boolean> = themePreferences.dynamicColorFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = true
    )

    val contrastMode: StateFlow<ContrastMode> = themePreferences.contrastModeFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ContrastMode.STANDARD
    )

    val seedColor: StateFlow<ThemeSeed> = themePreferences.seedColorFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ThemeSeed.BLUE
    )

    fun setDarkMode(mode: DarkMode) {
        viewModelScope.launch {
            themePreferences.setDarkMode(mode)
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch {
            themePreferences.setDynamicColor(enabled)
        }
    }

    fun setContrastMode(mode: ContrastMode) {
        viewModelScope.launch {
            themePreferences.setContrastMode(mode)
        }
    }

    fun setSeedColor(seed: ThemeSeed) {
        viewModelScope.launch {
            themePreferences.setSeedColor(seed)
        }
    }
}