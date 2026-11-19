package dev.mnascimentos.aureole.feature.home.components.overlays.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

/**
 * The showcase content: every touch stops here before the tiles see it, so none presses, ripples or scrolls.
 * [isBlocking] false leaves the content as it is.
 */
fun Modifier.blockingTouches(isBlocking: Boolean): Modifier {
    if (!isBlocking) return this
    return pointerInput(Unit) {
        awaitPointerEventScope {
            while (true) {
                awaitPointerEvent(PointerEventPass.Initial).changes.forEach { it.consume() }
            }
        }
    }
}
