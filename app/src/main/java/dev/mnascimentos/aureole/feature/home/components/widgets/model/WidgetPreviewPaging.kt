package dev.mnascimentos.aureole.feature.home.components.widgets.model

import kotlin.math.roundToInt

/**
 * Index of the preview card closest to the start of a row scrolled [scrollPx] where each card takes [slotPx] (card plus
 * spacing), clamped to the [count] cards; 0 for an empty row or an unmeasured slot.
 */
fun widgetPreviewPage(scrollPx: Int, slotPx: Float, count: Int): Int {
    if (count <= 0 || slotPx <= 0f) return 0
    return (scrollPx / slotPx).roundToInt().coerceIn(0, count - 1)
}
