package com.example.anchor.ui.screens

import android.net.Uri
import android.webkit.URLUtil.isValidUrl
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.toUri
import com.example.anchor.data.local.ContentType
import com.example.anchor.data.local.SavedItem
import com.example.anchor.ui.components.InputField
import com.example.anchor.ui.components.SavedContentScreen
import com.example.anchor.ui.components.StashlyAppBar
import com.example.anchor.ui.components.UploadFileField
import com.example.anchor.ui.viewmodels.MainViewModel
import com.example.anchor.utils.classifyInput
import com.example.anchor.utils.normalizeUrl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    var text by remember { mutableStateOf("") }
    val items by viewModel.items.collectAsState(initial = emptyList())
    var isError by remember { mutableStateOf(false) }

    Scaffold(

        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),

        topBar = {
            StashlyAppBar()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InputField(
                value = text,
                onValueChange = {
                    text = it
                    isError = false
                },
                onSaveClick = {
                    val type = classifyInput(text)

                    when (type) {
                        ContentType.LINK -> {
                            val normalized = normalizeUrl(text) // normalize only if it's a URL
                            if (isValidUrl(normalized)) {
                                viewModel.saveLink(
                                    SavedItem(
                                        url = normalized,
                                        contentType = ContentType.LINK
                                    )
                                )
                                text = ""
                            } else {
                                isError = true
                            }
                        }

                        ContentType.TEXT -> {
                            viewModel.saveText(
                                SavedItem(
                                    text = text,
                                    contentType = ContentType.TEXT
                                )
                            )
                            text = ""
                        }

                        else -> {
                            isError = true
                        }
                    }
                },
                isError = isError
            )
            if (isError) {
                Text(
                    "please enter a proper url",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Spacer(Modifier.height(8.dp))

            Text(
                "or",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            UploadFileField(
                modifier = Modifier.fillMaxWidth(),
                onFilePicked = { uri ->
                    val fileName = uri.lastPathSegment ?: "File"
                    val newItem = SavedItem(
                        contentType = ContentType.FILE,
                        title = fileName,
                        filePath = uri.toString()
                    )
                    viewModel.saveFile(
                        SavedItem(
                            contentType = ContentType.FILE,
                            title = newItem.title,
                            filePath = newItem.filePath
                        )
                    )
                }
            )

            Spacer(Modifier.height(16.dp))

            if(items.isEmpty()){

                Text(
                    "Nothing saved yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else {
                SavedContentScreen(
                    savedItems = items,
                    onDelete = { savedItem ->

                        viewModel.removeItem(savedItem)
                    }
                )
            }
        }
    }
}

