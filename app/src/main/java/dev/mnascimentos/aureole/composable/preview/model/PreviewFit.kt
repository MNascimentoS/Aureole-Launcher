package dev.mnascimentos.aureole.composable.preview.model

/**
 * Scale of the editor preview of a component measured [widthDp] × [heightDp] on the home (the clock, the app &
 * folder group), in a room [roomWidthDp] × [roomHeightDp]: the real size (1) when it fits, shrunk just enough when it
 * does not, never grown. A room with no bound in one direction does not limit it.
 */
fun previewFitScale(widthDp: Float, heightDp: Float, roomWidthDp: Float, roomHeightDp: Float): Float {
    if (widthDp <= 0f || heightDp <= 0f) return 1f
    val widthScale = if (roomWidthDp.isFinite()) roomWidthDp / widthDp else 1f
    val heightScale = if (roomHeightDp.isFinite()) roomHeightDp / heightDp else 1f
    return minOf(1f, widthScale, heightScale).coerceAtLeast(0f)
}
