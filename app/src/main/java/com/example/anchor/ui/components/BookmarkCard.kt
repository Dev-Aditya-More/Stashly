package com.example.anchor.ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
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
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.anchor.jsoup.BookmarkMetaData
import com.example.anchor.jsoup.fetchBookmarkMetadata
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddBookmarkSheet(
    onSave: (String, BookmarkMetaData?) -> Unit,
    onDismiss: () -> Unit
) {
    var url by remember { mutableStateOf("") }
    var metadata by remember { mutableStateOf<BookmarkMetaData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("Add Bookmark", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = url,
            onValueChange = {
                url = it
                metadata = null // reset
            },
            label = { Text("Enter bookmark URL") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                if (url.isNotBlank()) {
                    isLoading = true
                    scope.launch {
                        metadata = fetchBookmarkMetadata(url)
                        isLoading = false
                    }
                }
            },
            enabled = url.isNotBlank(),
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Preview")
        }

        Spacer(Modifier.height(16.dp))

        when {
            isLoading -> {
                LoadingIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            metadata != null -> {
                BookmarkPreviewCard(metadata!!)
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = { onSave(url, metadata) },
                enabled = url.isNotBlank(),
            ) {
                Text("Save")
            }
        }
    }
}


@Composable
fun BookmarkPreviewCard(metadata: BookmarkMetaData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (metadata.faviconUrl != null) {
                AsyncImage(
                    model = metadata.faviconUrl,
                    contentDescription = "Favicon",
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                val displayTitle = metadata.title
                    ?: metadata.url.toUri().host
                    ?: "Untitled"

                Text(
                    displayTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (!metadata.description.isNullOrBlank()) {
                    Text(
                        metadata.description,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}


