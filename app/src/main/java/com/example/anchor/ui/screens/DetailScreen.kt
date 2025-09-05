package com.example.anchor.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.anchor.data.local.ContentType
import com.example.anchor.ui.viewmodels.MainViewModel
import org.koin.compose.viewmodel.koinViewModel
import androidx.core.net.toUri

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SharedTransitionScope.DetailScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    itemId: Int,
    navController: NavController,
    viewModel: MainViewModel = koinViewModel()
) {
    val item = viewModel.getItemById(itemId) ?: return
    var isEditing by remember { mutableStateOf(false) }
    var editedTitle by remember { mutableStateOf(item.title ?: "") }
    var editedText by remember { mutableStateOf(item.text ?: "") }
    val context = LocalContext.current

    val sharedContentState = rememberSharedContentState("card_${item.id}")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .sharedElement(
                        sharedContentState,
                        animatedVisibilityScope,
                        boundsTransform = { _, _ ->
                            tween(durationMillis = 1000)
                        }
                    ),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(16.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // ---------- EDIT MODE ----------
                    if (isEditing) {
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
                                viewModel.editItem(
                                    item.copy(
                                        title = editedTitle,
                                        text = editedText
                                    )
                                )
                                isEditing = false
                            }) { Text("Save") }

                            Spacer(Modifier.width(8.dp))
                            TextButton(onClick = { isEditing = false }) { Text("Cancel") }
                        }
                    } else {

                        // ---------- VIEW MODE ----------
                        when (item.contentType) {

                            ContentType.LINK -> {
                                item.title?.let {
                                    Text(it, style = MaterialTheme.typography.headlineMedium)
                                }
                                Spacer(Modifier.height(4.dp))
                                item.url?.let { url ->
                                    ClickableText(
                                        text = AnnotatedString(url),
                                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary),
                                        onClick = { cont ->
                                            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                                            context.startActivity(intent)
                                        }
                                    )
                                }

                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(onClick = {
                                        val clipboard = context.getSystemService(
                                            Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Link", item.url ?: ""))
                                    }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                                    }

                                    IconButton(onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url ?: ""))
                                        context.startActivity(intent)
                                    }) {
                                        Icon(Icons.Default.OpenInBrowser, contentDescription = "Open")
                                    }

                                    IconButton(onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, item.url)
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, null))
                                    }) {
                                        Icon(Icons.Default.Share, contentDescription = "Share")
                                    }

                                    IconButton(onClick = { isEditing = true }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                }
                            }

                            ContentType.TEXT -> {
                                item.title?.let { Text(it, style = MaterialTheme.typography.headlineMedium) }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    item.text ?: "",
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Text", item.text ?: ""))
                                    }) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                                    }

                                    IconButton(onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, item.text)
                                            type = "text/plain"
                                        }
                                         context.startActivity(Intent.createChooser(sendIntent, null))
                                    }) {
                                        Icon(Icons.Default.Share, contentDescription = "Share")
                                    }

                                    IconButton(onClick = { isEditing = true }) {
                                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                                    }
                                }
                            }

                            ContentType.FILE -> {
                                Text(item.title ?: "File", style = MaterialTheme.typography.headlineMedium)
                                Spacer(Modifier.height(4.dp))
                                Text(item.filePath ?: "", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))

                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW).apply {
                                            setDataAndType(item.filePath?.toUri(), "*/*")
                                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                        }
                                        context.startActivity(intent)
                                    }) {
                                        Icon(Icons.Default.OpenInBrowser, contentDescription = "Open")
                                    }

                                    IconButton(onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            type = "*/*"
                                            putExtra(Intent.EXTRA_STREAM, item.filePath?.toUri())
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, null))
                                    }) {
                                        Icon(Icons.Default.Share, contentDescription = "Share")
                                    }

                                    IconButton(onClick = { viewModel.removeItem(item) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}