package dev.mnascimentos.aureole.feature.home.grid.model

import dev.mnascimentos.aureole.core.coroutines.LatestJobRunner
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GridLoadModelsTest {

    private val portrait = listOf(LauncherItemState(id = "p", type = LauncherItemType.CLOCK, colSpan = 4, rowSpan = 2))
    private val landscape = listOf(LauncherItemState(id = "l", type = LauncherItemType.CLOCK, colSpan = 4, rowSpan = 2))
    private val loaded = LoadedGrid(portrait, landscape)

    @Test
    fun `when a load finishes in landscape, should show the landscape grid`() {
        val state = MainUiState().withLoadedGrid(loaded, isLandscape = true)

        assertEquals(landscape, state.gridItems)
        assertEquals(portrait, state.portraitGridItems)
        assertTrue(state.isLandscape)
    }

    @Test
    fun `when a load finishes in portrait, should show the portrait grid`() {
        val state = MainUiState(isLandscape = true).withLoadedGrid(loaded, isLandscape = false)

        assertEquals(portrait, state.gridItems)
        assertFalse(state.isLandscape)
    }

    @Test
    fun `when a grid edit is in progress, should not apply the load`() {
        val editing = MainUiState(isGridEditMode = true)

        assertEquals(editing, editing.withLoadedGrid(loaded, isLandscape = true))
    }

    @Test
    fun `when portrait loads and landscape follows, should end with the landscape grid and no exception`() = runTest {
        val runner = LatestJobRunner(this)
        var state = MainUiState()
        runner.launch {
            delay(SLOW_MS)
            state = state.withLoadedGrid(loaded, isLandscape = false)
        }
        runner.launch {
            delay(FAST_MS)
            state = state.withLoadedGrid(loaded, isLandscape = true)
        }
        advanceUntilIdle()

        assertEquals(landscape, state.gridItems)
        assertTrue(state.isLandscape)
    }

    @Test
    fun `when the orientation and limits are unchanged, should keep the state and save nothing`() {
        val limits = GridLimits(30, 20)
        val state = MainUiState(isLandscape = true, gridLimits = limits)

        val result = state.withGridOrientation(LoadedGrid(emptyList(), emptyList()), limits, isLandscape = true)

        assertEquals(state, result.state)
        assertNull(result.itemsToSave)
    }

    @Test
    fun `when the layout is in memory, should switch to landscape and use no fallback`() {
        val state = MainUiState(portraitGridItems = portrait, landscapeGridItems = landscape)
        val limits = GridLimits(30, 20)

        val result = state.withGridOrientation(LoadedGrid(emptyList(), emptyList()), limits, isLandscape = true)

        assertEquals(landscape, result.state.gridItems)
        assertTrue(result.state.isLandscape)
        assertEquals(limits, result.state.gridLimits)
        assertNull(result.itemsToSave)
    }

    @Test
    fun `when the layout is not in memory, should use the fallback`() {
        val result = MainUiState().withGridOrientation(loaded, GridLimits(30, 20), isLandscape = true)

        assertEquals(landscape, result.state.gridItems)
        assertEquals(portrait, result.state.portraitGridItems)
    }

    @Test
    fun `when items exceed the limits, should fit them and ask to save`() {
        val wide = listOf(LauncherItemState(id = "w", type = LauncherItemType.CLOCK, colSpan = 40, rowSpan = 2))
        val state = MainUiState(landscapeGridItems = wide)

        val result = state.withGridOrientation(loaded, GridLimits(30, 20), isLandscape = true)

        assertEquals(30, result.state.gridItems.single().colSpan)
        assertEquals(result.state.gridItems, result.itemsToSave)
    }

    @Test
    fun `when items exceed the limits during a grid edit, should fit them but not save`() {
        val wide = listOf(LauncherItemState(id = "w", type = LauncherItemType.CLOCK, colSpan = 40, rowSpan = 2))
        val state = MainUiState(landscapeGridItems = wide, isGridEditMode = true)

        val result = state.withGridOrientation(loaded, GridLimits(30, 20), isLandscape = true)

        assertEquals(30, result.state.gridItems.single().colSpan)
        assertNull(result.itemsToSave)
    }

    private companion object {
        const val SLOW_MS = 100L
        const val FAST_MS = 10L
    }
}
