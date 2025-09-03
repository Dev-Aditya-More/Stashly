package com.example.anchor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.anchor.ui.components.LinkInputField
import com.example.anchor.ui.components.SavedContentScreen
import com.example.anchor.ui.components.StashlyAppBar
import com.example.anchor.ui.viewmodels.MainViewModel
import com.example.anchor.utils.toLinkEntity
import com.example.anchor.utils.toSavedItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    var text by remember { mutableStateOf("") }
    val links by viewModel.links.collectAsState(initial = emptyList())

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
            LinkInputField(
                value = text,
                onValueChange = { text = it },
                onSaveClick = {
                    if (text.isNotBlank()) {

                        viewModel.saveLink(text)
                        text = ""
                    }
                }
            )
            Spacer(Modifier.height(16.dp))

            if(links.isEmpty()){

                Text(
                    "Nothing saved yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            else {
                SavedContentScreen(
                    savedItems = links.toSavedItems(),
                    onDelete = { savedItem ->
                        val entity = links.find { it.url == savedItem.url }
                        if (entity != null) viewModel.removeLink(entity)
                    }
                )
            }
        }
    }
}

