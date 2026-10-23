package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.model.DragTargetSlot
import dev.mnascimentos.aureole.feature.home.grid.model.GridCellParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DynamicGridDragSourceTest {

    private val testItem = LauncherItemState(
        id = "test_item",
        type = LauncherItemType.CLOCK,
        col = 0,
        row = 0,
        colSpan = 2,
        rowSpan = 2
    )

    private val testParams = GridCellParams(
        item = testItem,
        items = listOf(testItem),
        isEditMode = true,
        cellWidthPx = 100f,
        cellHeightPx = 100f,
        cellWidthDp = 100.dp,
        cellHeightDp = 100.dp
    )

    @Test
    fun `when updating drag target slot, should callback with calculated slot`() {
        var resultSlot: DragTargetSlot? = null

        updateDragTargetSlot(
            params = testParams,
            dragOffsetX = 150f, // roughly 1.5 columns -> should round to 2 because we started at 0
            dragOffsetY = 150f, // roughly 1.5 rows -> should round to 2
            onDragTargetChange = { resultSlot = it }
        )

        assertEquals(2, resultSlot?.col)
        assertEquals(2, resultSlot?.row)
        assertTrue(resultSlot?.isValid == true)
    }

    @Test
    fun `when updating drag target slot with no movement, should return null`() {
        var resultSlot: DragTargetSlot? = null

        updateDragTargetSlot(
            params = testParams,
            dragOffsetX = 0f,
            dragOffsetY = 0f,
            onDragTargetChange = { resultSlot = it }
        )

        assertEquals(null, resultSlot)
    }

    @Test
    fun `when drag target collides, should return invalid slot`() {
        var resultSlot: DragTargetSlot? = null

        val blockingItem = LauncherItemState(
            id = "block",
            type = LauncherItemType.CLOCK,
            col = 2,
            row = 2,
            colSpan = 2,
            rowSpan = 2
        )

        val collisionParams = testParams.copy(items = listOf(testItem, blockingItem))

        updateDragTargetSlot(
            params = collisionParams,
            dragOffsetX = 200f, // -> col 2
            dragOffsetY = 200f, // -> row 2
            onDragTargetChange = { resultSlot = it }
        )

        assertEquals(2, resultSlot?.col)
        assertEquals(2, resultSlot?.row)
        assertFalse(resultSlot?.isValid == true)
    }
}
