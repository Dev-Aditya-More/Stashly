package com.example.anchor.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.anchor.Screen

@Composable
fun StashlyBottomBar(
    navController: NavHostController
) {
    val bottomBarScreens = listOf(Screen.Main, Screen.Favourites)

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route


    Box(
        modifier = Modifier
            .shadow(10.dp, RoundedCornerShape(24.dp))
    ) {
        NavigationBar(
            tonalElevation = 0.dp,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        ) {
            bottomBarScreens.forEach { screen ->
                val selected = currentDestination == screen.route

                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        navController.navigate(screen.route) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                        }
                    },
                    icon = {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                            ,
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.label,
                                tint = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    },
                    label = {
                        if (selected) {
                            Text(
                                screen.label,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    alwaysShowLabel = false
                )
            }
        }
    }
}

