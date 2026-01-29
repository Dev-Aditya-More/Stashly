package nodomain.aditya1875more.stashly.ui.screens

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import kotlinx.coroutines.launch
import nodomain.aditya1875more.stashly.R
import nodomain.aditya1875more.stashly.data.local.ContentType
import nodomain.aditya1875more.stashly.data.local.SavedItem
import nodomain.aditya1875more.stashly.ui.components.ExpandableText
import nodomain.aditya1875more.stashly.utils.HapticEvent
import nodomain.aditya1875more.stashly.utils.rememberHapticManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    item: SavedItem,
    onToggleFavorite: (Int, Boolean) -> Unit,
    onBack: () -> Unit,
    windowSizeClass: WindowSizeClass
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val haptics = rememberHapticManager()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details") },
                actions = {
                    IconButton(
                        onClick = {
                            onToggleFavorite(
                                item.id,
                                !item.isFavorite
                            )
                            haptics(HapticEvent.TAP)
                        },
                        modifier = Modifier.padding(end = 15.dp)
                    ) {
                        if (item.isFavorite) {
                            Icon(Icons.Default.Star, contentDescription = "Favourite")
                        } else {
                            Icon(Icons.Default.StarBorder, contentDescription = "Favourite")
                        }
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

        val modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .fillMaxSize()

        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                DetailContent(item, context)
                DetailActions(item, context, clipboard)
            }
        } else {
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    DetailContent(item, context)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    DetailActions(item, context, clipboard)
                }
            }
        }
    }
}

// ------------------- CONTENT -------------------
@Composable
fun DetailContent(item: SavedItem, context: Context) {
    // Title
    Text(
        text = item.title ?: item.contentType.name,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(horizontal = 4.dp)
    )

    when (item.contentType) {
        ContentType.TEXT -> {
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = item.text ?: "No text available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        ContentType.LINK -> {
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
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
                        item.text?.let { ExpandableText(text = it) }
                    }
                }
            }
        }

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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    if (isImage) {
                        val painter = rememberAsyncImagePainter(
                            ImageRequest.Builder(context)
                                .data(item.filePath?.toUri())
                                .crossfade(true)
                                .build()
                        )
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
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = fileName,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
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
                        ExpandableText(text = item.text ?: fileInfo.ifEmpty { "No description" }, minimizedMaxLines = 2)
                    }
                }
            }
        }
    }
}

// ------------------- ACTIONS -------------------
@Composable
fun DetailActions(item: SavedItem, context: Context, clipboard: Clipboard) {

    val scope = rememberCoroutineScope()
    when (item.contentType) {
        ContentType.TEXT -> {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AssistChip(
                    onClick = {
                        item.text?.let {
                            scope.launch {
                                clipboard.setClipEntry(
                                    ClipEntry(
                                        ClipData.newPlainText(
                                            "",
                                            AnnotatedString(
                                                it
                                            )
                                        )
                                    )
                                )
                            }
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

        ContentType.LINK -> {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                AssistChip(
                    onClick = { item.url?.let { context.startActivity(Intent(Intent.ACTION_VIEW, it.toUri())) } },
                    label = { Text("Open") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.OpenInNew, null) }
                )
                AssistChip(
                    onClick = {
                        item.url?.let {
                            scope.launch {
                                clipboard.setClipEntry(
                                    ClipEntry(
                                        ClipData.newPlainText(
                                            "",
                                            AnnotatedString(
                                                it
                                            )
                                        )
                                    )
                                )
                            }
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

        ContentType.FILE -> {
            val fileName = item.filePath?.substringAfterLast("/") ?: "Unknown file"
            val isImage = fileName.endsWith(".png", true) ||
                    fileName.endsWith(".jpg", true) ||
                    fileName.endsWith(".jpeg", true) ||
                    fileName.endsWith(".gif", true) ||
                    fileName.endsWith(".webp", true)

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
                            scope.launch {
                                clipboard.setClipEntry(
                                    ClipEntry(
                                        ClipData.newPlainText(
                                            "",
                                            AnnotatedString(
                                                it
                                            )
                                        )
                                    )
                                )
                            }
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
