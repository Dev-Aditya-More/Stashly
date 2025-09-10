package com.example.anchor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.anchor.data.local.ContentType
import com.example.anchor.data.local.SavedItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    item: SavedItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                actions = {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            )
        }
    ) { paddingValues ->
        Card(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Text(
                    text = item.title ?: item.contentType.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Content
                when (item.contentType) {
                    ContentType.TEXT -> {
                        Text(
                            text = item.text ?: "No text available",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        ActionRow(
                            actions = listOf(
                                Action("Copy", Icons.Default.ContentCopy) { /* Copy text */ },
                                Action("Share", Icons.Default.Share) { /* Share text */ }
                            )
                        )
                    }

                    ContentType.LINK -> {
                        Text(
                            text = item.url ?: "No link available",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.primary
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                        ActionRow(
                            actions = listOf(
                                Action("", Icons.AutoMirrored.Filled.OpenInNew) { /* Open in browser */ },
                                Action("", Icons.Default.ContentCopy) { /* Copy link */ },
                                Action("", Icons.Default.Share) { /* Share link */ }
                            )
                        )
                    }

                    ContentType.FILE -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = item.filePath?.substringAfterLast("/") ?: "No file path",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        ActionRow(
                            actions = listOf(
                                Action("", Icons.Default.FolderOpen) { /* Open file */ },
                                Action("", Icons.Default.ContentCopy) { /* Copy file path */ },
                                Action("", Icons.Default.Share) { /* Share file */ }
                            )
                        )
                    }
                }
            }
        }
    }
}

// Helper composable for buttons
@Composable
fun ActionRow(actions: List<Action>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        actions.forEach { action ->
            OutlinedButton(
                onClick = action.onClick,
                modifier = Modifier.weight(1f)
            ) {
                Icon(action.icon, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text(action.label)
            }
        }
    }
}

data class Action(val label: String, val icon: ImageVector, val onClick: () -> Unit)
