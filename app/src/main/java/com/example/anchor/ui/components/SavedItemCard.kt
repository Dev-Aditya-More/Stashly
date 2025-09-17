package com.example.anchor.ui.components

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.anchor.data.local.ContentType
import com.example.anchor.data.local.SavedItem
import com.example.anchor.ui.viewmodels.MainViewModel
import com.example.anchor.utils.getFileIconRes
import com.example.stashly.R
import org.koin.compose.viewmodel.koinViewModel
import java.io.File

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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isEditing) { onItemClick(item) }
            .padding(horizontal = 12.dp)
            .graphicsLayer {
                alpha = 0.9f
                shadowElevation = 4f
                shape = RoundedCornerShape(20.dp)
                clip = true
            }
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.1f),
                        Color.Black.copy(alpha = 0.5f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.2f),
                        Color.Black.copy(alpha = 0.1f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        elevation = CardDefaults.cardElevation(2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isEditing) {
                // -------- EDIT MODE --------
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
                            label = { Text("link") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    ContentType.TEXT -> {
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editedText,
                            onValueChange = { editedText = it },
                            label = { Text("note") },
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

                Spacer(Modifier.height(8.dp))
                Row {
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
                    }) { Text("Save") }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { isEditing = false }) { Text("Cancel") }
                }
            } else {
                // -------- VIEW MODE --------
                when (item.contentType) {
                    ContentType.LINK -> {
                        Text(
                            text = item.title?.ifBlank { "Untitled Link" } ?: "Untitled Link",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.url?.ifBlank { "No link available" } ?: "No link available",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    ContentType.TEXT -> {
                        Text(
                            text = item.title?.ifBlank { "Untitled Note" } ?: "Untitled Note",
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = item.text?.ifBlank { "No text found" } ?: "No text found",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    ContentType.FILE -> {
                        val fileName = item.title ?: getReadableFileName(context,
                            item.filePath?.toUri() ?: "".toUri()
                        )
                        val fileExt = getFileExtension(item.filePath)
                        val iconRes = getFileIconRes(fileName)

                        Text(
                            text = fileName.ifBlank { "Unnamed File" },
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = painterResource(iconRes),
                                contentDescription = null,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = fileName.substringAfterLast('.', "Unknown").uppercase() + " file",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
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
                            tint = MaterialTheme.colorScheme.primary
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

fun getFileSizeText(path: String?): String {
    if (path.isNullOrEmpty()) return "Unknown size"
    return try {
        val uri = path.toUri()
        val file = File(uri.path!!)
        if (!file.exists()) return "Unknown size"
        val sizeKb = file.length() / 1024.0
        if (sizeKb < 1024) {
            "%.1f KB".format(sizeKb)
        } else {
            "%.1f MB".format(sizeKb / 1024)
        }
    } catch (e: Exception) {
        "Unknown size"
    }
}
