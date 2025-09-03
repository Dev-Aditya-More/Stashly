package com.example.anchor.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.anchor.data.local.SavedItem

@Composable
fun SavedContentScreen(
    savedItems: List<SavedItem>,
    onDelete: (SavedItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Recently Saved",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(savedItems) { item ->
                SavedItemCard(item, onDelete)
            }
        }
    }
}


