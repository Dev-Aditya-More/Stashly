package nodomain.aditya1875more.stashly.ui.components

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import nodomain.aditya1875more.stashly.R
import nodomain.aditya1875more.stashly.data.local.ContentType
import nodomain.aditya1875more.stashly.data.local.SavedItem
import nodomain.aditya1875more.stashly.ui.viewmodels.MainViewModel
import nodomain.aditya1875more.stashly.utils.getFileIconRes
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SavedItemCard(
    item: SavedItem,
    onDelete: (SavedItem) -> Unit,
    onSaveEdit: (SavedItem) -> Unit,
    onItemClick: (SavedItem) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf(item.title ?: "") }
    var editedText by remember { mutableStateOf(item.text ?: "") }
    var editedUrl by remember { mutableStateOf(item.url ?: "") }
    var editedPath by remember { mutableStateOf(item.filePath ?: "") }
    val viewModel: MainViewModel = koinViewModel()
    val context = LocalContext.current

    // Per-content-type color tokens
    val cardContainerColor = when (item.contentType) {
        ContentType.LINK -> MaterialTheme.colorScheme.tertiaryContainer
        ContentType.TEXT -> MaterialTheme.colorScheme.secondaryContainer
        ContentType.FILE -> MaterialTheme.colorScheme.surfaceContainerHigh
    }
    val titleColor = when (item.contentType) {
        ContentType.LINK -> MaterialTheme.colorScheme.onTertiaryContainer
        ContentType.TEXT -> MaterialTheme.colorScheme.onSecondaryContainer
        ContentType.FILE -> MaterialTheme.colorScheme.onSurface
    }
    val subtitleColor = when (item.contentType) {
        ContentType.LINK -> MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.65f)
        ContentType.TEXT -> MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.65f)
        ContentType.FILE -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val typeAccentColor = when (item.contentType) {
        ContentType.LINK -> MaterialTheme.colorScheme.tertiary
        ContentType.TEXT -> MaterialTheme.colorScheme.secondary
        ContentType.FILE -> MaterialTheme.colorScheme.primary
    }
    val typeName = when (item.contentType) {
        ContentType.LINK -> "Link"
        ContentType.TEXT -> "Note"
        ContentType.FILE -> "File"
    }

    if (isEditing) {
        AlertDialog(
            onDismissRequest = { isEditing = false },
            title = { Text("Edit Item") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editedTitle,
                        onValueChange = { editedTitle = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    when (item.contentType) {
                        ContentType.LINK -> {
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = editedUrl,
                                onValueChange = { editedUrl = it },
                                label = { Text("Link") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        ContentType.TEXT -> {
                            Spacer(Modifier.height(8.dp))
                            OutlinedTextField(
                                value = editedText,
                                onValueChange = { editedText = it },
                                label = { Text("Note") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        ContentType.FILE -> {
                            Spacer(Modifier.height(8.dp))
                            ReplaceFileButton(
                                onFileReplaced = { uri, fileName ->
                                    editedPath = uri.toString()
                                    val updatedItem = item.copy(
                                        filePath = editedPath,
                                        title = fileName
                                    )
                                    viewModel.editItem(updatedItem)
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    onSaveEdit(
                        item.copy(
                            title = editedTitle.ifBlank { item.title ?: "Untitled" },
                            text = editedText.ifBlank { item.text },
                            url = editedUrl.ifBlank { item.url },
                            filePath = editedPath.ifBlank { item.filePath }
                        )
                    )
                    isEditing = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditing = false }) { Text("Cancel") }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isEditing) { onItemClick(item) },
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = cardContainerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Content type badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = typeAccentColor.copy(alpha = 0.18f)
                ) {
                    Text(
                        text = typeName,
                        style = MaterialTheme.typography.labelSmall,
                        color = typeAccentColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            when (item.contentType) {
                ContentType.LINK -> {
                    Text(
                        text = item.title?.ifBlank { "Untitled Link" } ?: "Untitled Link",
                        style = MaterialTheme.typography.titleMedium,
                        color = titleColor,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = item.url?.ifBlank { "No link available" } ?: "No link available",
                        style = MaterialTheme.typography.bodySmall,
                        color = subtitleColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                ContentType.TEXT -> {
                    Text(
                        text = item.title?.ifBlank { "Untitled Note" } ?: "Untitled Note",
                        style = MaterialTheme.typography.titleMedium,
                        color = titleColor,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = item.text?.ifBlank { "No text found" } ?: "No text found",
                        style = MaterialTheme.typography.bodySmall,
                        color = subtitleColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                ContentType.FILE -> {
                    val fileName = item.title ?: getReadableFileName(
                        context,
                        item.filePath?.toUri() ?: "".toUri()
                    )
                    val iconRes = getFileIconRes(fileName)

                    Text(
                        text = fileName.ifBlank { "Unnamed File" },
                        style = MaterialTheme.typography.titleMedium,
                        color = titleColor,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = fileName.substringAfterLast('.', "Unknown").uppercase() + " file",
                            style = MaterialTheme.typography.bodySmall,
                            color = subtitleColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { isEditing = true }) {
                    Icon(
                        painter = painterResource(R.drawable.edit),
                        contentDescription = "Edit",
                        tint = typeAccentColor
                    )
                }
                IconButton(onClick = { onDelete(item) }) {
                    Icon(
                        painter = painterResource(R.drawable.delete),
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

fun getReadableFileName(context: Context, uri: Uri): String {
    return try {
        var name: String? = null
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    name = it.getString(nameIndex)
                }
            }
        }
        name ?: uri.lastPathSegment ?: "Unknown file"
    } catch (e: Exception) {
        uri.lastPathSegment ?: "Unknown file"
    }
}

fun getFileExtension(path: String?): String {
    if (path.isNullOrEmpty()) return ""
    return path.substringAfterLast('.', "")
}
