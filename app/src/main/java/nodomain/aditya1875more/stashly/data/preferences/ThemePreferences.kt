package nodomain.aditya1875more.stashly.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemePreferences(private val context: Context) {

    companion object {
        private val DARK_MODE_KEY = intPreferencesKey("dark_mode")
        private val DYNAMIC_COLOR_KEY = booleanPreferencesKey("dynamic_color")
        private val CONTRAST_MODE_KEY = intPreferencesKey("contrast_mode")
        private val SEED_COLOR_KEY = intPreferencesKey("seed_color")
    }

    val darkModeFlow: Flow<DarkMode> = context.themeDataStore.data.map { preferences ->
        when (preferences[DARK_MODE_KEY]) {
            0 -> DarkMode.LIGHT
            1 -> DarkMode.DARK
            else -> DarkMode.SYSTEM
        }
    }

    val dynamicColorFlow: Flow<Boolean> = context.themeDataStore.data.map { preferences ->
        preferences[DYNAMIC_COLOR_KEY] ?: true
    }

    val contrastModeFlow: Flow<ContrastMode> = context.themeDataStore.data.map { preferences ->
        when (preferences[CONTRAST_MODE_KEY]) {
            0 -> ContrastMode.STANDARD
            1 -> ContrastMode.MEDIUM
            2 -> ContrastMode.HIGH
            else -> ContrastMode.STANDARD
        }
    }

    val seedColorFlow: Flow<ThemeSeed> = context.themeDataStore.data.map { preferences ->
        val seedIndex = preferences[SEED_COLOR_KEY] ?: 0
        ThemeSeed.entries.getOrElse(seedIndex) { ThemeSeed.BLUE }
    }

    suspend fun setDarkMode(mode: DarkMode) {
        context.themeDataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = when (mode) {
                DarkMode.LIGHT -> 0
                DarkMode.DARK -> 1
                DarkMode.SYSTEM -> 2
            }
        }
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        context.themeDataStore.edit { preferences ->
            preferences[DYNAMIC_COLOR_KEY] = enabled
        }
    }

    suspend fun setContrastMode(mode: ContrastMode) {
        context.themeDataStore.edit { preferences ->
            preferences[CONTRAST_MODE_KEY] = when (mode) {
                ContrastMode.STANDARD -> 0
                ContrastMode.MEDIUM -> 1
                ContrastMode.HIGH -> 2
            }
        }
    }

    suspend fun setSeedColor(seed: ThemeSeed) {
        context.themeDataStore.edit { preferences ->
            preferences[SEED_COLOR_KEY] = seed.ordinal
        }
    }
}

enum class DarkMode {
    LIGHT,
    DARK,
    SYSTEM
}

enum class ContrastMode {
    STANDARD,
    MEDIUM,
    HIGH
}

enum class ThemeSeed(val displayName: String, val seedColor: Long) {
    BLUE("Ocean Blue", 0xFF415F91),
    PURPLE("Royal Purple", 0xFF7B5FA1),
    GREEN("Forest Green", 0xFF3D7A52),
    ORANGE("Sunset Orange", 0xFFD67C3E),
    RED("Cherry Red", 0xFFBA3A3A),
    TEAL("Tropical Teal", 0xFF2D8B8B),
    PINK("Rose Pink", 0xFFD65B8F),
    INDIGO("Deep Indigo", 0xFF4A5899),
    AMBER("Golden Amber", 0xFFD69636),
    CYAN("Sky Cyan", 0xFF2B8AA0)
}