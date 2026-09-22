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

private const val DEFAULT_CLOCK_SPAN_X = 10
private const val DEFAULT_CLOCK_SPAN_Y = 6
private const val DEFAULT_PANEL_COL = 7
private const val DEFAULT_PANEL_ROW = 7
private const val DEFAULT_PANEL_SPAN_X = 3
private const val DEFAULT_PANEL_SPAN_Y = 7
private const val DEFAULT_APPS_ROW = 9
private const val DEFAULT_APPS_SPAN_X = 7
private const val DEFAULT_APPS_SPAN_Y = 11

private const val CLOCK_DEFAULT_COL_SPAN = 10
private const val CLOCK_DEFAULT_ROW_SPAN = 6
private const val APPS_DEFAULT_COL_SPAN = 7
private const val APPS_DEFAULT_ROW_SPAN = 11
private const val SIDE_PANEL_DEFAULT_COL_SPAN = 3
private const val SIDE_PANEL_DEFAULT_ROW_SPAN = 7
private const val WIDGET_DEFAULT_COL_SPAN = 2
private const val WIDGET_DEFAULT_ROW_SPAN = 2
private const val WIDGET_LIST_DEFAULT_COL_SPAN = 3
private const val WIDGET_LIST_DEFAULT_ROW_SPAN = 2
private const val SCROLL_VIEW_DEFAULT_COL_SPAN = 7
private const val SCROLL_VIEW_DEFAULT_ROW_SPAN = 6
private const val DEFAULT_MIN_COL_SPAN = 1
private const val DEFAULT_MIN_ROW_SPAN = 1

@Suppress("TooManyFunctions")
object GridEngineUtils {

    const val PORTRAIT_MAX_COLS = 10
    const val PORTRAIT_MAX_ROWS = 20

    const val LANDSCAPE_MAX_COLS = 20
    const val LANDSCAPE_MAX_ROWS = 10

    // For backwards compatibility or default initialization
    const val DEFAULT_MAX_COLS = PORTRAIT_MAX_COLS
    const val DEFAULT_MAX_ROWS = PORTRAIT_MAX_ROWS

