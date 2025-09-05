package com.example.anchor.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.anchor.data.local.ContentType
import com.example.anchor.data.local.SavedItem
import com.example.stashly.R

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SavedItemCard(
    animatedVisibilityScope: AnimatedVisibilityScope,
    item: SavedItem,
    onDelete: (SavedItem) -> Unit,
    onSaveEdit: (SavedItem) -> Unit,
    onItemClick: (SavedItem) -> Unit,
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf(item.title ?: "") }
    var editedText by remember { mutableStateOf(item.text ?: "") }
    val sharedContentState = rememberSharedContentState("card_${item.id}")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .sharedElement(
                sharedContentState,
                animatedVisibilityScope,
                boundsTransform = { _, _ ->
                    tween(durationMillis = 1000)
                }
            )
            .clickable { onItemClick(item) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
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
                if (item.contentType == ContentType.TEXT) {
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editedText,
                        onValueChange = { editedText = it },
                        label = { Text("Text") },
                        modifier = Modifier.fillMaxWidth()
                    )
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
                        Text(
                            text = item.title ?: "File",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        item.filePath?.let {
                            Text(
                                text = it,
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
