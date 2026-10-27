package dev.mnascimentos.aureole.feature.home.grid

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
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
        assertEquals(0, result[0].col)
        assertEquals(0, result[0].row)
        assertEquals(4, result[0].colSpan)
        assertEquals(8, result[0].rowSpan)
        assertEquals(0, result[1].col)
        assertEquals(3, result[1].row)
        assertEquals(4, result[1].colSpan)
        assertEquals(5, result[1].rowSpan)
    }
}
