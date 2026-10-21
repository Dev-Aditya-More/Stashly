package nodomain.aditya1875more.stashly

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import nodomain.aditya1875more.stashly.ui.screens.ShareScreen
import nodomain.aditya1875more.stashly.ui.theme.StashlyTheme
import nodomain.aditya1875more.stashly.ui.viewmodels.ShareViewModel
import nodomain.aditya1875more.stashly.ui.viewmodels.ThemeViewModel
import org.koin.compose.viewmodel.koinViewModel

class ShareActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Allow content to draw behind system bars so the sheet looks correct
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val sharedText = intent?.getStringExtra(Intent.EXTRA_TEXT)
        val sharedSubject = intent?.getStringExtra(Intent.EXTRA_SUBJECT)
        if (sharedText.isNullOrBlank()) {
            finish()
            return
        }

        setContent {
            // Theme prefs start from their defaults and settle within a frame or two,
            // so the sheet matches the app without blocking on DataStore
            val themeViewModel: ThemeViewModel = koinViewModel()
            val darkMode by themeViewModel.darkMode.collectAsStateWithLifecycle()
            val dynamicColor by themeViewModel.dynamicColor.collectAsStateWithLifecycle()
            val contrastMode by themeViewModel.contrastMode.collectAsStateWithLifecycle()
            val seedColor by themeViewModel.seedColor.collectAsStateWithLifecycle()

            StashlyTheme(
                darkMode = darkMode,
                dynamicColor = dynamicColor,
                contrastMode = contrastMode,
                seedColor = seedColor
            ) {
                val viewModel: ShareViewModel = koinViewModel()
                val state by viewModel.state.collectAsStateWithLifecycle()

                LaunchedEffect(Unit) {
                    viewModel.save(sharedText, sharedSubject)
                }

                ShareScreen(
                    sharedText = sharedText,
                    state = state,
                    onDismiss = ::finish
                )
            }
        }
    }
}
