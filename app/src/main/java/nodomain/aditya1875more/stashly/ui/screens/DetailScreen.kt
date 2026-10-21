package nodomain.aditya1875more.stashly.ui.screens

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.text.format.Formatter
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Storage
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.Clipboard
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import nodomain.aditya1875more.stashly.ui.components.EditItemDialog
import nodomain.aditya1875more.stashly.ui.components.ExpandableText
import nodomain.aditya1875more.stashly.ui.viewmodels.getFileName
import nodomain.aditya1875more.stashly.utils.HapticEvent
import nodomain.aditya1875more.stashly.utils.rememberHapticManager
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    item: SavedItem,
    onToggleFavorite: (Int, Boolean) -> Unit,
    onSaveEdit: (SavedItem) -> Unit,
    onBack: () -> Unit,
    windowSizeClass: WindowSizeClass
) {
    val context = LocalContext.current
    val clipboard = LocalClipboard.current
    val haptics = rememberHapticManager()
    var isEditing by remember { mutableStateOf(false) }

    if (isEditing) {
        EditItemDialog(
            item = item,
            onDismiss = { isEditing = false },
            onSave = {
                onSaveEdit(it)
                isEditing = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    "Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = MaterialTheme.typography.titleLarge.fontWeight,
                    color = MaterialTheme.colorScheme.onSurface
                )},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = { isEditing = true }) {
                        Icon(
                            painter = painterResource(R.drawable.edit),
                            contentDescription = "Edit"
                        )
                    }
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

        val horizontalPadding = when (windowSizeClass.widthSizeClass) {
            WindowWidthSizeClass.Expanded -> 32.dp
            WindowWidthSizeClass.Medium -> 24.dp
            else -> 16.dp
        }
        val modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = horizontalPadding, vertical = 16.dp)

        if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) {
            Column(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                DetailContent(item, context)
                DetailActions(item, context, clipboard)
                DetailInfoCard(item, context)
            }
        } else {
            // Wide screens: content on the left, actions + metadata on the right so neither side sits empty
            Row(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1.4f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    DetailContent(item, context)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    DetailActions(item, context, clipboard)
                    DetailInfoCard(item, context)
                }
            }
        }
    }
}

// ------------------- CONTENT -------------------
@Composable
fun DetailContent(item: SavedItem, context: Context) {

    val displayTitle = remember(item) {
        if (item.contentType == ContentType.FILE) {
            item.title?.takeIf { it.isNotBlank() }
                ?: item.filePath?.toUri()?.let { uri -> getFileName(context, uri) }
                ?: item.contentType.name
        } else {
            item.title ?: item.contentType.name
        }
    }

    Text(
        text = displayTitle,
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
                            .aspectRatio(16f / 9f)
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
            val fileUri = remember(item.filePath) { item.filePath?.toUri() }
            val fileName = remember(fileUri) {
                fileUri?.let { getFileName(context, it) } ?: item.title ?: "Unknown file"
            }

            val mimeType = remember(fileUri) {
                fileUri?.let { context.contentResolver.getType(it) }
            }
            val isImage = mimeType?.startsWith("image/") == true

            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    if (isImage && fileUri != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(fileUri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Image Preview",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                            contentScale = ContentScale.Crop,
                            error = painterResource(R.drawable.ic_link),
                            placeholder = painterResource(R.drawable.ic_link)
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
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        val fileInfo = remember(fileUri) {
                            val type = mimeType ?: "Unknown type"
                            val size = fileUri?.let {
                                try {
                                    context.contentResolver.openFileDescriptor(it, "r")?.use { pfd ->
                                        val sizeKB = pfd.statSize / 1024
                                        if (sizeKB > 1024) {
                                            "${String.format("%.1f", sizeKB / 1024f)} MB"
                                        } else {
                                            "$sizeKB KB"
                                        }
                                    }
                                } catch (e: Exception) {
                                    null
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

// ------------------- INFO -------------------
@Composable
fun DetailInfoCard(item: SavedItem, context: Context) {
    val dateFormat = remember { DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT) }

    val rows: List<Triple<ImageVector, String, String>> = remember(item) {
        buildList {
            add(
                Triple(
                    Icons.Default.Category, "Type", when (item.contentType) {
                        ContentType.LINK -> "Link"
                        ContentType.TEXT -> "Note"
                        ContentType.FILE -> "File"
                    }
                )
            )
            when (item.contentType) {
                ContentType.LINK -> {
                    item.url?.toUri()?.host?.removePrefix("www.")?.let {
                        add(Triple(Icons.Default.Language, "Website", it))
                    }
                    item.url?.let { add(Triple(Icons.Default.Link, "Link", it)) }
                }
                ContentType.TEXT -> {
                    val text = item.text.orEmpty()
                    val words = text.split("\\s+".toRegex()).count { it.isNotBlank() }
                    add(Triple(Icons.AutoMirrored.Filled.Notes, "Length", "$words words · ${text.length} characters"))
                }
                ContentType.FILE -> {
                    val uri = item.filePath?.toUri()
                    val mime = uri?.let { runCatching { context.contentResolver.getType(it) }.getOrNull() }
                    val size = uri?.let {
                        runCatching {
                            context.contentResolver.openFileDescriptor(it, "r")?.use { pfd ->
                                Formatter.formatShortFileSize(context, pfd.statSize)
                            }
                        }.getOrNull()
                    }
                    mime?.let { add(Triple(Icons.Default.Description, "Format", it)) }
                    add(Triple(Icons.Default.Storage, "Size", size ?: "File unavailable"))
                }
            }
            add(Triple(Icons.Default.Schedule, "Saved", dateFormat.format(Date(item.createdAt))))
            // Edits bump updatedAt; ignore the few ms between the two defaults on insert
            if (item.updatedAt - item.createdAt > 1_000) {
                add(Triple(Icons.Default.EditCalendar, "Edited", dateFormat.format(Date(item.updatedAt))))
            }
        }
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Details",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            rows.forEach { (icon, label, value) ->
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        SelectionContainer {
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
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
