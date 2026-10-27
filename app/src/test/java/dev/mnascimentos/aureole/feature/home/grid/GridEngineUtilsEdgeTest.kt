package dev.mnascimentos.aureole.feature.home.grid

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import dev.mnascimentos.aureole.feature.home.grid.model.SlotSearchRequest
import dev.mnascimentos.aureole.feature.home.grid.model.SnapParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GridEngineUtilsEdgeTest {

    private val limits = GridLimits(maxCols = 4, maxRows = 4)
    private val fullGrid = listOf(
        LauncherItemState(id = "full", type = LauncherItemType.APPS_LIST, col = 0, row = 0, colSpan = 4, rowSpan = 4)
    )

    @Test
    fun `when an item leaves any side of the grid, should be out of bounds`() {
        assertTrue(GridEngineUtils.isWithinBounds(0, 0, 4, 4, limits))
        assertFalse(GridEngineUtils.isWithinBounds(-1, 0, 1, 1, limits))
        assertFalse(GridEngineUtils.isWithinBounds(0, -1, 1, 1, limits))
        assertFalse(GridEngineUtils.isWithinBounds(1, 0, 4, 1, limits))
        assertFalse(GridEngineUtils.isWithinBounds(0, 1, 1, 4, limits))
    }

    @Test
    fun `when cell size is unknown, should snap to the origin`() {
        assertEquals(Pair(0, 0), GridEngineUtils.calculateSnapCell(SnapParams(50f, 50f, 0f, 10f, 1, 1), limits))
        assertEquals(Pair(0, 0), GridEngineUtils.calculateSnapCell(SnapParams(50f, 50f, 10f, 0f, 1, 1), limits))
    }

    @Test
    fun `when snapping beyond the grid, should clamp to the last cell that fits`() {
        assertEquals(Pair(2, 3), GridEngineUtils.calculateSnapCell(SnapParams(999f, 999f, 10f, 10f, 2, 1), limits))
    }

    @Test
    fun `when the grid is full, should find no slot of any size`() {
        assertNull(GridEngineUtils.findFirstAvailableSlot(1, 1, fullGrid, limits))
        assertNull(GridEngineUtils.findLargestAvailableSlot(SlotSearchRequest(2, 2, limits = limits), fullGrid))
    }

    @Test
    fun `when the wanted size does not fit, should settle for the largest smaller one`() {
        val leftHalf = listOf(
            LauncherItemState(id = "left", type = LauncherItemType.CLOCK, col = 0, row = 0, colSpan = 2, rowSpan = 4)
        )

        val slot = GridEngineUtils.findLargestAvailableSlot(SlotSearchRequest(4, 4, limits = limits), leftHalf)

        assertEquals(Triple(2, 4, Pair(2, 0)), slot)
    }

    @Test
    fun `when the target cell is free, should keep it after clamping`() {
        val item = LauncherItemState(id = "item", type = LauncherItemType.CLOCK, colSpan = 2, rowSpan = 2)

        assertEquals(Pair(2, 2), GridEngineUtils.findNearestValidSlot(9, 9, item, emptyList(), limits))
    }

    @Test
    fun `when the target cell is taken, should pick the nearest free one or none`() {
        val item = LauncherItemState(id = "item", type = LauncherItemType.CLOCK, colSpan = 1, rowSpan = 1)
        val blocker =
            LauncherItemState(id = "blocker", type = LauncherItemType.CLOCK, col = 0, row = 0, colSpan = 3, rowSpan = 4)

        assertEquals(Pair(3, 0), GridEngineUtils.findNearestValidSlot(0, 0, item, listOf(blocker), limits))
        assertNull(GridEngineUtils.findNearestValidSlot(0, 0, item, fullGrid, limits))
    }
}
