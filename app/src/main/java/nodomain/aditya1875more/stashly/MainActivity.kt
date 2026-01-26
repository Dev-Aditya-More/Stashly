package nodomain.aditya1875more.stashly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import nodomain.aditya1875more.stashly.ui.theme.StashlyTheme
import nodomain.aditya1875more.stashly.ui.viewmodels.ThemeViewModel
import nodomain.aditya1875more.stashly.utils.SetSystemBars
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }

        setContent {
            val themeViewModel : ThemeViewModel = koinViewModel()
            val darkMode by themeViewModel.darkMode.collectAsStateWithLifecycle()
            val dynamicColor by themeViewModel.dynamicColor.collectAsStateWithLifecycle()
            val contrastMode by themeViewModel.contrastMode.collectAsStateWithLifecycle()
            val seedColor by themeViewModel.seedColor.collectAsStateWithLifecycle()
            val windowSizeClass = calculateWindowSizeClass(this)

            StashlyTheme(
                darkMode = darkMode,
                dynamicColor = dynamicColor,
                contrastMode = contrastMode,
                seedColor = seedColor
            ) {
                AppNavHost(
                    navController = rememberNavController(),
                    windowSizeClass = windowSizeClass
                )
            }
        }
    }
}


