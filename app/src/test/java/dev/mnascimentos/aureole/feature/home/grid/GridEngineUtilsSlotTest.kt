package dev.mnascimentos.aureole.feature.home.grid

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import dev.mnascimentos.aureole.feature.home.grid.model.SlotSearchRequest
import dev.mnascimentos.aureole.feature.home.grid.model.SnapParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private const val TEST_GRID_COLS = 4
private const val TEST_GRID_ROWS = 8

class GridEngineUtilsSlotTest {

    private val testLimits = GridLimits(maxCols = TEST_GRID_COLS, maxRows = TEST_GRID_ROWS)

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

        assertTrue(slot == Pair(2, 2) || slot == Pair(0, 0))
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

        val result = GridEngineUtils.findLargestAvailableSlot(request, items)

        assertTrue(result != null)
        assertEquals(3, result?.first)
        assertEquals(2, result?.second)
        assertEquals(0, result?.third?.first)
        assertEquals(6, result?.third?.second)
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
