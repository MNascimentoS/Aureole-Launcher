package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintBand
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintEdge
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintGrid
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintItem
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintSide
import kotlin.math.ceil

/** Delay before the hint text folds into the save pill when the user has not touched anything. */
const val EDIT_HINT_TEXT_MS = 3000L

fun List<LauncherItemState>.toEditHintGrid(maxRows: Int, maxCols: Int = 0) = EditHintGrid(
    items = map { EditHintItem(row = it.row, rowSpan = it.rowSpan, col = it.col, colSpan = it.colSpan) },
    maxRows = maxRows,
    maxCols = maxCols
)

/**
 * The columns a strip [bandDp] wide covers on a grid of [maxCols] columns over [totalDp]: centered in portrait,
 * on the thumb side in landscape (right; left for left-handed). Null (every column) when the grid is unknown.
 */
fun editHintBandCols(bandDp: Float, totalDp: Float, maxCols: Int, side: EditHintSide): IntRange? {
    if (totalDp <= 0f || maxCols <= 0) return null
    val cols = ceil(bandDp / (totalDp / maxCols)).toInt().coerceIn(1, maxCols)
    val start = when (side) {
        EditHintSide.CENTER -> (maxCols - cols) / 2
        EditHintSide.END -> maxCols - cols
        EditHintSide.START -> 0
    }
    return start until start + cols
}

/** How many grid rows a strip [bandDp] tall covers on a grid of [maxRows] rows over [totalDp]; at least one. */
fun editHintBandRows(bandDp: Float, totalDp: Float, maxRows: Int): Int {
    if (totalDp <= 0f || maxRows <= 0) return 1
    return ceil(bandDp / (totalDp / maxRows)).toInt().coerceIn(1, maxRows)
}

private fun EditHintItem.coversAny(cols: IntRange?): Boolean =
    cols == null || colSpan <= 0 || (col < cols.last + 1 && col + colSpan > cols.first)

/** Rows of [items] that fall inside the strip [from] until [to] (exclusive) and under [cols], summed. */
private fun List<EditHintItem>.overlap(from: Int, to: Int, cols: IntRange?): Int =
    filter { it.coversAny(cols) }.sumOf { item ->
        (minOf(item.row + item.rowSpan, to) - maxOf(item.row, from)).coerceAtLeast(0)
    }

/**
 * The edge with less grid covered under the hint: the bottom (thumb zone) unless the edited item sits under
 * the pill there and the top is freer. A tie keeps the bottom.
 */
fun editHintEdge(grid: EditHintGrid, band: EditHintBand): EditHintEdge {
    val bottom = grid.items.overlap(from = grid.maxRows - band.rows, to = grid.maxRows, cols = band.cols)
    val top = grid.items.overlap(from = 0, to = band.rows, cols = band.cols)
    return if (bottom > top) EditHintEdge.TOP else EditHintEdge.BOTTOM
}

fun editHintEdge(grid: EditHintGrid, bandRows: Int): EditHintEdge = editHintEdge(grid, EditHintBand(bandRows))
