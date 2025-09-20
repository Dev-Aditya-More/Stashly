package nodomain.aditya1875more.stashly.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.with
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ExpandableText(
    text: String,
    minimizedMaxLines: Int = 2
) {
    var expanded by remember { mutableStateOf(false) }
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    var isOverflowing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(top = 4.dp)
    ) {
        AnimatedContent(
            targetState = expanded,
            transitionSpec = {
                (fadeIn(tween(300)) with fadeOut(tween(300)))
            },
            label = "expandableTextAnimation"
        ) { isExpanded ->
            Text(
                text = text,
                style = MaterialTheme.typography.bodySmall,
                maxLines = if (isExpanded) Int.MAX_VALUE else minimizedMaxLines,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f),
                onTextLayout = { layoutResult ->
                    textLayoutResult = layoutResult
                    if (!isExpanded) {
                        isOverflowing = layoutResult.hasVisualOverflow
                    }
                }
            )
        }

        if (isOverflowing || expanded) {
            Text(
                text = if (expanded) "Read less" else "Read more",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .padding(top = 2.dp)
                    .clickable { expanded = !expanded }
            )
        }
    }
}
