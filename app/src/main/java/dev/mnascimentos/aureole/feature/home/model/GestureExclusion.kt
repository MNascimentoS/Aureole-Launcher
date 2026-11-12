package dev.mnascimentos.aureole.feature.home.model

import androidx.compose.ui.unit.IntRect

/** Which part of the screen the system gestures (back swipe) must leave to the launcher. */
enum class GestureExclusion { NONE, FULL_SCREEN, RIGHT_EDGE, LEFT_EDGE }

/**
 * Single place that decides the gesture exclusion: the whole screen on the plain home, only the alphabet edge
 * while the drawer is open with nothing above it (the opposite edge keeps the back that closes the drawer), and
 * nothing for any other overlay, so the back gesture closes it.
 */
fun MainUiState.gestureExclusion(): GestureExclusion = when {
    isScrubberEdgeActive -> if (isLeftHandedMode) GestureExclusion.LEFT_EDGE else GestureExclusion.RIGHT_EDGE
    isOverlayActive -> GestureExclusion.NONE
    else -> GestureExclusion.FULL_SCREEN
}

/**
 * Rects to hand to the system for [exclusion] on a [widthPx] x [heightPx] area. [edgeInsetPx] is the width of the
 * system gesture strip on the edge of the exclusion; it is zero with button navigation, where there is nothing to
 * exclude on an edge.
 */
fun gestureExclusionRects(exclusion: GestureExclusion, widthPx: Int, heightPx: Int, edgeInsetPx: Int): List<IntRect> {
    val inset = edgeInsetPx.coerceIn(0, widthPx.coerceAtLeast(0))
    val isEmpty = widthPx <= 0 || heightPx <= 0
    val isEdge = exclusion == GestureExclusion.RIGHT_EDGE || exclusion == GestureExclusion.LEFT_EDGE
    val rect = when {
        isEmpty || exclusion == GestureExclusion.NONE || (isEdge && inset == 0) -> null
        exclusion == GestureExclusion.FULL_SCREEN -> IntRect(0, 0, widthPx, heightPx)
        exclusion == GestureExclusion.RIGHT_EDGE -> IntRect(widthPx - inset, 0, widthPx, heightPx)
        else -> IntRect(0, 0, inset, heightPx)
    }
    return listOfNotNull(rect)
}
