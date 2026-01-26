package nodomain.aditya1875more.stashly

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import nodomain.aditya1875more.stashly.ui.viewmodels.ThemeViewModel
import nodomain.aditya1875more.stashly.ui.screens.DetailScreen
import nodomain.aditya1875more.stashly.ui.screens.FavouritesScreen
import nodomain.aditya1875more.stashly.ui.screens.ItemScreen
import nodomain.aditya1875more.stashly.ui.screens.MainScreen
import nodomain.aditya1875more.stashly.ui.screens.SplashScreen
import nodomain.aditya1875more.stashly.ui.screens.ThemeSettingsScreen
import nodomain.aditya1875more.stashly.ui.viewmodels.FavouriteViewModel
import nodomain.aditya1875more.stashly.ui.viewmodels.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

// Define your routes safely
sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Splash : Screen("splash", "", Icons.Default.Home)
    object Main : Screen("main", "Home", Icons.Default.Home)
    object Favourites : Screen("favourites", "Favourites", Icons.Default.Star)
    object Detail : Screen("detail", "", Icons.Default.Home) {
        fun createRoute(id: Int) = "$route/$id"
    }
    object Items : Screen("items", "", Icons.Default.Home)

    object ThemeSettings : Screen("theme_settings", "Theme", Icons.Default.Settings)
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass
) {
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
        addMainGraph(navController, windowSizeClass)

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
                val favViewModel : FavouriteViewModel = koinViewModel()
                val item by viewModel.getItemById(id).collectAsStateWithLifecycle(initialValue = null)
                val currentItem = item ?: return@composable
                when (item) {
                    else ->
                        DetailScreen(
                            item = currentItem,
                            onToggleFavorite = { id, fav ->
                                favViewModel.toggleFavourite(id, fav)
                            },
                            onBack = { navController.popBackStack() },
                            windowSizeClass = windowSizeClass
                        )

                }
            }
        }

        composable(Screen.Items.route) {
            ItemScreen(navController, windowSizeClass)
        }
    }
}


fun NavGraphBuilder.addMainGraph(
    navController: NavHostController,
    windowSizeClass: WindowSizeClass
) {
    composable(Screen.Main.route) {
        MainScreen(navController)
    }
    composable(Screen.Favourites.route) {
        FavouritesScreen(navController, windowSizeClass)
    }
    composable(Screen.ThemeSettings.route) {
        val themeViewModel: ThemeViewModel = koinViewModel()
        ThemeSettingsScreen(
            themeViewModel = themeViewModel,
            navController = navController
        )
    }
}

