package com.example.anchor.ui.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.anchor.data.local.ContentType
import com.example.anchor.data.local.SavedItem
import com.example.anchor.ui.viewmodels.MainViewModel
import com.example.stashly.R
import org.koin.compose.viewmodel.koinViewModel
import androidx.core.net.toUri
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick(item) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
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
                            label = { Text("${item.contentType}".lowercase()) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    ContentType.TEXT -> {
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editedText,
                            onValueChange = { editedText = it },
                            label = { Text("${item.contentType}".lowercase()) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    ContentType.FILE -> {
                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = if (editedPath.isNotEmpty()) {
                                "Selected: ${getReadableFileName(editedPath)}"
                            } else {
                                "No file chosen"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )


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
                        onSaveEdit(item.copy(title = editedTitle, text = editedText))
                        isEditing = false
                    }) { Text("Save") }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = { isEditing = false }) { Text("Cancel") }
                }
            } else {
                // -------- VIEW MODE --------
                when (item.contentType) {
                    ContentType.LINK -> {
                        item.title?.let { TypingText(fullText = it) }
                        item.url?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    ContentType.TEXT -> {
                        if (item.title == null) {
                            LottieAnimationExample(
                                resId = R.raw.loading,
                                modifier = Modifier
                                    .size(40.dp)
                                    .fillMaxWidth()
                            )
                        } else {
                            TypingText(fullText = item.title!!)
                        }

                        item.text?.let {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        } ?: Text(text = "NO TEXT FOUND", color = Color.Red)
                    }

                    ContentType.FILE -> {
                        val fileName = getReadableFileName(item.filePath)
                        val fileExt = getFileExtension(item.filePath)
                        val fileSize = getFileSize(item.filePath) // e.g., "2.1 MB"

                        // File title (name shown big)
                        Text(
                            text = item.title ?: fileName,
                            style = MaterialTheme.typography.titleMedium,
                        )

                        // File details row (icon + type + size)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(R.drawable.ic_file),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = buildString {
                                    if (fileExt.isNotEmpty()) append(".$fileExt file")
                                    if (fileSize.isNotEmpty()) append(" • $fileSize")
                                },
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row {
                    IconButton(onClick = { isEditing = true }) {
                        Icon(
                            painterResource(R.drawable.edit),
                            contentDescription = "Edit",
                            tint = Color.Black
                        )
                    }
                    IconButton(onClick = { onDelete(item) }) {
                        Icon(
                            painterResource(R.drawable.delete),
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }
        }
    }
}

fun getReadableFileName(path: String?): String {
    if (path.isNullOrEmpty()) return "Unknown file"
    return path.toUri().lastPathSegment
        ?.substringAfterLast("/") ?: path
}

fun getFileExtension(path: String?): String {
    if (path.isNullOrEmpty()) return ""
    return path.substringAfterLast('.', "")
}

@Composable
fun getFileSize(path: String?): String {
    if (path.isNullOrEmpty()) return "0L"
    val context = LocalContext.current
    return try {
        val file = File(context.cacheDir, path.toUri().lastPathSegment ?: "tempFile")
        file.length().toString()
    } catch (e: Exception) {
        Text("File Not found")
        ""
    }
}