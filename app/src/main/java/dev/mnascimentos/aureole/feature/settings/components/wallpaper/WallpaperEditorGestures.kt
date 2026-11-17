package dev.mnascimentos.aureole.feature.settings.components.wallpaper

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import dev.mnascimentos.aureole.feature.settings.model.EditorGestureCallbacks
import kotlin.math.abs

/**
 * Drag, pinch and double tap on the photo of the editor. [callbacks] is read on every event, so it may change
 * between recompositions without restarting the gestures. A single tap does nothing (it must not close anything).
 */
internal fun Modifier.editorGestures(callbacks: () -> EditorGestureCallbacks): Modifier = this
    .pointerInput(Unit) {
        detectEditorTransform(
            onTransform = { zoomBy, centroid, pan -> callbacks().onTransform(zoomBy, centroid, pan) },
            onEnd = { callbacks().onEnd() }
        )
    }
    .pointerInput(Unit) { detectTapGestures(onDoubleTap = { callbacks().onDoubleTap(it) }) }

/**
 * Like `detectTransformGestures`, plus [onEnd] when the fingers lift. Nothing moves until the fingers pass the
 * touch slop, so the small shake of a double tap does not drag the photo (or cancel the double tap).
 */
private suspend fun PointerInputScope.detectEditorTransform(
    onTransform: (zoomBy: Float, centroid: Offset, pan: Offset) -> Unit,
    onEnd: () -> Unit
) {
    awaitEachGesture {
        var zoom = 1f
        var pan = Offset.Zero
        var pastSlop = false
        var active = true
        awaitFirstDown(requireUnconsumed = false)
        while (active) {
            val event = awaitPointerEvent()
            val zoomChange = event.calculateZoom()
            val panChange = event.calculatePan()
            if (!pastSlop) {
                zoom *= zoomChange
                pan += panChange
                val zoomMotion = abs(1f - zoom) * event.calculateCentroidSize(useCurrent = false)
                pastSlop = zoomMotion > viewConfiguration.touchSlop || pan.getDistance() > viewConfiguration.touchSlop
            }
            if (pastSlop && (zoomChange != 1f || panChange != Offset.Zero)) {
                onTransform(zoomChange, event.calculateCentroid(useCurrent = false), panChange)
                event.changes.forEach { if (it.positionChanged()) it.consume() }
            }
            active = event.changes.any { it.pressed }
        }
        if (pastSlop) onEnd()
    }
}
