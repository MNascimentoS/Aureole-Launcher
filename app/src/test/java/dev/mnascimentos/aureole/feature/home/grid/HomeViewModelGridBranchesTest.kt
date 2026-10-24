package dev.mnascimentos.aureole.feature.home.grid

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.extensions.cancelGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.extensions.loadGridItems
import dev.mnascimentos.aureole.feature.home.grid.extensions.moveGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.resizeGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.updateGridItemsOrientation
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelGridBranchesTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var gridRepository: GridRepository

    private val portraitLimits = GridLimits(GridEngineUtils.PORTRAIT_MAX_COLS, GridEngineUtils.PORTRAIT_MAX_ROWS)
    private val landscapeLimits = GridLimits(GridEngineUtils.LANDSCAPE_MAX_COLS, GridEngineUtils.LANDSCAPE_MAX_ROWS)
    private val clock = LauncherItemState(
        id = "clock",
        type = LauncherItemType.CLOCK,
        col = 0,
        row = 0,
        colSpan = 4,
        rowSpan = 2
    )
    private val apps = LauncherItemState(
        id = "apps",
        type = LauncherItemType.APPS_LIST,
        col = 0,
        row = 2,
        colSpan = 4,
        rowSpan = 4
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val context: Context = RuntimeEnvironment.getApplication()
        val settingsRepository = SettingsRepository(context)
        gridRepository = GridRepository(context)
        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepository,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository),
                containerRepository = ContainerRepository(context),
                gridRepository = gridRepository
            )
        )
    }

    @After
    fun tearDown() {
        // Let the init's background loads finish before Dispatchers.Main is swapped back.
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        Dispatchers.resetMain()
    }

    private fun givenGrid(isEditMode: Boolean = false, limits: GridLimits = portraitLimits) {
        viewModel.updateUiState {
            MainUiState(
                gridItems = listOf(clock, apps),
                portraitGridItems = listOf(clock, apps),
                gridLimits = limits,
                isGridEditMode = isEditMode
            )
        }
    }

    /** The grid part of the state; the init's settings load may still touch unrelated fields. */
    private fun MainUiState.gridState() = listOf(
        gridItems,
        portraitGridItems,
        landscapeGridItems,
        editingGridItem,
        gridLimits,
        isLandscape,
        isGridEditMode
    )

    @Test
    fun `when loading in edit mode, should keep the items being edited`() {
        givenGrid(isEditMode = true)
        val before = viewModel.uiState.value

        viewModel.loadGridItems(isLandscape = true)

        assertEquals(before.gridState(), viewModel.uiState.value.gridState())
    }

    @Test
    fun `when loading in landscape, should show the landscape items`() {
        gridRepository.saveGridItems(listOf(apps), isLandscape = true)
        gridRepository.saveGridItems(listOf(clock), isLandscape = false)

        viewModel.loadGridItems(isLandscape = true)

        val state = viewModel.uiState.value
        assertTrue(state.isLandscape)
        assertEquals(listOf("apps"), state.gridItems.map { it.id })
        assertEquals(listOf("clock"), state.portraitGridItems.map { it.id })
    }

    @Test
    fun `when cancelling edit mode without a cache, should keep the current items`() {
        givenGrid(isEditMode = true)

        viewModel.cancelGridEditMode()

        val state = viewModel.uiState.value
        assertFalse(state.isGridEditMode)
        assertEquals(listOf(clock, apps), state.gridItems)
    }

    @Test
    fun `when cancelling edit mode in landscape, should restore the cached landscape items`() {
        viewModel.updateUiState {
            MainUiState(
                isGridEditMode = true,
                isLandscape = true,
                gridItems = listOf(clock),
                landscapeGridItems = listOf(clock),
                cachedLandscapeGridItems = listOf(apps),
                cachedPortraitGridItems = listOf(clock, apps)
            )
        }

        viewModel.cancelGridEditMode()

        val state = viewModel.uiState.value
        assertEquals(listOf(apps), state.gridItems)
        assertEquals(listOf(clock, apps), state.portraitGridItems)
        assertNull(state.cachedLandscapeGridItems)
    }

    @Test
    fun `when the orientation did not change, should leave the state untouched`() {
        givenGrid()
        val before = viewModel.uiState.value

        viewModel.updateGridItemsOrientation(portraitLimits.maxCols, portraitLimits.maxRows)

        assertEquals(before.gridState(), viewModel.uiState.value.gridState())
    }

    @Test
    fun `when rotating to landscape with no landscape items loaded, should load and constrain them`() {
        val wide = clock.copy(colSpan = 25)
        gridRepository.saveGridItems(listOf(wide), isLandscape = true)
        viewModel.updateUiState { MainUiState(gridLimits = portraitLimits) }

        viewModel.updateGridItemsOrientation(landscapeLimits.maxCols, landscapeLimits.maxRows)

        val state = viewModel.uiState.value
        assertTrue(state.isLandscape)
        assertEquals(landscapeLimits, state.gridLimits)
        val active = state.gridItems.single()
        assertTrue(active.col + active.colSpan <= landscapeLimits.maxCols)
        assertEquals(state.gridItems, state.landscapeGridItems)
    }

    @Test
    fun `when rotating back to portrait with items out of bounds, should constrain and save them`() {
        val tooWide = clock.copy(colSpan = 28)
        viewModel.updateUiState {
            MainUiState(
                isLandscape = true,
                gridLimits = landscapeLimits,
                portraitGridItems = listOf(tooWide),
                landscapeGridItems = listOf(apps)
            )
        }

        viewModel.updateGridItemsOrientation(portraitLimits.maxCols, portraitLimits.maxRows)

        val saved = gridRepository.getGridItems(isLandscape = false).single()
        assertTrue(saved.col + saved.colSpan <= portraitLimits.maxCols)
        assertEquals(listOf(apps), viewModel.uiState.value.landscapeGridItems)
    }

    @Test
    fun `when moving to a free cell, should move the item and save it`() {
        givenGrid()

        viewModel.moveGridItem("clock", newCol = 5, newRow = 0)

        assertEquals(5, viewModel.uiState.value.gridItems.first { it.id == "clock" }.col)
        assertEquals(5, gridRepository.getGridItems(isLandscape = false).first { it.id == "clock" }.col)
    }

    @Test
    fun `when moving in edit mode, should move without saving`() {
        gridRepository.saveGridItems(listOf(clock, apps), isLandscape = false)
        givenGrid(isEditMode = true)

        viewModel.moveGridItem("clock", newCol = 6, newRow = 0)

        assertEquals(6, viewModel.uiState.value.gridItems.first { it.id == "clock" }.col)
        assertEquals(0, gridRepository.getGridItems(isLandscape = false).first { it.id == "clock" }.col)
    }

    @Test
    fun `when a move collides, leaves the grid or targets a missing item, should do nothing`() {
        givenGrid()
        val before = viewModel.uiState.value

        viewModel.moveGridItem("clock", newCol = 0, newRow = 3)
        viewModel.moveGridItem("clock", newCol = portraitLimits.maxCols, newRow = 0)
        viewModel.moveGridItem("missing", newCol = 1, newRow = 1)

        assertEquals(before.gridState(), viewModel.uiState.value.gridState())
    }

    @Test
    fun `when resizing into free space, should resize and save`() {
        givenGrid()

        viewModel.resizeGridItem("clock", newColSpan = 6, newRowSpan = 2)

        assertEquals(6, viewModel.uiState.value.gridItems.first { it.id == "clock" }.colSpan)
        assertEquals(6, gridRepository.getGridItems(isLandscape = false).first { it.id == "clock" }.colSpan)
    }

    @Test
    fun `when resizing in edit mode, should resize without saving`() {
        gridRepository.saveGridItems(listOf(clock, apps), isLandscape = false)
        givenGrid(isEditMode = true)

        viewModel.resizeGridItem("apps", newColSpan = 5, newRowSpan = 4)

        assertEquals(5, viewModel.uiState.value.gridItems.first { it.id == "apps" }.colSpan)
        assertEquals(4, gridRepository.getGridItems(isLandscape = false).first { it.id == "apps" }.colSpan)
    }

    @Test
    fun `when a resize collides, leaves the grid or targets a missing item, should do nothing`() {
        givenGrid()
        val before = viewModel.uiState.value

        viewModel.resizeGridItem("clock", newColSpan = 4, newRowSpan = 3)
        viewModel.resizeGridItem("clock", newColSpan = portraitLimits.maxCols + 1, newRowSpan = 2)
        viewModel.resizeGridItem("missing", newColSpan = 1, newRowSpan = 1)

        assertEquals(before.gridState(), viewModel.uiState.value.gridState())
    }
}
