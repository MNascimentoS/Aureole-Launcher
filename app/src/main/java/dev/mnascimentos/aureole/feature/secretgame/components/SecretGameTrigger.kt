package dev.mnascimentos.aureole.feature.secretgame.components

import android.os.SystemClock
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import dev.mnascimentos.aureole.feature.secretgame.model.SecretTapCounter

/**
 * Gesture of the hidden game on a text: [SecretGameTuning.TAPS_TO_OPEN] quick taps call [onOpenGame] (each tap
 * vibrates lightly, the last one harder); a long press shows the best score through [onShowBest] and is not a tap.
 */
@Composable
fun rememberSecretGameTrigger(onOpenGame: () -> Unit, onShowBest: () -> Unit): Modifier {
    val haptic = LocalHapticFeedback.current
    var counter by remember { mutableStateOf(SecretTapCounter()) }
    val openGame by rememberUpdatedState(onOpenGame)
    val showBest by rememberUpdatedState(onShowBest)
    return Modifier.pointerInput(Unit) {
        detectTapGestures(
            onTap = {
                val result = counter.register(SystemClock.uptimeMillis())
                counter = result.counter
                if (result.triggered) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    openGame()
                } else {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            },
            onLongPress = {
                counter = SecretTapCounter()
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                showBest()
            }
        )
    }
}
