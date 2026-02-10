package nodomain.aditya1875more.stashly.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import nodomain.aditya1875more.stashly.data.local.ContentType
import nodomain.aditya1875more.stashly.ui.viewmodels.MainViewModel
import nodomain.aditya1875more.stashly.utils.HapticEvent
import nodomain.aditya1875more.stashly.utils.classifyInput
import nodomain.aditya1875more.stashly.utils.rememberHapticManager
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    isError: Boolean = false,
    showSaved: Boolean,
    viewModel: MainViewModel = koinViewModel()
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val haptics = rememberHapticManager()
    val loading by viewModel.isLoading.collectAsStateWithLifecycle()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                })
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { RotatingPlaceholder() },
                label = { Text("Start here") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                isError = isError,
                shape = RoundedCornerShape(10.dp),
                singleLine = false,
                maxLines = Int.MAX_VALUE,
                textStyle = LocalTextStyle.current.copy(lineHeight = 20.sp),
                trailingIcon = {
                    AnimatedVisibility(
                        visible = showSaved,
                        enter = fadeIn() + slideInVertically { it / 2 },
                        exit = fadeOut() + slideOutVertically { -it / 2 }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF0E830F))
                        }
                    }
                }
            )

            Spacer(Modifier.height(16.dp))

            val pulse by animateFloatAsState(
                targetValue = if (!loading) 1f else 0.98f,
                label = "stashPulse"
            )

//            val detectedType = remember(value) { classifyInput(value) }
//
//            if (value.count() > 4) {
//                AnimatedContent(
//                    targetState = detectedType,
//                    transitionSpec = {
//                        fadeIn(tween(200)) + slideInVertically { it / 2 } togetherWith
//                                fadeOut(tween(150)) + slideOutVertically { -it / 2 }
//                    },
//                    label = "TypeChipAnim"
//                ) { type ->
//                    val (label, icon) = when (type) {
//                        ContentType.LINK -> "Link" to Icons.Default.Link
//                        ContentType.TEXT -> "Note" to Icons.Default.Edit
//                        ContentType.FILE -> "File" to Icons.Default.AttachFile
//                    }
//
//                    AssistChip(
//                        onClick = {},
//                        label = { Text(label) },
//                        leadingIcon = {
//                            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
//                        }
//                    )
//                }
//            }

            AnimatedContent(
                targetState = loading,
                label = "StashButtonState"
            ) { isLoading ->
                if (isLoading) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(Modifier.width(12.dp))
                            Text("Stashing…", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            onSaveClick()
                            if (!isError) haptics(HapticEvent.SUCCESS) else haptics(HapticEvent.ERROR)
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer {
                                scaleX = pulse
                                scaleY = pulse
                            },
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Stash it",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = MaterialTheme.typography.titleMedium.fontWeight
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RotatingPlaceholder() {
    val hints = listOf(
        "Paste a link \n",
        "Write a note \n",
        "Maybe some text? \n",
        "Keep everything absolutely safe! \n"
    )

    var index by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            index = (index + 1) % hints.size
        }
    }

    AnimatedContent(
        targetState = hints[index],
        transitionSpec = {
            fadeIn(tween(250)) + slideInVertically { it / 2 } togetherWith
                    fadeOut(tween(200)) + slideOutVertically { -it / 2 }
        },
        label = "HintAnim"
    ) { text ->
        Text(
            text = "$text…",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.8f)
        )
    }
}

