package com.example.stashly.ui.screens

import com.example.stashly.R
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.stashly.data.local.ContentType
import com.example.stashly.data.local.SavedItem
import androidx.core.net.toUri
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.stashly.ui.components.ExpandableText

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
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column {
                            // Large banner preview
                            AsyncImage(
                                model = item.linkPreview ?: R.drawable.nopreview,
                                contentDescription = "Preview",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = item.faviconUrl ?: R.drawable.ic_link,
                                        contentDescription = "Favicon",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                    )

                                    Spacer(Modifier.width(8.dp))

                                    Text(
                                        text = item.title ?: item.url ?: "Untitled",
                                        style = MaterialTheme.typography.titleMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                // Description / notes
                                item.text?.let {
                                    ExpandableText(text = it)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AssistChip(
                            onClick = { item.url?.let { context.startActivity(Intent(Intent.ACTION_VIEW, it.toUri())) } },
                            label = { Text("Open") },
                            leadingIcon = { Icon(Icons.AutoMirrored.Filled.OpenInNew, null) }
                        )
                        AssistChip(
                            onClick = { item.url?.let { clipboard.setText(AnnotatedString(it)) } },
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
                    val fileName = item.filePath?.substringAfterLast("/") ?: "Unknown file"
                    val isImage = fileName.endsWith(".png", true) ||
                            fileName.endsWith(".jpg", true) ||
                            fileName.endsWith(".jpeg", true) ||
                            fileName.endsWith(".gif", true) ||
                            fileName.endsWith(".webp", true)

                    Card(
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Log.d("DEBUG", "Picked file URI: ${item.filePath}")

                        Column {

                            val painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(context)
                                    .data(item.filePath?.toUri())
                                    .crossfade(true)
                                    .build()
                            )
                            // Preview banner
                            if (isImage) {
                                Image(
                                    painter = painter,
                                    contentDescription = "Image Preview",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                                    contentScale = ContentScale.Crop
                                )

                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(180.dp)
                                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                                        .background(MaterialTheme.colorScheme.tertiaryContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                        modifier = Modifier.size(64.dp)
                                    )
                                }
                            }


                            // Metadata below preview
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Title (filename)
                                Text(
                                    text = fileName,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(Modifier.height(8.dp))

                                // Subtitle (description or file info)
                                val fileInfo = remember(item.filePath) {
                                    val uri = item.filePath?.toUri()
                                    val type = uri?.let { context.contentResolver.getType(it) } ?: "Unknown type"
                                    val size = uri?.let {
                                        context.contentResolver.openFileDescriptor(it, "r")?.use { pfd ->
                                            "${(pfd.statSize / 1024)} KB"
                                        }
                                    } ?: ""
                                    listOf(type, size).filter { it.isNotEmpty() }.joinToString(" • ")
                                }

                                ExpandableText(
                                    text = item.text ?: fileInfo.ifEmpty { "No description" },
                                    minimizedMaxLines = 2
                                )
                            }

                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Actions (same as link section)
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AssistChip(
                            onClick = {
                                item.filePath?.let {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(it.toUri(), if (isImage) "image/*" else "*/*")
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
                                        type = if (isImage) "image/*" else "*/*"
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
