package dev.mnascimentos.aureole.feature.secretgame.components

import android.os.SystemClock
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameHostState
import dev.mnascimentos.aureole.feature.secretgame.model.SecretTapCounter

/**
 * Gesture of the hidden game on a text: [SecretGameTuning.TAPS_TO_OPEN] quick taps open the game through [host] (each
 * tap vibrates lightly, the last one harder); a long press shows the best score and is not a tap.
 */
fun Modifier.secretGameTrigger(host: SecretGameHostState, haptic: HapticFeedback): Modifier =
    pointerInput(host) {
        detectTapGestures(
            onTap = {
                val result = host.counter.register(SystemClock.uptimeMillis())
                host.counter = result.counter
                if (result.triggered) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    host.open()
                } else {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            },
            onLongPress = {
                host.counter = SecretTapCounter()
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                host.showBestScore = true
            }
        )
    }
