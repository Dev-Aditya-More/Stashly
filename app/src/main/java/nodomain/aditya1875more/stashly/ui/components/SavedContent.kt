package nodomain.aditya1875more.stashly.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import nodomain.aditya1875more.stashly.data.local.SavedItem

/**
 * How many recent items the home screen previews: columns follow the width,
 * rows follow the height, so a landscape phone stays to one row and a tablet fills its space.
 */
private data class RecentLayout(val columns: Int, val rows: Int) {
    val count get() = columns * rows
}

private fun recentLayoutFor(windowSizeClass: WindowSizeClass): RecentLayout {
    val columns = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> 3
        WindowWidthSizeClass.Medium -> 2
        else -> 1
    }
    val rows = when (windowSizeClass.heightSizeClass) {
        WindowHeightSizeClass.Compact -> 1
        WindowHeightSizeClass.Expanded -> 3
        // Phones in portrait land here; one column has room for a few cards
        else -> if (columns == 1) 3 else 2
    }
    return RecentLayout(columns, rows)
}

@Composable
fun SavedContentScreen(
    savedItems: List<SavedItem>,
    windowSizeClass: WindowSizeClass,
    onDelete: (SavedItem) -> Unit,
    onEdit: (SavedItem) -> Unit,
    onItemClick: (SavedItem) -> Unit,
    onSeeMore: () -> Unit
) {
    val layout = recentLayoutFor(windowSizeClass)
    val recentItems = savedItems.take(layout.count)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Recently Saved",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (savedItems.isNotEmpty()) {
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "${savedItems.size}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            if (savedItems.size > recentItems.size) {
                TextButton(onClick = onSeeMore) {
                    Text(
                        "See all",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(layout.columns),
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(recentItems, key = { it.id }) { item ->
                SwipeToDeleteCard(
                    item = item,
                    onDelete = onDelete
                ) {
                    SavedItemCard(
                        item = item,
                        onDelete = onDelete,
                        onSaveEdit = onEdit,
                        onItemClick = onItemClick
                    )
                }
            }
        }
    }
}


