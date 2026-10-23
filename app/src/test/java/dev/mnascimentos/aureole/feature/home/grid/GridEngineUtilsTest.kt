package dev.mnascimentos.aureole.feature.home.grid

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import dev.mnascimentos.aureole.feature.home.grid.model.SlotSearchRequest
import dev.mnascimentos.aureole.feature.home.grid.model.SnapParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private const val TEST_GRID_COLS = 4
private const val TEST_GRID_ROWS = 8

class GridEngineUtilsTest {

    private val testLimits = GridLimits(maxCols = TEST_GRID_COLS, maxRows = TEST_GRID_ROWS)

    @Test
    fun `when items overlap, should return true for AABB collision`() {
        val itemA = LauncherItemState(
            id = "a",
            type = LauncherItemType.CLOCK,
            col = 0,
            row = 0,
            colSpan = 2,
            rowSpan = 2
        )
        val itemB = LauncherItemState(
            id = "b",
            type = LauncherItemType.SINGLE_APP_WIDGET,
            col = 1,
            row = 1,
            colSpan = 2,
            rowSpan = 2
        )

        assertTrue(GridEngineUtils.hasAABBCollision(itemA, itemB))
    }

    @Test
    fun `when items touch edges but do not overlap, should return false for AABB collision`() {
        val itemA = LauncherItemState(
            id = "a",
            type = LauncherItemType.CLOCK,
            col = 0,
            row = 0,
            colSpan = 2,
            rowSpan = 2
        )
        val itemB = LauncherItemState(
            id = "b",
            type = LauncherItemType.SINGLE_APP_WIDGET,
            col = 2,
            row = 0,
            colSpan = 2,
            rowSpan = 2
        )

        assertFalse(GridEngineUtils.hasAABBCollision(itemA, itemB))
    }

    @Test
    fun `when checking collision with existing items, should detect overlap`() {
        val items = listOf(
            LauncherItemState(
                id = "a",
                type = LauncherItemType.CLOCK,
                col = 0,
                row = 0,
                colSpan = 3,
                rowSpan = 2
            ),
            LauncherItemState(
                id = "b",
                type = LauncherItemType.SHORTCUTS_CONTAINER,
                col = 3,
                row = 0,
                colSpan = 1,
                rowSpan = 8
            )
        )
        val newItem = LauncherItemState(
            id = "c",
            type = LauncherItemType.SINGLE_APP_WIDGET,
            col = 2,
            row = 0,
            colSpan = 2,
            rowSpan = 2
        )

        assertTrue(GridEngineUtils.checkCollisionWithOthers(newItem, items))
    }

    @Test
    fun `when item fits within max cols and rows, should return true for isWithinBounds`() {
        assertTrue(
            GridEngineUtils.isWithinBounds(
                col = 0,
                row = 0,
                colSpan = 3,
                rowSpan = 2,
                limits = testLimits
            )
        )
        assertFalse(
            GridEngineUtils.isWithinBounds(
                col = 2,
                row = 0,
                colSpan = 3,
                rowSpan = 2,
                limits = testLimits
            )
        )
    }

    @Test
    fun `when calculating snap cell, should round correctly and stay within bounds`() {
        val cellWidth = 100f
        val cellHeight = 100f

        val snap = GridEngineUtils.calculateSnapCell(
            params = SnapParams(
                xPx = 140f,
                yPx = 260f,
                cellWidthPx = cellWidth,
                cellHeightPx = cellHeight,
                colSpan = 2,
                rowSpan = 2
            ),
            limits = testLimits
        )

        assertEquals(1, snap.first)
        assertEquals(3, snap.second)
    }

    @Test
    fun `when searching for available slot, should return first free slot`() {
        val items = listOf(
            LauncherItemState(
                id = "a",
                type = LauncherItemType.CLOCK,
                col = 0,
                row = 0,
                colSpan = 3,
                rowSpan = 2
            )
        )
        val slot = GridEngineUtils.findFirstAvailableSlot(
            colSpan = 3,
            rowSpan = 2,
            items = items,
            limits = testLimits
        )
        assertEquals(Pair(0, 2), slot)
    }

    @Test
    fun `when grid is full, should return null for available slot`() {
        val items = listOf(
            LauncherItemState(
                id = "a",
                type = LauncherItemType.SHORTCUTS_CONTAINER,
                col = 0,
                row = 0,
                colSpan = 4,
                rowSpan = 8
            )
        )
        val slot = GridEngineUtils.findFirstAvailableSlot(
            colSpan = 2,
            rowSpan = 2,
            items = items,
            limits = testLimits
        )
        assertEquals(null, slot)
    }

    @Test
    fun `when target slot is unoccupied, should return direct target slot`() {
        val movingItem = LauncherItemState(
            id = "a",
            type = LauncherItemType.SINGLE_APP_WIDGET,
            col = 0,
            row = 0,
            colSpan = 2,
            rowSpan = 2
        )
        val otherItem = LauncherItemState(
            id = "b",
            type = LauncherItemType.CLOCK,
            col = 2,
            row = 0,
            colSpan = 2,
            rowSpan = 2
        )
        val items = listOf(movingItem, otherItem)

        val slot = GridEngineUtils.findNearestValidSlot(
            targetCol = 0,
            targetRow = 2,
            item = movingItem,
            items = items,
            limits = testLimits
        )

        assertEquals(Pair(0, 2), slot)
    }

