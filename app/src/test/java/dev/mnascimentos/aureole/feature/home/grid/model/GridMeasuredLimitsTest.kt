package dev.mnascimentos.aureole.feature.home.grid.model

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/** RF-12: the measure of the grid only fits the items to the limits, it never decides the orientation. */
class GridMeasuredLimitsTest {

    private val portrait = listOf(LauncherItemState(id = "p", type = LauncherItemType.CLOCK, colSpan = 4, rowSpan = 2))
    private val landscape = listOf(LauncherItemState(id = "l", type = LauncherItemType.CLOCK, colSpan = 4, rowSpan = 2))
    private val none = LoadedGrid(emptyList(), emptyList())

    @Test
    fun `a transient measure with landscape columns should not turn a portrait home landscape`() {
        val state = MainUiState(
            portraitGridItems = portrait,
            landscapeGridItems = landscape,
            gridItems = portrait,
            gridLimits = gridLimitsFor(false)
        )

        val result = state.withMeasuredLimits(none, gridLimitsFor(true), measuredLandscape = true)

        assertSame(state, result.state)
        assertNull(result.itemsToSave)
        assertFalse(state.isMeasureChange(gridLimitsFor(true), measuredLandscape = true))
    }

    @Test
    fun `a measure for the orientation the home is in should set its limits`() {
        val state = MainUiState(portraitGridItems = portrait, gridItems = portrait)

        val result = state.withMeasuredLimits(none, gridLimitsFor(false), measuredLandscape = false)

        assertEquals(gridLimitsFor(false), result.state.gridLimits)
        assertFalse(result.state.isLandscape)
    }

    @Test
    fun `during a grid edit the measure still moves the edit to the new orientation`() {
        val state = MainUiState(
            portraitGridItems = portrait,
            landscapeGridItems = landscape,
            gridItems = portrait,
            isGridEditMode = true
        )

        assertTrue(state.isMeasureChange(gridLimitsFor(true), measuredLandscape = true))
        val result = state.withMeasuredLimits(none, gridLimitsFor(true), measuredLandscape = true)

        assertTrue(result.state.isLandscape)
        assertEquals(landscape, result.state.gridItems)
    }

    @Test
    fun `a finished load should set the limits of its orientation`() {
        val loaded = MainUiState().withLoadedGrid(LoadedGrid(portrait, landscape), isLandscape = true)

        assertEquals(gridLimitsFor(true), loaded.gridLimits)
        assertEquals(30, gridLimitsFor(true).maxCols)
        assertEquals(30, gridLimitsFor(false).maxRows)
    }
}
