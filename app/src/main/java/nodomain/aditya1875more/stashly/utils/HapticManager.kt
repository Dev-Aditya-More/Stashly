package nodomain.aditya1875more.stashly.utils

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.runtime.Composable

enum class HapticEvent {
    TAP,
    SUCCESS,
    ERROR,
    LONG_PRESS
}
@Composable
fun rememberHapticManager(): (HapticEvent) -> Unit {
    val haptic = LocalHapticFeedback.current

    return { event ->
        when (event) {
            HapticEvent.TAP -> haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            HapticEvent.SUCCESS -> haptic.performHapticFeedback(HapticFeedbackType.ContextClick)
            HapticEvent.ERROR -> haptic.performHapticFeedback(HapticFeedbackType.Reject)
            HapticEvent.LONG_PRESS -> haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }
}