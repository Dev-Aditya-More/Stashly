package com.example.anchor.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.delay

@Composable
fun TypingText(
    fullText: String,
    typingSpeed: Long = 50L // ms per character
) {
    var text by remember { mutableStateOf("") }
    LaunchedEffect(fullText) {
        text = ""
        fullText.forEachIndexed { index, _ ->
            text = fullText.take(index + 1)
            delay(typingSpeed)
        }
    }

    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}
