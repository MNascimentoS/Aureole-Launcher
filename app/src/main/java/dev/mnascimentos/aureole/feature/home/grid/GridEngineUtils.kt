package dev.mnascimentos.aureole.feature.home.grid

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import kotlin.math.roundToInt

data class GridLimits(
    val maxCols: Int = GridEngineUtils.DEFAULT_MAX_COLS,
    val maxRows: Int = GridEngineUtils.DEFAULT_MAX_ROWS
)

data class SnapParams(
    val xPx: Float,
    val yPx: Float,
    val cellWidthPx: Float,
    val cellHeightPx: Float,
    val colSpan: Int,
    val rowSpan: Int
)

data class SlotSearchRequest(
    val targetColSpan: Int,
    val targetRowSpan: Int,
    val minColSpan: Int = 1,
    val minRowSpan: Int = 1,
    val limits: GridLimits = GridLimits()
)

data class SlotSearchParams(
    val targetCol: Int,
    val targetRow: Int,
    val item: LauncherItemState,
    val items: List<LauncherItemState>,
    val limits: GridLimits,
    val maxCol: Int,
    val maxRow: Int
)

object GridEngineUtils {

    const val PORTRAIT_MAX_COLS = 10
    const val PORTRAIT_MAX_ROWS = 20

    const val LANDSCAPE_MAX_COLS = 20
    const val LANDSCAPE_MAX_ROWS = 10

    const val DEFAULT_MAX_COLS = PORTRAIT_MAX_COLS
    const val DEFAULT_MAX_ROWS = PORTRAIT_MAX_ROWS

    fun constrainItemsToBounds(items: List<LauncherItemState>, limits: GridLimits): List<LauncherItemState> {
        val constrainedList = mutableListOf<LauncherItemState>()
        for (item in items) {
            val newColSpan = item.colSpan.coerceAtMost(limits.maxCols)
            val newRowSpan = item.rowSpan.coerceAtMost(limits.maxRows)
            val newCol = item.col.coerceIn(0, limits.maxCols - newColSpan)
            val newRow = item.row.coerceIn(0, limits.maxRows - newRowSpan)

            constrainedList.add(
                item.copy(
                    col = newCol,
                    row = newRow,
                    colSpan = newColSpan,
                    rowSpan = newRowSpan
                )
            )
        }
        return constrainedList
    }

    fun hasAABBCollision(itemA: LauncherItemState, itemB: LauncherItemState): Boolean {
        return itemA.col < itemB.col + itemB.colSpan &&
            itemA.col + itemA.colSpan > itemB.col &&
            itemA.row < itemB.row + itemB.rowSpan &&
            itemA.row + itemA.rowSpan > itemB.row
    }

    fun checkCollisionWithOthers(
        target: LauncherItemState,
        items: List<LauncherItemState>
    ): Boolean {
        return items.any { other ->
            other.id != target.id && hasAABBCollision(target, other)
        }
    }

    fun isWithinBounds(
        col: Int,
        row: Int,
        colSpan: Int,
        rowSpan: Int,
        limits: GridLimits = GridLimits()
    ): Boolean {
        return col >= 0 &&
            row >= 0 &&
            col + colSpan <= limits.maxCols &&
            row + rowSpan <= limits.maxRows
    }

    fun calculateSnapCell(
        params: SnapParams,
        limits: GridLimits = GridLimits()
    ): Pair<Int, Int> {
        if (params.cellWidthPx <= 0f || params.cellHeightPx <= 0f) return Pair(0, 0)

        val targetCol = (params.xPx / params.cellWidthPx).roundToInt()
            .coerceIn(0, (limits.maxCols - params.colSpan).coerceAtLeast(0))
        val targetRow = (params.yPx / params.cellHeightPx).roundToInt()
            .coerceIn(0, (limits.maxRows - params.rowSpan).coerceAtLeast(0))

        return Pair(targetCol, targetRow)
    }

