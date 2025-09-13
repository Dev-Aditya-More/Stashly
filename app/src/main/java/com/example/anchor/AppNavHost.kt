package com.example.anchor

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.anchor.data.local.SavedItem
import com.example.anchor.ui.screens.DetailScreen
import com.example.anchor.ui.screens.FavouritesScreen
import com.example.anchor.ui.screens.ItemScreen
import com.example.anchor.ui.screens.MainScreen
import com.example.anchor.ui.viewmodels.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.compose.viewmodel.koinViewModel

// Define your routes safely
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Splash : Screen("splash", "", Icons.Default.Home) // no bottom bar
    data object Main : Screen("main", "Home", Icons.Default.Home)
    data object Favourites : Screen("favourites", "Favourites", Icons.Default.Star)
    object Detail : Screen("detail", "", Icons.Default.Home) {
        fun createRoute(id: Int) = "$route/$id"
    }
    data object Items : Screen("items", "", Icons.Default.Home)
}

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
        composable(Screen.Favourites.route) {
            FavouritesScreen(navController = navController)
        }
        composable(
            route = Screen.Detail.route + "/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id")
            if (id == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Invalid item id")
                }
            } else {
                val viewModel: MainViewModel = koinViewModel()
                val item by viewModel.getItemById(id).collectAsStateWithLifecycle(initialValue = null)
                when (item) {
                    null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                    else -> DetailScreen(
                        item = item!!,
                        onToggleFavorite = {
                            viewModel.toggleFavourite(item!!)
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }

        composable(Screen.Items.route) {
            ItemScreen(navController = navController)
        }

    }
}
