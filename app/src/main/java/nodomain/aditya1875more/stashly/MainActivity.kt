package nodomain.aditya1875more.stashly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import nodomain.aditya1875more.stashly.ui.theme.AnchorTheme
import nodomain.aditya1875more.stashly.utils.SetSystemBars

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
            val windowSizeClass = calculateWindowSizeClass(this)
            AnchorTheme {

                val darkTheme = isSystemInDarkTheme()
                SetSystemBars(lightIcons = darkTheme)

                AppNavHost(
                    navController = rememberNavController(),
                    windowSizeClass = windowSizeClass
                )
            }
        }
    }
}

@Preview
@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onTimeout: () -> Unit = {}
) {

    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = ""
    )

    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(1200),
        label = ""
    )

    LaunchedEffect(key1 = true) {
        // Delay for 2 seconds before navigating
        delay(2000)
        onTimeout()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Show the logo
            Image(
                painter = painterResource(id = R.drawable.stashlylogobg),
                contentDescription = "Stashly Logo",
                modifier = Modifier
                    .size(250.dp)
                    .graphicsLayer{
                        scaleX = scale
                        scaleY = scale
                    }.alpha(alpha)
                ,
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.padding(16.dp))

            TypingText2(
                "Stashly"
            )
        }
    }
}

@Composable
fun TypingText2(text: String, delayMillis: Long = 100) {
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(text) {
        text.forEachIndexed { i, _ ->
            delay(delayMillis)
            displayedText = text.take(i + 1)
        }
    }

    Text(
        text = displayedText,
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f),
        fontWeight = FontWeight.Bold,
        fontSize = 42.sp,
        letterSpacing = 2.sp
    )
}

