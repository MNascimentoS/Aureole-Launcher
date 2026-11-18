package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintBand
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintEdge
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintGrid
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintItem
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintSide
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingEditHintExtTest {

    private fun grid(vararg items: EditHintItem) = EditHintGrid(items = items.toList(), maxRows = 30)

    @Test
    fun `when nothing covers either strip, should stay at the bottom`() {
        assertEquals(EditHintEdge.BOTTOM, editHintEdge(grid(EditHintItem(row = 10, rowSpan = 5)), bandRows = 3))
        assertEquals(EditHintEdge.BOTTOM, editHintEdge(grid(), bandRows = 3))
    }

    @Test
    fun `when an item covers the bottom strip and the top is free, should move to the top`() {
        assertEquals(EditHintEdge.TOP, editHintEdge(grid(EditHintItem(row = 26, rowSpan = 4)), bandRows = 3))
    }

    @Test
    fun `when the top is covered more than the bottom, should stay at the bottom`() {
        val items = arrayOf(EditHintItem(row = 0, rowSpan = 6), EditHintItem(row = 28, rowSpan = 2))
        assertEquals(EditHintEdge.BOTTOM, editHintEdge(grid(*items), bandRows = 3))
    }

    @Test
    fun `when both strips are covered the same, should keep the bottom`() {
        val items = arrayOf(EditHintItem(row = 0, rowSpan = 6), EditHintItem(row = 24, rowSpan = 6))
        assertEquals(EditHintEdge.BOTTOM, editHintEdge(grid(*items), bandRows = 3))
    }

    @Test
    fun `band rows should round up and stay inside the grid`() {
        assertEquals(3, editHintBandRows(bandDp = 90f, totalDp = 900f, maxRows = 30))
        assertEquals(4, editHintBandRows(bandDp = 91f, totalDp = 900f, maxRows = 30))
        assertEquals(30, editHintBandRows(bandDp = 5000f, totalDp = 900f, maxRows = 30))
        assertEquals(1, editHintBandRows(bandDp = 100f, totalDp = 0f, maxRows = 30))
    }

    @Test
    fun `grid items should map to the rows they cover`() {
        val grid = listOf(LauncherItemState(row = 2, rowSpan = 6)).toEditHintGrid(maxRows = 30)

        assertEquals(listOf(EditHintItem(row = 2, rowSpan = 6, col = 0, colSpan = 1)), grid.items)
        assertEquals(30, grid.maxRows)
    }

    @Test
    fun `band columns should be centered in portrait and on the thumb side in landscape`() {
        assertEquals(5 until 15, editHintBandCols(180f, 360f, 20, EditHintSide.CENTER))
        assertEquals(20 until 30, editHintBandCols(240f, 720f, 30, EditHintSide.END))
        assertEquals(0 until 10, editHintBandCols(240f, 720f, 30, EditHintSide.START))
        assertNull(editHintBandCols(180f, 0f, 20, EditHintSide.CENTER))
    }

    @Test
    fun `when the edited item sits beside the pill, should keep the bottom`() {
        val container = EditHintItem(row = 12, rowSpan = 18, col = 16, colSpan = 4)
        val grid = EditHintGrid(items = listOf(container), maxRows = 30, maxCols = 20)

        assertEquals(EditHintEdge.BOTTOM, editHintEdge(grid, EditHintBand(rows = 3, cols = 5 until 15)))
        assertEquals(EditHintEdge.TOP, editHintEdge(grid, EditHintBand(rows = 3, cols = 10 until 20)))
    }

    @Test
    fun `in the default layout, the hint should look only at the item being edited`() {
        val layout = GridDefaults.getDefaultGridItems()
        val widgets = layout.first { it.safeType == LauncherItemType.WIDGET_LIST }
        val state = MainUiState(
            gridItems = layout,
            isGridEditMode = true,
            onboarding = OnboardingUiState(pendingTask = OnboardingTask.ADD_WIDGET, createdItemId = widgets.id)
        )

        assertEquals(listOf(widgets), state.editHintItems())
        val grid = state.editHintItems().toEditHintGrid(maxRows = 30, maxCols = 20)
        assertEquals(EditHintEdge.BOTTOM, editHintEdge(grid, EditHintBand(rows = 4, cols = 5 until 15)))
    }

    @Test
    fun `the layout step and no pending task should leave nothing to avoid`() {
        val layout = GridDefaults.getDefaultGridItems()

        assertTrue(MainUiState(gridItems = layout).editHintItems().isEmpty())
        val adjust = OnboardingUiState(pendingTask = OnboardingTask.ADJUST_LAYOUT)
        assertTrue(MainUiState(gridItems = layout, onboarding = adjust).editHintItems().isEmpty())
    }
}
