package com.example.anchor

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.anchor.data.local.SavedItem
import com.example.anchor.ui.screens.DetailScreen
import com.example.anchor.ui.screens.MainScreen
import com.example.anchor.ui.viewmodels.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

// Define your routes safely
sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Main : Screen("main")
    object Detail : Screen("detail") {
        fun createRoute(id: Int) = "$route/$id"
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen {
                navController.navigate(Screen.Main.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            }
        }
        composable(Screen.Main.route) {
            MainScreen(navController = navController)
        }
        composable(
            route = Screen.Detail.route + "/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id: Int? = backStackEntry.arguments?.getInt("id")

            if (id == null) {
                // Show a friendly placeholder instead of returning early
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Invalid item id", modifier = Modifier)
                }
            } else {
                val viewModel: MainViewModel = koinViewModel()
                // lifecycle aware collection (avoid work when screen is not visible)
                val item by viewModel.getItemById(id).collectAsStateWithLifecycle(initialValue = null)

                when {
                    item == null -> {
                        // loading / not yet emitted
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    else -> {
                        DetailScreen(
                            item = item!!,
                            onToggleFavorite = {

                            },
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }

    }
}
