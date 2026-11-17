package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintEdge
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintGrid
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintItem
import kotlin.math.ceil

/** Delay before the hint text folds into the save pill when the user has not touched anything. */
const val EDIT_HINT_TEXT_MS = 3000L

fun List<LauncherItemState>.toEditHintGrid(maxRows: Int) =
    EditHintGrid(items = map { EditHintItem(row = it.row, rowSpan = it.rowSpan) }, maxRows = maxRows)

/** How many grid rows a strip [bandDp] tall covers on a grid of [maxRows] rows over [totalDp]; at least one. */
fun editHintBandRows(bandDp: Float, totalDp: Float, maxRows: Int): Int {
    if (totalDp <= 0f || maxRows <= 0) return 1
    return ceil(bandDp / (totalDp / maxRows)).toInt().coerceIn(1, maxRows)
}

/** Rows of [items] that fall inside the strip [from] until [to] (exclusive), summed over the items. */
private fun List<EditHintItem>.overlap(from: Int, to: Int): Int = sumOf { item ->
    (minOf(item.row + item.rowSpan, to) - maxOf(item.row, from)).coerceAtLeast(0)
}

/**
 * The edge with less grid covered under it: the bottom (thumb zone) unless items sit there and the top is
 * freer. A tie keeps the bottom.
 */
fun editHintEdge(grid: EditHintGrid, bandRows: Int): EditHintEdge {
    val bottom = grid.items.overlap(from = grid.maxRows - bandRows, to = grid.maxRows)
    val top = grid.items.overlap(from = 0, to = bandRows)
    return if (bottom > top) EditHintEdge.TOP else EditHintEdge.BOTTOM
}
