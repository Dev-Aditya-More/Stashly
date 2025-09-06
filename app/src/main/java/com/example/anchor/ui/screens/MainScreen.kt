package com.example.anchor.ui.screens

import android.net.Uri
import android.webkit.URLUtil.isValidUrl
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import coil3.toUri
import com.example.anchor.data.local.ContentType
import com.example.anchor.data.local.SavedItem
import com.example.anchor.ui.components.InputField
import com.example.anchor.ui.components.LottieAnimationExample
import com.example.anchor.ui.components.SavedContentScreen
import com.example.anchor.ui.components.StashlyAppBar
import com.example.anchor.ui.components.UploadFileField
import com.example.anchor.ui.viewmodels.MainViewModel
import com.example.anchor.utils.classifyInput
import com.example.anchor.utils.normalizeUrl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MainScreen(animatedVisibilityScope: AnimatedVisibilityScope, navController: NavHostController, viewModel: MainViewModel = koinViewModel()) {
    var text by remember { mutableStateOf("") }
    val items by viewModel.items.collectAsState(initial = emptyList())
    var isError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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

                    scope.launch {
                        val type = withContext(Dispatchers.Default) { classifyInput(text) }

                        when (type) {
                            ContentType.LINK -> {
                                val normalized = normalizeUrl(text)
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

                                if(text.isBlank()){
                                    isError = true
                                }else {
                                    viewModel.saveText(
                                        SavedItem(
                                            text = text,
                                            contentType = ContentType.TEXT
                                        )
                                    )
                                }
                                text = ""
                            }

                            ContentType.FILE -> {
                                val uri = text.toUri()
                                val fileName = uri.lastPathSegment ?: "File"
                                viewModel.saveFile(
                                    SavedItem(
                                        contentType = ContentType.FILE,
                                        title = fileName,
                                        filePath = text
                                    ),
                                    context

                                )
                                text = ""
                            }

                            else -> {
                                isError = true
                            }
                        }
                    }
                },
                isError = isError
            )
            if (isError) {
                Text(
                    "please enter it properly",
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
                onFilePicked = { uri, context ->
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
                        ),
                        context
                    )
                }
            )

            Spacer(Modifier.height(16.dp))

            if(items.isEmpty()){

                LottieAnimationExample(
                   modifier = Modifier.size(300.dp).padding(top = 50.dp),
                )
            }
            else {
                SavedContentScreen(
                    animatedVisibilityScope = animatedVisibilityScope,
                    savedItems = items,
                    onDelete = { savedItem -> viewModel.removeItem(savedItem) },
                    onEdit = { savedItem -> viewModel.editItem(savedItem) },
                    onItemClick = { savedItem ->
                        // navigate to detail screen with the item's id
                        navController.navigate("detail/${savedItem.id}")
                    },
                    onNewFilePicked = { uri ->
                        val fileName = uri.lastPathSegment ?: "File"
                        val newItem = SavedItem(
                            contentType = ContentType.FILE,
                            title = fileName,
                            filePath = uri.toString()
                        )
                        viewModel.saveFile(newItem, context)
                    }
                )
            }
        }
    }
}

