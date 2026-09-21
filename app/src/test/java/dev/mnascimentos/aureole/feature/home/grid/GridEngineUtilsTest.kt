package dev.mnascimentos.aureole.feature.home.grid

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private const val TEST_GRID_COLS = 4
private const val TEST_GRID_ROWS = 8

class GridEngineUtilsTest {

    private val testLimits = GridLimits(maxCols = TEST_GRID_COLS, maxRows = TEST_GRID_ROWS)

    @Test
    fun `hasAABBCollision returns true when items overlap`() {
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
    fun `hasAABBCollision returns false when items touch edges but do not overlap`() {
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
    fun `checkCollisionWithOthers detects overlap with existing item`() {
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
                type = LauncherItemType.SHORTCUTS_SIDE_PANEL,
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
    fun `isWithinBounds returns true when item fits within max cols and rows`() {
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
    fun `calculateSnapCell rounds correctly and stays within bounds`() {
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
    fun `findFirstAvailableSlot finds first free slot`() {
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
    fun `findFirstAvailableSlot returns null when grid is full`() {
        val items = listOf(
            LauncherItemState(
                id = "a",
                type = LauncherItemType.SHORTCUTS_SIDE_PANEL,
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
}