    @Test
    fun `when target slot is occupied, should fall back to nearest valid slot`() {
        val movingItem = LauncherItemState(
            id = "a",
            type = LauncherItemType.SINGLE_APP_WIDGET,
            col = 0,
            row = 0,
            colSpan = 2,
            rowSpan = 2
        )
        val blockingItem = LauncherItemState(
            id = "b",
            type = LauncherItemType.CLOCK,
            col = 0,
            row = 2,
            colSpan = 2,
            rowSpan = 2
        )
        val items = listOf(movingItem, blockingItem)

        val slot = GridEngineUtils.findNearestValidSlot(
            targetCol = 0,
            targetRow = 2,
            item = movingItem,
            items = items,
            limits = testLimits
        )

        // Since (0,2) is occupied by item b, the nearest free 2x2 slot to (0,2) is (2,2) or (0,0)
        assertTrue(slot == Pair(2, 2) || slot == Pair(0, 0))
    }

    @Test
    fun `when target distance is greater than 3 cells, should find nearest valid slot`() {
        val movingItem = LauncherItemState(
            id = "a",
            type = LauncherItemType.SINGLE_APP_WIDGET,
            col = 0,
            row = 0,
            colSpan = 2,
            rowSpan = 2
        )
        // Block rows 0 through 4 completely except for movingItem at (0,0)
        val blocker1 = LauncherItemState(
            id = "b",
            type = LauncherItemType.CLOCK,
            col = 2,
            row = 0,
            colSpan = 2,
            rowSpan = 8
        )
        val blocker2 = LauncherItemState(
            id = "c",
            type = LauncherItemType.CLOCK,
            col = 0,
            row = 2,
            colSpan = 2,
            rowSpan = 3
        )
        val items = listOf(movingItem, blocker1, blocker2)

        val slot = GridEngineUtils.findNearestValidSlot(
            targetCol = 2,
            targetRow = 5,
            item = movingItem,
            items = items,
            limits = testLimits
        )

        assertEquals(Pair(0, 5), slot)
    }

    @Test
    fun `when constrainItemsToBounds is called, should clamp col and row spans to limits`() {
        val items = listOf(
            LauncherItemState(
                id = "1",
                type = LauncherItemType.CLOCK,
                col = -2,
                row = -5,
                colSpan = 10,
                rowSpan = 20
            ),
            LauncherItemState(
                id = "2",
                type = LauncherItemType.CLOCK,
                col = 10,
                row = 20,
                colSpan = 5,
                rowSpan = 5
            )
        )

        val result = GridEngineUtils.constrainItemsToBounds(items, testLimits)

        assertEquals(2, result.size)
        // item 1
        assertEquals(0, result[0].col)
        assertEquals(0, result[0].row)
        assertEquals(4, result[0].colSpan) // capped to maxCols
        assertEquals(8, result[0].rowSpan) // capped to maxRows

        // item 2
        assertEquals(0, result[1].col) // maxCols(4) - colSpan(4) = 0
        assertEquals(3, result[1].row) // maxRows(8) - rowSpan(5) = 3
        assertEquals(4, result[1].colSpan) // maxCols(4)
        assertEquals(5, result[1].rowSpan) // rowSpan(5) < maxRows(8)
    }

    @Test
    fun `when calculateSnapCell receives negative or zero dimensions, should return origin`() {
        val snapZeroWidth = GridEngineUtils.calculateSnapCell(
            params = SnapParams(
                xPx = 140f,
                yPx = 260f,
                cellWidthPx = 0f,
                cellHeightPx = 100f,
                colSpan = 2,
                rowSpan = 2
            ),
            limits = testLimits
        )
        assertEquals(0, snapZeroWidth.first)
        assertEquals(0, snapZeroWidth.second)
    }

    @Test
    fun `when findLargestAvailableSlot receives request, should return largest fitting slot`() {
        val request = SlotSearchRequest(
            targetColSpan = 3,
            targetRowSpan = 3,
            minColSpan = 1,
            minRowSpan = 1,
            limits = testLimits
        )
        val items = listOf(
            LauncherItemState(id = "1", type = LauncherItemType.CLOCK, col = 0, row = 0, colSpan = 4, rowSpan = 6)
        )

        // Only row 6 and 7 are free. (2 rows).
        // Max available slot is 4 cols x 2 rows
        val result = GridEngineUtils.findLargestAvailableSlot(request, items)

        assertTrue(result != null)
        assertEquals(3, result?.first) // targetColSpan was 3, it fits in 4
        assertEquals(2, result?.second) // targetRowSpan was 3, but only 2 rows available
        assertEquals(0, result?.third?.first) // col
        assertEquals(6, result?.third?.second) // row
    }

    @Test
    fun `when findLargestAvailableSlot has no space at all, should return null`() {
        val request = SlotSearchRequest(
            targetColSpan = 2,
            targetRowSpan = 2,
            minColSpan = 1,
            minRowSpan = 1,
            limits = testLimits
        )
        val items = listOf(
            LauncherItemState(id = "1", type = LauncherItemType.CLOCK, col = 0, row = 0, colSpan = 4, rowSpan = 8)
        )

        val result = GridEngineUtils.findLargestAvailableSlot(request, items)
        assertEquals(null, result)
    }
}
