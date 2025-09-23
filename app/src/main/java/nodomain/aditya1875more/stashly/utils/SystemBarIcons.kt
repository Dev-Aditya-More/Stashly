package nodomain.aditya1875more.stashly.utils

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun SetSystemBars(lightIcons: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            // if lightIcons = true → icons are white
            insetsController.isAppearanceLightStatusBars = !lightIcons
            insetsController.isAppearanceLightNavigationBars = !lightIcons
        }
    }
}