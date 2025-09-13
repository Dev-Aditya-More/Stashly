package com.example.anchor.ui.screens

import com.example.stashly.R
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.anchor.data.local.ContentType
import com.example.anchor.data.local.SavedItem
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    item: SavedItem,
    onToggleFavorite: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                actions = {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.padding(end = 15.dp)
                    ) {
                        if (item.isFavorite) Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Favourite"
                        ) else
                        Icon(
                            imageVector = Icons.Default.StarBorder,
                            contentDescription = "Favourite"
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Title
            Text(
                text = item.title ?: item.contentType.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            when (item.contentType) {
                // ---------------- TEXT ----------------
                ContentType.TEXT -> {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = item.text ?: "No text available",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    // Actions
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AssistChip(
                            onClick = {
                                item.text?.let {
                                    clipboard.setText(AnnotatedString(it))
                                    Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            label = { Text("Copy") },
                            leadingIcon = { Icon(Icons.Default.ContentCopy, null) }
                        )
                        AssistChip(
                            onClick = {
                                item.text?.let {
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, it)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share via"))
                                }
                            },
                            label = { Text("Share") },
                            leadingIcon = { Icon(Icons.Default.Share, null) }
                        )
                    }
                }

                // ---------------- LINK ----------------
                ContentType.LINK -> {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column {
                            // Large banner preview
                            item.linkPreview?.let { imageUrl ->
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(200.dp)
                                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Small favicon
                                    AsyncImage(
                                        model = item.faviconUrl ?: R.drawable.ic_link,
                                        contentDescription = "Favicon",
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )

                                    Spacer(Modifier.width(8.dp))

                                    Text(
                                        text = item.title ?: item.url ?: "Untitled",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                item.text?.let {
                                    Text(
                                        text = item.text,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }

                    }

                    // Actions
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AssistChip(
                            onClick = {
                                item.url?.let {
                                    val intent = Intent(Intent.ACTION_VIEW, it.toUri())
                                    context.startActivity(intent)
                                }
                            },
                            label = { Text("Open") },
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.OpenInNew, null) }
                        )
                        AssistChip(
                            onClick = {
                                item.url?.let {
                                    clipboard.setText(AnnotatedString(it))
                                    Toast.makeText(context, "Link copied!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            label = { Text("Copy") },
                            leadingIcon = { Icon(Icons.Default.ContentCopy, null) }
                        )
                        AssistChip(
                            onClick = {
                                item.url?.let {
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, it)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share link via"))
                                }
                            },
                            label = { Text("Share") },
                            leadingIcon = { Icon(Icons.Default.Share, null) }
                        )
                    }
                }

                // ---------------- FILE ----------------
                ContentType.FILE -> {
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = item.filePath?.substringAfterLast("/") ?: "Unknown file",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer
                                )
                                Text(
                                    text = "File",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // Actions
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AssistChip(
                            onClick = {
                                item.filePath?.let {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(it.toUri(), "*/*")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(intent)
                                }
                            },
                            label = { Text("Open") },
                            leadingIcon = { Icon(Icons.Default.FolderOpen, null) }
                        )
                        AssistChip(
                            onClick = {
                                item.filePath?.let {
                                    clipboard.setText(AnnotatedString(it))
                                    Toast.makeText(context, "Path copied!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            label = { Text("Copy") },
                            leadingIcon = { Icon(Icons.Default.ContentCopy, null) }
                        )
                        AssistChip(
                            onClick = {
                                item.filePath?.let {
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "*/*"
                                        putExtra(Intent.EXTRA_STREAM, it.toUri())
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share file via"))
                                }
                            },
                            label = { Text("Share") },
                            leadingIcon = { Icon(Icons.Default.Share, null) }
                        )
                    }
                }
            }
        }
    }
}

data class Action(val label: String, val icon: ImageVector, val onClick: () -> Unit)
