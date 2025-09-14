package com.example.anchor.ui.screens

import android.webkit.URLUtil.isValidUrl
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.anchor.Screen
import com.example.anchor.data.local.ContentType
import com.example.anchor.data.local.SavedItem
import com.example.anchor.ui.components.InputField
import com.example.anchor.ui.components.LottieAnimationExample
import com.example.anchor.ui.components.SavedContentScreen
import com.example.anchor.ui.components.StashlyAppBar
import com.example.anchor.ui.components.StashlyBottomBar
import com.example.anchor.ui.components.UploadFileField
import com.example.anchor.ui.viewmodels.MainViewModel
import com.example.anchor.utils.autoCorrectUrl
import com.example.anchor.utils.classifyInput
import com.example.anchor.utils.normalizeUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MainScreen(navController: NavHostController, viewModel: MainViewModel = koinViewModel()) {
    var text by remember { mutableStateOf("") }
    val items by viewModel.items.collectAsState(initial = emptyList())
    var isError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { StashlyAppBar() },
        bottomBar = { StashlyBottomBar(navController) }
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
                                val corrected = autoCorrectUrl(normalized)
                                if (isValidUrl(corrected)) {
                                    viewModel.saveLink(SavedItem(url = corrected, contentType = ContentType.LINK))
                                    text = ""
                                } else isError = true
                            }
                            ContentType.TEXT -> {
                                if (text.isBlank()) {
                                    isError = true
                                } else {
                                    viewModel.saveText(SavedItem(text = text, contentType = ContentType.TEXT))
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
                                    ), context
                                )
                                text = ""
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

            Spacer(Modifier.height(16.dp))
            Text("or", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))

            UploadFileField(
                modifier = Modifier.fillMaxWidth(),
                onFilePicked = { uri, context ->
                    val fileName = uri.lastPathSegment ?: "File"
                    viewModel.saveFile(SavedItem(contentType = ContentType.FILE, title = fileName, filePath = uri.toString()), context)
                }
            )

            Spacer(Modifier.height(16.dp))

            if (items.isEmpty()) {
                LottieAnimationExample(
                    modifier = Modifier.size(300.dp).padding(top = 50.dp),
                )
            } else {
                SavedContentScreen(
                    savedItems = items,
                    onDelete = { savedItem -> viewModel.removeItem(savedItem) },
                    onEdit = { savedItem -> viewModel.editItem(savedItem) },
                    onItemClick = { item ->
                        coroutineScope.launch {
                            navController.navigate(Screen.Detail.createRoute(item.id))
                        }
                    },
                    onSeeMore = {
                        coroutineScope.launch {
                            navController.navigate(Screen.Items.route)
                        }
                    }
                )
            }
        }
    }
}