    fun findFirstAvailableSlot(
        colSpan: Int,
        rowSpan: Int,
        items: List<LauncherItemState>,
        limits: GridLimits = GridLimits()
    ): Pair<Int, Int>? {
        val dummyId = "temp_find_slot"
        for (r in 0..(limits.maxRows - rowSpan)) {
            for (c in 0..(limits.maxCols - colSpan)) {
                val candidate = LauncherItemState(
                    id = dummyId,
                    type = LauncherItemType.CLOCK,
                    col = c,
                    row = r,
                    colSpan = colSpan,
                    rowSpan = rowSpan
                )
                if (!checkCollisionWithOthers(candidate, items) && isWithinBounds(c, r, colSpan, rowSpan, limits)) {
                    return Pair(c, r)
                }
            }
        }
        return null
    }

    fun findLargestAvailableSlot(
        request: SlotSearchRequest,
        items: List<LauncherItemState>
    ): Triple<Int, Int, Pair<Int, Int>>? {
        val limits = request.limits
        val safeMinColSpan = request.minColSpan.coerceIn(1, limits.maxCols)
        val safeMinRowSpan = request.minRowSpan.coerceIn(1, limits.maxRows)
        val safeTargetColSpan = request.targetColSpan.coerceIn(safeMinColSpan, limits.maxCols)
        val safeTargetRowSpan = request.targetRowSpan.coerceIn(safeMinRowSpan, limits.maxRows)

        val candidates = mutableListOf<Pair<Int, Int>>()
        for (cSpan in safeTargetColSpan downTo safeMinColSpan) {
            for (rSpan in safeTargetRowSpan downTo safeMinRowSpan) {
                candidates.add(Pair(cSpan, rSpan))
            }
        }
        candidates.sortWith(
            compareByDescending<Pair<Int, Int>> { it.first * it.second }
                .thenByDescending { it.first }
                .thenByDescending { it.second }
        )

        for ((cSpan, rSpan) in candidates) {
            val slot = findFirstAvailableSlot(cSpan, rSpan, items, limits)
            if (slot != null) {
                return Triple(cSpan, rSpan, slot)
            }
        }
        return null
    }

    fun findNearestValidSlot(
        targetCol: Int,
        targetRow: Int,
        item: LauncherItemState,
        items: List<LauncherItemState>,
        limits: GridLimits = GridLimits()
    ): Pair<Int, Int>? {
        val maxCol = (limits.maxCols - item.colSpan).coerceAtLeast(0)
        val maxRow = (limits.maxRows - item.rowSpan).coerceAtLeast(0)
        val clampedCol = targetCol.coerceIn(0, maxCol)
        val clampedRow = targetRow.coerceIn(0, maxRow)

        if (isCandidateSlotValid(clampedCol, clampedRow, item, items, limits)) {
            return Pair(clampedCol, clampedRow)
        }

        return searchNearestSlot(
            SlotSearchParams(targetCol, targetRow, item, items, limits, maxCol, maxRow)
        )
    }

    private fun searchNearestSlot(params: SlotSearchParams): Pair<Int, Int>? {
        val validSlots = mutableListOf<Pair<Int, Int>>()
        for (r in 0..params.maxRow) {
            for (c in 0..params.maxCol) {
                if (isCandidateSlotValid(c, r, params.item, params.items, params.limits)) {
                    validSlots.add(Pair(c, r))
                }
            }
        }
        return validSlots.minByOrNull { (c, r) ->
            val dc = c - params.targetCol
            val dr = r - params.targetRow
            dc * dc + dr * dr
        }
    }

    private fun isCandidateSlotValid(
        col: Int,
        row: Int,
        item: LauncherItemState,
        items: List<LauncherItemState>,
        limits: GridLimits
    ): Boolean {
        val candidate = item.copy(col = col, row = row)
        return !checkCollisionWithOthers(candidate, items) &&
            isWithinBounds(col, row, item.colSpan, item.rowSpan, limits)
    }
}
