package nodomain.aditya1875more.stashly.ui.screens

import android.webkit.URLUtil.isValidUrl
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import nodomain.aditya1875more.stashly.Screen
import nodomain.aditya1875more.stashly.data.local.ContentType
import nodomain.aditya1875more.stashly.data.local.SavedItem
import nodomain.aditya1875more.stashly.jsoup.LinkPreview
import nodomain.aditya1875more.stashly.jsoup.fetchLinkPreview
import nodomain.aditya1875more.stashly.ui.components.AddBookmarkSheet
import nodomain.aditya1875more.stashly.ui.components.InputField
import nodomain.aditya1875more.stashly.ui.components.LottieAnimationExample
import nodomain.aditya1875more.stashly.ui.components.SavedContentScreen
import nodomain.aditya1875more.stashly.ui.components.StashlyAppBar
import nodomain.aditya1875more.stashly.ui.components.StashlyBottomBar
import nodomain.aditya1875more.stashly.ui.components.UploadFileField
import nodomain.aditya1875more.stashly.ui.viewmodels.MainViewModel
import nodomain.aditya1875more.stashly.utils.autoCorrectUrl
import nodomain.aditya1875more.stashly.utils.classifyInput
import nodomain.aditya1875more.stashly.utils.normalizeUrl
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun MainScreen(navController: NavHostController, viewModel: MainViewModel = koinViewModel()) {
    var text by remember { mutableStateOf("") }
    val items by viewModel.items.collectAsState(initial = emptyList())
    var isError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showAddBookmarkSheet by remember { mutableStateOf(false) }
    val loading by viewModel.isLoading.collectAsState()
    var fetchMetadata by remember { mutableStateOf<LinkPreview?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { StashlyAppBar() },
        bottomBar = { StashlyBottomBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddBookmarkSheet = true }) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Localized description"
                )
            }
        }
    ) { paddingValues ->

        Box(modifier = Modifier.fillMaxSize()) {

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
                        fetchMetadata = null
                    },
                    onSaveClick = {
                        scope.launch {
                            val type = classifyInput(text)
                            when (type) {
                                ContentType.LINK -> {
                                    val normalized = normalizeUrl(text)
                                    val corrected = autoCorrectUrl(normalized)

                                    if (isValidUrl(corrected)) {
                                        scope.launch {
                                            fetchMetadata = fetchLinkPreview(corrected)
                                        }

                                        viewModel.saveLink(
                                            SavedItem(
                                                url = corrected,
                                                contentType = ContentType.LINK,
                                            ), fetchMetadata
                                        )
                                        text = ""
                                    } else isError = true
                                }
                                ContentType.TEXT -> {
                                    if (text.isBlank()) {
                                        isError = true
                                    } else {
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
                        "",
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
                        viewModel.saveFile(
                            SavedItem(
                                contentType = ContentType.FILE,
                                title = fileName,
                                filePath = uri.toString()
                            ), context
                        )
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

                if (showAddBookmarkSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showAddBookmarkSheet = false }
                    ) {
                        AddBookmarkSheet(
                            onSave = { url, metadata ->
                                val normalized = normalizeUrl(url)
                                val corrected = autoCorrectUrl(normalized)
                                if (isValidUrl(corrected)) {
                                    viewModel.saveBookmark(corrected, metadata)
                                    showAddBookmarkSheet = false
                                }
                            },
                            onDismiss = { showAddBookmarkSheet = false }
                        )
                    }
                }
            }

            // Overlay loading indicator
            if (loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularWavyProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
