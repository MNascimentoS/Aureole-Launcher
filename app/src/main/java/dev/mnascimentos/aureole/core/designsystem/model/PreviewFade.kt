package dev.mnascimentos.aureole.core.designsystem.model

/**
 * Opacity of the preview above an overlay sheet for how far the sheet is expanded ([expandFraction], 0 at rest and 1
 * expanded): it only fades, linearly and without extra easing, and never scales, so it is never cut at the sides
 * (ajustes-review-aislan-2 RF-G2, D-04). Out-of-range fractions (an overshooting spring) are clamped to 0..1.
 */
fun previewAlpha(expandFraction: Float): Float = (1f - expandFraction).coerceIn(0f, 1f)

/** At this opacity the preview is gone: it is not placed, so it takes no touch. */
fun isPreviewHidden(alpha: Float): Boolean = alpha <= 0f
