package dev.mnascimentos.aureole.feature.home.components.widgets.model

import dev.mnascimentos.aureole.composable.reorder.model.reorderStepTarget
import kotlin.math.ceil

/** Cards that fit across the stack preview: one whole card and half of the second card show in the carousel. */
const val WIDGET_CARDS_VISIBLE = 1.5f

/** Height of a stack card as a share of its width. */
const val WIDGET_CARD_HEIGHT_RATIO = 0.82f

/**
 * Width of a stack card so that [visibleCards] fit in a row of [rowWidth] with a [margin] on both sides and a [gap]
 * between neighbours.
 */
fun widgetCardWidth(rowWidth: Float, margin: Float, gap: Float, visibleCards: Float = WIDGET_CARDS_VISIBLE): Float {
    val wholeCards = ceil(visibleCards).toInt()
    val usable = rowWidth - 2f * margin - (wholeCards - 1) * gap
    return (usable / visibleCards).coerceAtLeast(0f)
}

/** Height of a stack card of [width]. */
fun widgetCardHeight(width: Float): Float = width * WIDGET_CARD_HEIGHT_RATIO

/**
 * Where the widget [id] goes when the user taps "move" without dragging: one step towards the start or the end of
 * [widgets]. Null when the widget is not in the stack or already sits at that end.
 */
fun widgetStepTarget(widgets: List<Int>, id: Int, towardsStart: Boolean): Int? {
    val index = widgets.indexOf(id)
    if (index < 0) return null
    return reorderStepTarget(index, widgets.size, towardsStart)
}

/** Geometry of the stack row for [revealScrollTarget]: the card [index] to show and the sizes in pixels. */
data class RevealScrollParams(
    val index: Int,
    val slotPx: Float,
    val cardPx: Float,
    val marginPx: Float,
    val viewportPx: Float
)

/**
 * Scroll offset that brings card [RevealScrollParams.index] fully into a row of
 * [RevealScrollParams.viewportPx] already scrolled to [current]: the same offset when the card is whole, otherwise
 * the closest one that shows it with the row [RevealScrollParams.marginPx] around it.
 * [RevealScrollParams.slotPx] is a card plus the gap, [RevealScrollParams.cardPx] only the card.
 */
fun revealScrollTarget(current: Int, params: RevealScrollParams): Int = with(params) {
    val mostScrolled = index * slotPx
    val leastScrolled = marginPx + index * slotPx + cardPx + marginPx - viewportPx
    current.toFloat().coerceIn(minOf(leastScrolled, mostScrolled), mostScrolled).toInt()
}