    fun constrainItemsToBounds(items: List<LauncherItemState>, limits: GridLimits): List<LauncherItemState> {
        val constrainedList = mutableListOf<LauncherItemState>()
        for (item in items) {
            var newColSpan = item.colSpan.coerceAtMost(limits.maxCols)
            var newRowSpan = item.rowSpan.coerceAtMost(limits.maxRows)

            var newCol = item.col.coerceIn(0, limits.maxCols - newColSpan)
            var newRow = item.row.coerceIn(0, limits.maxRows - newRowSpan)

            // Note: simple fallback allows overlap if items are forced into the same constrained spot upon rotation
            val constrainedItem = item.copy(
                col = newCol,
                row = newRow,
                colSpan = newColSpan,
                rowSpan = newRowSpan
            )
            constrainedList.add(constrainedItem)
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
        targetColSpan: Int,
        targetRowSpan: Int,
        minColSpan: Int = 1,
        minRowSpan: Int = 1,
        items: List<LauncherItemState>,
        limits: GridLimits = GridLimits()
    ): Triple<Int, Int, Pair<Int, Int>>? {
        val safeMinColSpan = minColSpan.coerceIn(1, limits.maxCols)
        val safeMinRowSpan = minRowSpan.coerceIn(1, limits.maxRows)
        val safeTargetColSpan = targetColSpan.coerceIn(safeMinColSpan, limits.maxCols)
        val safeTargetRowSpan = targetRowSpan.coerceIn(safeMinRowSpan, limits.maxRows)

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

    fun getDefaultSpanForType(type: LauncherItemType): Pair<Pair<Int, Int>, Pair<Int, Int>> {
        return when (type) {
            LauncherItemType.CLOCK -> Pair(
                Pair(CLOCK_DEFAULT_COL_SPAN, CLOCK_DEFAULT_ROW_SPAN),
                Pair(DEFAULT_MIN_COL_SPAN, DEFAULT_MIN_ROW_SPAN)
            )
            LauncherItemType.APPS_LIST -> Pair(
                Pair(APPS_DEFAULT_COL_SPAN, APPS_DEFAULT_ROW_SPAN),
                Pair(DEFAULT_MIN_COL_SPAN, DEFAULT_MIN_ROW_SPAN)
            )
            LauncherItemType.SHORTCUTS_SIDE_PANEL -> Pair(
                Pair(SIDE_PANEL_DEFAULT_COL_SPAN, SIDE_PANEL_DEFAULT_ROW_SPAN),
                Pair(DEFAULT_MIN_COL_SPAN, DEFAULT_MIN_ROW_SPAN)
            )
            LauncherItemType.SINGLE_APP_WIDGET -> Pair(
                Pair(WIDGET_DEFAULT_COL_SPAN, WIDGET_DEFAULT_ROW_SPAN),
                Pair(DEFAULT_MIN_COL_SPAN, DEFAULT_MIN_ROW_SPAN)
            )
            LauncherItemType.WIDGET_LIST -> Pair(
                Pair(WIDGET_LIST_DEFAULT_COL_SPAN, WIDGET_LIST_DEFAULT_ROW_SPAN),
                Pair(DEFAULT_MIN_COL_SPAN, DEFAULT_MIN_ROW_SPAN)
            )
            LauncherItemType.SCROLL_VIEW -> Pair(
                Pair(SCROLL_VIEW_DEFAULT_COL_SPAN, SCROLL_VIEW_DEFAULT_ROW_SPAN),
                Pair(DEFAULT_MIN_COL_SPAN, DEFAULT_MIN_ROW_SPAN)
            )
        }
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
        val clampedTargetCol = targetCol.coerceIn(0, maxCol)
        val clampedTargetRow = targetRow.coerceIn(0, maxRow)

        val testItem = item.copy(col = clampedTargetCol, row = clampedTargetRow)
        if (!checkCollisionWithOthers(testItem, items) &&
            isWithinBounds(clampedTargetCol, clampedTargetRow, item.colSpan, item.rowSpan, limits)
        ) {
            return Pair(clampedTargetCol, clampedTargetRow)
        }

        var bestSlot: Pair<Int, Int>? = null
        var minDistanceSq = Float.MAX_VALUE

        for (r in 0..maxRow) {
            for (c in 0..maxCol) {
                val candidate = item.copy(col = c, row = r)
                if (!checkCollisionWithOthers(candidate, items) &&
                    isWithinBounds(c, r, item.colSpan, item.rowSpan, limits)
                ) {
                    val dc = c - targetCol
                    val dr = r - targetRow
                    val distSq = (dc * dc + dr * dr).toFloat()
                    if (distSq < minDistanceSq) {
                        minDistanceSq = distSq
                        bestSlot = Pair(c, r)
                    }
                }
            }
        }
        return bestSlot
    }

    fun getDefaultGridItems(isLandscape: Boolean = false): List<LauncherItemState> {
        if (isLandscape) {
            return listOf(
                LauncherItemState(
                    id = "clock_item",
                    type = LauncherItemType.CLOCK,
                    col = 0,
                    row = 0,
                    colSpan = 10,
                    rowSpan = 4,
                    minColSpan = 1,
                    minRowSpan = 1
                ),
                LauncherItemState(
                    id = "side_panel_item",
                    type = LauncherItemType.SHORTCUTS_SIDE_PANEL,
                    col = 17,
                    row = 0,
                    colSpan = 3,
                    rowSpan = 10,
                    minColSpan = 1,
                    minRowSpan = 1
                ),
                LauncherItemState(
                    id = "apps_list_item",
                    type = LauncherItemType.APPS_LIST,
                    col = 0,
                    row = 4,
                    colSpan = 16,
                    rowSpan = 6,
                    minColSpan = 1,
                    minRowSpan = 1
                )
            )
        } else {
            return listOf(
                LauncherItemState(
                    id = "clock_item",
                    type = LauncherItemType.CLOCK,
                    col = 0,
                    row = 1,
                    colSpan = DEFAULT_CLOCK_SPAN_X,
                    rowSpan = DEFAULT_CLOCK_SPAN_Y,
                    minColSpan = 1,
                    minRowSpan = 1
                ),
                LauncherItemState(
                    id = "side_panel_item",
                    type = LauncherItemType.SHORTCUTS_SIDE_PANEL,
                    col = DEFAULT_PANEL_COL,
                    row = DEFAULT_PANEL_ROW,
                    colSpan = DEFAULT_PANEL_SPAN_X,
                    rowSpan = DEFAULT_PANEL_SPAN_Y,
                    minColSpan = 1,
                    minRowSpan = 1
                ),
                LauncherItemState(
                    id = "apps_list_item",
                    type = LauncherItemType.APPS_LIST,
                    col = 0,
                    row = DEFAULT_APPS_ROW,
                    colSpan = DEFAULT_APPS_SPAN_X,
                    rowSpan = DEFAULT_APPS_SPAN_Y,
                    minColSpan = 1,
                    minRowSpan = 1
                )
            )
        }
    }
}
