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

private const val SEARCH_MAX_RADIUS = 3
private const val DEFAULT_CLOCK_SPAN_X = 9
private const val DEFAULT_CLOCK_SPAN_Y = 2
private const val DEFAULT_PANEL_COL = 9
private const val DEFAULT_PANEL_SPAN_Y = 20
private const val DEFAULT_APPS_ROW = 2
private const val DEFAULT_APPS_SPAN_Y = 18

private const val CLOCK_DEFAULT_COL_SPAN = 3
private const val CLOCK_DEFAULT_ROW_SPAN = 2
private const val APPS_DEFAULT_COL_SPAN = 3
private const val APPS_DEFAULT_ROW_SPAN = 4
private const val SIDE_PANEL_DEFAULT_COL_SPAN = 1
private const val SIDE_PANEL_DEFAULT_ROW_SPAN = 4
private const val WIDGET_DEFAULT_COL_SPAN = 2
private const val WIDGET_DEFAULT_ROW_SPAN = 2
private const val WIDGET_LIST_DEFAULT_COL_SPAN = 3
private const val WIDGET_LIST_DEFAULT_ROW_SPAN = 2
private const val DEFAULT_MIN_COL_SPAN = 1
private const val DEFAULT_MIN_ROW_SPAN = 1

private data class RadiusSearchParams(
    val radius: Int,
    val targetCol: Int,
    val targetRow: Int,
    val limits: GridLimits
)

object GridEngineUtils {

    const val DEFAULT_MAX_COLS = 10
    const val DEFAULT_MAX_ROWS = 20

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
        }
    }

    fun findNearestValidSlot(
        targetCol: Int,
        targetRow: Int,
        item: LauncherItemState,
        items: List<LauncherItemState>,
        limits: GridLimits = GridLimits()
    ): Pair<Int, Int>? {
        val testItem = item.copy(col = targetCol, row = targetRow)
        if (!checkCollisionWithOthers(testItem, items) &&
            isWithinBounds(targetCol, targetRow, item.colSpan, item.rowSpan, limits)
        ) {
            return Pair(targetCol, targetRow)
        }
        return searchRadiusSlots(targetCol, targetRow, item, items, limits)
    }

    private fun searchRadiusSlots(
        targetCol: Int,
        targetRow: Int,
        item: LauncherItemState,
        items: List<LauncherItemState>,
        limits: GridLimits
    ): Pair<Int, Int>? {
        for (radius in 1..SEARCH_MAX_RADIUS) {
            val params = RadiusSearchParams(radius, targetCol, targetRow, limits)
            val found = findSlotAtRadius(params, item, items)
            if (found != null) return found
        }
        return null
    }

    private fun findSlotAtRadius(
        params: RadiusSearchParams,
        item: LauncherItemState,
        items: List<LauncherItemState>
    ): Pair<Int, Int>? {
        for (dc in -params.radius..params.radius) {
            for (dr in -params.radius..params.radius) {
                val c = (params.targetCol + dc).coerceIn(0, params.limits.maxCols - item.colSpan)
                val r = (params.targetRow + dr).coerceIn(0, params.limits.maxRows - item.rowSpan)
                val candidate = item.copy(col = c, row = r)
                if (!checkCollisionWithOthers(candidate, items) &&
                    isWithinBounds(c, r, item.colSpan, item.rowSpan, params.limits)
                ) {
                    return Pair(c, r)
                }
            }
        }
        return null
    }

    fun getDefaultGridItems(): List<LauncherItemState> {
        return listOf(
            LauncherItemState(
                id = "clock_item",
                type = LauncherItemType.CLOCK,
                col = 0,
                row = 0,
                colSpan = DEFAULT_CLOCK_SPAN_X,
                rowSpan = DEFAULT_CLOCK_SPAN_Y,
                minColSpan = 1,
                minRowSpan = 1
            ),
            LauncherItemState(
                id = "side_panel_item",
                type = LauncherItemType.SHORTCUTS_SIDE_PANEL,
                col = DEFAULT_PANEL_COL,
                row = 0,
                colSpan = 1,
                rowSpan = DEFAULT_PANEL_SPAN_Y,
                minColSpan = 1,
                minRowSpan = 1
            ),
            LauncherItemState(
                id = "apps_list_item",
                type = LauncherItemType.APPS_LIST,
                col = 0,
                row = DEFAULT_APPS_ROW,
                colSpan = DEFAULT_CLOCK_SPAN_X,
                rowSpan = DEFAULT_APPS_SPAN_Y,
                minColSpan = 1,
                minRowSpan = 1
            )
        )
    }
}
