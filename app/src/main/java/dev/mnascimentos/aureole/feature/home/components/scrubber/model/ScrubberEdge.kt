package dev.mnascimentos.aureole.feature.home.components.scrubber.model

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Dp

/** Where the scrubber sits: which screen edge it hugs and how far the system gesture strip pushes it inward. */
data class ScrubberEdge(
    val isLeftHandedMode: Boolean,
    val gestureInset: Dp
)

/**
 * Width of the system gesture strip on the edge the scrubber hugs: the left inset for the left-handed mode, the
 * right one otherwise. Zero with button navigation, so nothing moves there.
 */
fun scrubberGestureInsetPx(isLeftHandedMode: Boolean, leftInsetPx: Int, rightInsetPx: Int): Int =
    (if (isLeftHandedMode) leftInsetPx else rightInsetPx).coerceAtLeast(0)

/** The whole touch area of the scrubber, in its own coordinates; the system back gesture must not take it. */
fun scrubberExclusionRect(widthPx: Float, heightPx: Float): Rect = Rect(0f, 0f, widthPx, heightPx)

/**
 * Width of the screen edge that starts a scrub from the home. The scrubber sits [insetPx] inside the edge, so
 * the zone grows by the same amount and still reaches the scrubber's own touch area.
 */
fun scrubberEdgeStartZonePx(baseWidthPx: Float, insetPx: Float): Float = baseWidthPx + insetPx.coerceAtLeast(0f)

/** Whether a touch at [x] starts in the scrubber's edge zone, on the left in the left-handed mode. */
fun isInScrubberEdgeStartZone(x: Float, screenWidthPx: Float, zonePx: Float, isLeftHandedMode: Boolean): Boolean =
    if (isLeftHandedMode) x < zonePx else x > screenWidthPx - zonePx
