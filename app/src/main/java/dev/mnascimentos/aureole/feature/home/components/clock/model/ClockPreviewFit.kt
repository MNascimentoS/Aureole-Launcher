package dev.mnascimentos.aureole.feature.home.components.clock.model

/**
 * Scale of the editor preview of a clock measured [clockWidthDp] × [clockHeightDp] on the home, in a room
 * [roomWidthDp] × [roomHeightDp] (RF-B5): the real size (1) when it fits, shrunk just enough when it does not, never
 * grown. A room with no bound in one direction does not limit it.
 */
fun clockPreviewScale(clockWidthDp: Float, clockHeightDp: Float, roomWidthDp: Float, roomHeightDp: Float): Float {
    if (clockWidthDp <= 0f || clockHeightDp <= 0f) return 1f
    val widthScale = if (roomWidthDp.isFinite()) roomWidthDp / clockWidthDp else 1f
    val heightScale = if (roomHeightDp.isFinite()) roomHeightDp / clockHeightDp else 1f
    return minOf(1f, widthScale, heightScale).coerceAtLeast(0f)
}
