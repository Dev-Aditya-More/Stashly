package com.example.anchor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.anchor.data.local.SavedItem

@Composable
fun SavedContentScreen(
    savedItems: List<SavedItem>,
    onDelete: (SavedItem) -> Unit,
    onEdit: (SavedItem) -> Unit,
    onItemClick: (SavedItem) -> Unit,
    onSeeMore: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recently Saved",
                style = MaterialTheme.typography.titleMedium,
            )
            if (savedItems.size > 2) {
                TextButton(onClick = onSeeMore) {
                    Text("See more")
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Show only first 2 items
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            savedItems.take(2).forEach { item ->
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


