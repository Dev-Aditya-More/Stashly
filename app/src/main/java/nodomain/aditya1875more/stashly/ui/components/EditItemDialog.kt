package nodomain.aditya1875more.stashly.ui.components

import android.webkit.URLUtil.isValidUrl
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import nodomain.aditya1875more.stashly.data.local.ContentType
import nodomain.aditya1875more.stashly.data.local.SavedItem
import nodomain.aditya1875more.stashly.utils.autoCorrectUrl
import nodomain.aditya1875more.stashly.utils.normalizeUrl

/**
 * Edits a saved item. Nothing is written until Save is pressed — a replaced file is only
 * held as pending state, so Cancel really does discard it.
 */
@Composable
fun EditItemDialog(
    item: SavedItem,
    onDismiss: () -> Unit,
    onSave: (SavedItem) -> Unit
) {
    // State is dropped when the dialog leaves composition, so each open starts from the saved item
    var editedTitle by rememberSaveable(item.id) { mutableStateOf(item.title ?: "") }
    var editedText by rememberSaveable(item.id) { mutableStateOf(item.text ?: "") }
    var editedUrl by rememberSaveable(item.id) { mutableStateOf(item.url ?: "") }
    var editedPath by rememberSaveable(item.id) { mutableStateOf(item.filePath ?: "") }
    var replacedFileName by rememberSaveable(item.id) { mutableStateOf<String?>(null) }

    val correctedUrl = if (item.contentType == ContentType.LINK && editedUrl.isNotBlank()) {
        autoCorrectUrl(normalizeUrl(editedUrl.trim()))
    } else null
    val urlError = item.contentType == ContentType.LINK &&
            (correctedUrl == null || !isValidUrl(correctedUrl))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit item") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = editedTitle,
                    onValueChange = { editedTitle = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                when (item.contentType) {
                    ContentType.LINK -> {
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editedUrl,
                            onValueChange = { editedUrl = it },
                            label = { Text("Link") },
                            singleLine = true,
                            isError = urlError,
                            supportingText = if (urlError) {
                                { Text("Enter a valid link") }
                            } else null,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editedText,
                            onValueChange = { editedText = it },
                            label = { Text("Description") },
                            minLines = 2,
                            maxLines = 5,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    ContentType.TEXT -> {
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = editedText,
                            onValueChange = { editedText = it },
                            label = { Text("Note") },
                            minLines = 4,
                            maxLines = 10,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    ContentType.FILE -> {
                        Spacer(Modifier.height(8.dp))
                        ReplaceFileButton(
                            onFileReplaced = { uri, fileName ->
                                editedPath = uri.toString()
                                replacedFileName = fileName
                                // Follow the new file's name unless the user already renamed it
                                if (editedTitle == (item.title ?: "")) editedTitle = fileName
                            }
                        )
                        replacedFileName?.let {
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "Will replace with: $it",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !urlError,
                onClick = {
                    val fallbackTitle = replacedFileName ?: item.title ?: "Untitled"
                    onSave(
                        item.copy(
                            title = editedTitle.trim().ifBlank { fallbackTitle },
                            text = when (item.contentType) {
                                // Notes need a body; descriptions are allowed to be cleared
                                ContentType.TEXT -> editedText.ifBlank { item.text }
                                ContentType.LINK -> editedText.trim().ifBlank { null }
                                ContentType.FILE -> item.text
                            },
                            url = correctedUrl ?: item.url,
                            filePath = editedPath.ifBlank { item.filePath },
                            updatedAt = System.currentTimeMillis()
                        )
                    )
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
