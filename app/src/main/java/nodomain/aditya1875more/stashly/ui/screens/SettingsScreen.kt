package nodomain.aditya1875more.stashly.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import nodomain.aditya1875more.stashly.ui.viewmodels.ThemeViewModel
import nodomain.aditya1875more.stashly.data.preferences.ContrastMode
import nodomain.aditya1875more.stashly.data.preferences.DarkMode
import nodomain.aditya1875more.stashly.data.preferences.ThemeSeed
import nodomain.aditya1875more.stashly.ui.components.StashlyBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSettingsScreen(
    themeViewModel: ThemeViewModel,
    navController: NavHostController,
) {
    val darkMode by themeViewModel.darkMode.collectAsStateWithLifecycle()
    val dynamicColor by themeViewModel.dynamicColor.collectAsStateWithLifecycle()
    val contrastMode by themeViewModel.contrastMode.collectAsStateWithLifecycle()
    val seedColor by themeViewModel.seedColor.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = { StashlyBottomBar(navController) }

    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Dark Mode Section
            item {
                SettingsSection(title = "Appearance") {
                    DarkModeSelector(
                        selectedMode = darkMode,
                        onModeSelected = { themeViewModel.setDarkMode(it) }
                    )
                }
            }

            // Dynamic Color Section
            item {
                SettingsSection(title = "Material You") {
                    DynamicColorToggle(
                        enabled = dynamicColor,
                        onToggle = { themeViewModel.setDynamicColor(it) }
                    )
                }
            }

            if (!dynamicColor) {
                item {
                    SettingsSection(title = "Theme Color") {
                        ColorSeedSelector(
                            selectedSeed = seedColor,
                            onSeedSelected = { themeViewModel.setSeedColor(it) }
                        )
                    }
                }
            }

            item {
                SettingsSection(title = "Contrast") {
                    ContrastModeSelector(
                        selectedMode = contrastMode,
                        onModeSelected = { themeViewModel.setContrastMode(it) }
                    )
                }
            }

            // Preview Section
            item {
                ThemePreview()
            }
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        content()
    }
}

@Composable
private fun DarkModeSelector(
    selectedMode: DarkMode,
    onModeSelected: (DarkMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        DarkModeOption(
            mode = DarkMode.LIGHT,
            icon = Icons.Default.LightMode,
            label = "Light",
            isSelected = selectedMode == DarkMode.LIGHT,
            onClick = { onModeSelected(DarkMode.LIGHT) },
            modifier = Modifier.weight(1f)
        )
        DarkModeOption(
            mode = DarkMode.DARK,
            icon = Icons.Default.DarkMode,
            label = "Dark",
            isSelected = selectedMode == DarkMode.DARK,
            onClick = { onModeSelected(DarkMode.DARK) },
            modifier = Modifier.weight(1f)
        )
        DarkModeOption(
            mode = DarkMode.SYSTEM,
            icon = Icons.Default.Contrast,
            label = "System",
            isSelected = selectedMode == DarkMode.SYSTEM,
            onClick = { onModeSelected(DarkMode.SYSTEM) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DarkModeOption(
    mode: DarkMode,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        label = "container_color"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        label = "content_color"
    )

    Surface(
        modifier = modifier
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = containerColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )
        }
    }
}

@Composable
private fun DynamicColorToggle(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Dynamic Colors",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Use wallpaper colors (Android 12+)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = enabled,
                onCheckedChange = onToggle
            )
        }
    }
}

@Composable
private fun ColorSeedSelector(
    selectedSeed: ThemeSeed,
    onSeedSelected: (ThemeSeed) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(5),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(ThemeSeed.entries.toList()) { seed ->
            ColorSeedOption(
                seed = seed,
                isSelected = selectedSeed == seed,
                onClick = { onSeedSelected(seed) }
            )
        }
    }
}

@Composable
private fun ColorSeedOption(
    seed: ThemeSeed,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .background(Color(seed.seedColor))
            .border(
                width = if (isSelected) 3.dp else 0.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun ContrastModeSelector(
    selectedMode: ContrastMode,
    onModeSelected: (ContrastMode) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ContrastOption(
            mode = ContrastMode.STANDARD,
            label = "Standard",
            isSelected = selectedMode == ContrastMode.STANDARD,
            onClick = { onModeSelected(ContrastMode.STANDARD) },
            modifier = Modifier.weight(1f)
        )
        ContrastOption(
            mode = ContrastMode.MEDIUM,
            label = "Medium",
            isSelected = selectedMode == ContrastMode.MEDIUM,
            onClick = { onModeSelected(ContrastMode.MEDIUM) },
            modifier = Modifier.weight(1f)
        )
        ContrastOption(
            mode = ContrastMode.HIGH,
            label = "High",
            isSelected = selectedMode == ContrastMode.HIGH,
            onClick = { onModeSelected(ContrastMode.HIGH) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ContrastOption(
    mode: ContrastMode,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surfaceVariant,
        label = "container_color"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected)
            MaterialTheme.colorScheme.onPrimaryContainer
        else
            MaterialTheme.colorScheme.onSurfaceVariant,
        label = "content_color"
    )

    Surface(
        modifier = modifier
            .height(60.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = contentColor,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun ThemePreview() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Preview",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primary color
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Primary", style = MaterialTheme.typography.bodyMedium)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }

                // Secondary color
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Secondary", style = MaterialTheme.typography.bodyMedium)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary)
                    )
                }

                // Tertiary color
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Tertiary", style = MaterialTheme.typography.bodyMedium)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.tertiary)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 4.dp))

                // Sample button
                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sample Button")
                }
            }
        }
    }
}