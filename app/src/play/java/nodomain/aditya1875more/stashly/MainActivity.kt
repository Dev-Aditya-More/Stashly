package nodomain.aditya1875more.stashly

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import nodomain.aditya1875more.stashly.ui.theme.StashlyTheme
import nodomain.aditya1875more.stashly.ui.viewmodels.ThemeViewModel
import nodomain.aditya1875more.stashly.utils.NotificationUtils
import org.koin.compose.viewmodel.koinViewModel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        requestNotificationPermission()

        NotificationUtils.createNotificationChannel(this)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        FirebaseMessaging.getInstance()
            .subscribeToTopic("theme_updates")

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

        FirebaseMessaging.getInstance()
            .subscribeToTopic("theme_updates")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Subscribed to theme_updates topic")
                } else {
                    Log.e("FCM", "Topic subscription failed", task.exception)
                }
            }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }
    }
}