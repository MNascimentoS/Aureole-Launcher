package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintEdge
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintGrid
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintItem
import org.junit.Assert.assertEquals
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

        assertEquals(listOf(EditHintItem(row = 2, rowSpan = 6)), grid.items)
        assertEquals(30, grid.maxRows)
    }
}
