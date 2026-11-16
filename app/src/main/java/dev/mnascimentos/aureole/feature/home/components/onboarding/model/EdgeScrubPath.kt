package dev.mnascimentos.aureole.feature.home.components.onboarding.model

/**
 * Geometry of the edge scrub hint, in pixels: the finger is born on [edgeX] (half off screen), moves
 * [pullPx] inwards ([inwardSign] is 1 towards the right, -1 towards the left) at [startY], then slides to [endY].
 */
data class EdgeScrubPath(
    val edgeX: Float,
    val inwardSign: Float,
    val pullPx: Float,
    val startY: Float,
    val endY: Float
) {
    /** X reached at the end of the pull. */
    val pulledX: Float get() = edgeX + inwardSign * pullPx
}
