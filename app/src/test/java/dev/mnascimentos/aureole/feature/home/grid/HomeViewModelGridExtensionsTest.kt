package dev.mnascimentos.aureole.feature.home.grid

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.extensions.addChildToScrollView
import dev.mnascimentos.aureole.feature.home.grid.extensions.addGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.cancelGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.extensions.deleteGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.dismissGridError
import dev.mnascimentos.aureole.feature.home.grid.extensions.enterGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.extensions.loadGridItems
import dev.mnascimentos.aureole.feature.home.grid.extensions.moveChildInScrollView
import dev.mnascimentos.aureole.feature.home.grid.extensions.moveGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.openAddContainerForParent
import dev.mnascimentos.aureole.feature.home.grid.extensions.removeChildFromScrollView
import dev.mnascimentos.aureole.feature.home.grid.extensions.resetGridItems
import dev.mnascimentos.aureole.feature.home.grid.extensions.resizeChildInScrollView
import dev.mnascimentos.aureole.feature.home.grid.extensions.resizeGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.saveGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.extensions.setEditingGridItem
import dev.mnascimentos.aureole.feature.home.grid.extensions.setShowAddContainerDialog
import dev.mnascimentos.aureole.feature.home.grid.extensions.updateGridItemsOrientation
import dev.mnascimentos.aureole.feature.home.grid.extensions.updateScrollViewOrientation
import dev.mnascimentos.aureole.feature.home.grid.model.GridItemSpec
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelGridExtensionsTest {

    private lateinit var context: Context
    private lateinit var gridRepository: GridRepository
    private lateinit var appRepository: AppRepository
    private lateinit var widgetRepository: WidgetRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var folderRepository: FolderRepository
    private lateinit var favoriteContainerRepository: FavoriteContainerRepository
    private lateinit var containerRepository: ContainerRepository
    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = RuntimeEnvironment.getApplication()
        gridRepository = GridRepository(context)
        gridRepository.resetToDefault() // start fresh
        appRepository = AppRepository(context)
        widgetRepository = WidgetRepository(context)
        settingsRepository = SettingsRepository(context)
        folderRepository = FolderRepository(context)
        favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository)
        containerRepository = ContainerRepository(context)

        viewModel = HomeViewModel(
            repositories = HomeRepositories(
                appRepository = appRepository,
                widgetRepository = widgetRepository,
                settingsRepository = settingsRepository,
                folderRepository = folderRepository,
                favoriteContainerRepository = favoriteContainerRepository,
                containerRepository = containerRepository,
                gridRepository = gridRepository
            )
        )
    }

    @After
    fun tearDown() {
        // Let the init's background loads finish before Dispatchers.Main is swapped back: they resume
        // on Main from IO threads, so drive the test scheduler until the cancelled scope completes.
        val scope = viewModel.viewModelScope.coroutineContext.job
        scope.cancel()
        val deadline = System.currentTimeMillis() + VIEW_MODEL_SHUTDOWN_MS
        while (!scope.isCompleted && System.currentTimeMillis() < deadline) {
            testDispatcher.scheduler.advanceUntilIdle()
            Thread.sleep(SHUTDOWN_POLL_MS)
        }
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        Dispatchers.resetMain()
    }

    @Test
    fun `when loading grid items, should populate portrait and landscape items and set current grid`() = runTest {
        viewModel.loadGridItems(isLandscape = false)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLandscape)
        assertEquals(state.portraitGridItems, state.gridItems)
    }

    @Test
    fun `when entering and canceling edit mode, should restore original items`() = runTest {
        val testItem = LauncherItemState(
            id = "test_item",
            type = LauncherItemType.CLOCK,
            col = 0,
            row = 0,
            colSpan = 1,
            rowSpan = 1
        )
        gridRepository.saveGridItems(listOf(testItem), isLandscape = false)
        viewModel.loadGridItems(isLandscape = false)
        testDispatcher.scheduler.advanceUntilIdle()

        val originalItems = viewModel.uiState.value.gridItems

        viewModel.enterGridEditMode()
        assertTrue(viewModel.uiState.value.isGridEditMode)

        viewModel.deleteGridItem(originalItems.first().id)

        viewModel.cancelGridEditMode()
        assertFalse(viewModel.uiState.value.isGridEditMode)
        assertEquals(originalItems.size, viewModel.uiState.value.gridItems.size)
    }

    @Test
    fun `when entering and saving edit mode, should persist changes`() = runTest {
        viewModel.loadGridItems(isLandscape = false)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.enterGridEditMode()

        // Remove existing clock to make space if needed
        viewModel.uiState.value.gridItems.filter { it.type == LauncherItemType.CLOCK }.forEach {
            viewModel.deleteGridItem(it.id)
        }

        val itemToAdd = GridItemSpec(type = LauncherItemType.CLOCK, targetColSpan = 2, targetRowSpan = 2)
        viewModel.addGridItem(itemToAdd)

        viewModel.saveGridEditMode()
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isGridEditMode)

        // Reload from repo to verify
        val freshGrid = gridRepository.getGridItems(isLandscape = false)
        assertTrue(freshGrid.any { it.colSpan == 2 && it.rowSpan == 2 && it.type == LauncherItemType.CLOCK })
    }

    @Test
    fun `when adding grid item to full grid, should set error message`() = runTest {
        viewModel.loadGridItems(isLandscape = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Add a giant item that takes up the whole grid
        val giantItem = GridItemSpec(
            type = LauncherItemType.CLOCK,
            targetColSpan = GridEngineUtils.PORTRAIT_MAX_COLS,
            targetRowSpan = GridEngineUtils.PORTRAIT_MAX_ROWS
        )
        viewModel.addGridItem(giantItem)

        // Try adding another item
        val extraItem = GridItemSpec(type = LauncherItemType.CLOCK)
        viewModel.addGridItem(extraItem)

        val state = viewModel.uiState.value
        assertNotNull(state.gridErrorMessage)
        assertEquals("Não há espaço livre suficiente na grade para este container.", state.gridErrorMessage)

        viewModel.dismissGridError()
        assertNull(viewModel.uiState.value.gridErrorMessage)
    }

    @Test
    fun `when moving grid item to valid slot, should update item position`() = runTest {
        viewModel.loadGridItems(isLandscape = false)
        testDispatcher.scheduler.advanceUntilIdle()

        // Clear grid and add one item at 0,0
        viewModel.enterGridEditMode()
        viewModel.uiState.value.gridItems.forEach { viewModel.deleteGridItem(it.id) }
        viewModel.addGridItem(GridItemSpec(type = LauncherItemType.CLOCK, targetColSpan = 2, targetRowSpan = 2))
        testDispatcher.scheduler.advanceUntilIdle()

        val item = viewModel.uiState.value.gridItems.first()

        viewModel.moveGridItem(item.id, newCol = 2, newRow = 2)

        val movedItem = viewModel.uiState.value.gridItems.find { it.id == item.id }
        assertEquals(2, movedItem?.col)
        assertEquals(2, movedItem?.row)
    }

    @Test
    fun `when resizing grid item to valid slot, should update item span`() = runTest {
        viewModel.loadGridItems(isLandscape = false)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.enterGridEditMode()
        viewModel.uiState.value.gridItems.forEach { viewModel.deleteGridItem(it.id) }
        viewModel.addGridItem(GridItemSpec(type = LauncherItemType.CLOCK, targetColSpan = 2, targetRowSpan = 2))
        testDispatcher.scheduler.advanceUntilIdle()

        val item = viewModel.uiState.value.gridItems.first()

        viewModel.resizeGridItem(item.id, newColSpan = 4, newRowSpan = 4)

        val resizedItem = viewModel.uiState.value.gridItems.find { it.id == item.id }
        assertEquals(4, resizedItem?.colSpan)
        assertEquals(4, resizedItem?.rowSpan)
    }

    @Test
    fun `when updating orientation to landscape, should constrain items to landscape limits`() = runTest {
        viewModel.loadGridItems(isLandscape = false)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.updateGridItemsOrientation(
            maxCols = GridEngineUtils.LANDSCAPE_MAX_COLS,
            maxRows = GridEngineUtils.LANDSCAPE_MAX_ROWS
        )

        assertTrue(viewModel.uiState.value.isLandscape)
        val landscapeLimits = viewModel.uiState.value.gridLimits
        assertEquals(GridEngineUtils.LANDSCAPE_MAX_COLS, landscapeLimits.maxCols)
        assertEquals(GridEngineUtils.LANDSCAPE_MAX_ROWS, landscapeLimits.maxRows)
    }

    @Test
    fun `when adding child to scroll view, should update scroll view children list`() = runTest {
        viewModel.loadGridItems(isLandscape = false)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.enterGridEditMode()
        viewModel.uiState.value.gridItems.forEach { viewModel.deleteGridItem(it.id) }

        // Add ScrollView
        viewModel.addGridItem(GridItemSpec(type = LauncherItemType.SCROLL_VIEW, targetColSpan = 4, targetRowSpan = 4))
        val scrollView = viewModel.uiState.value.gridItems.first { it.type == LauncherItemType.SCROLL_VIEW }

        viewModel.openAddContainerForParent(scrollView.id)
        assertTrue(viewModel.uiState.value.showAddContainerDialog)
        assertEquals(scrollView.id, viewModel.uiState.value.targetParentContainerId)

        viewModel.addGridItem(GridItemSpec(type = LauncherItemType.CLOCK))

        val updatedScrollView = viewModel.uiState.value.gridItems.first { it.id == scrollView.id }
        assertEquals(1, updatedScrollView.safeChildren.size)
        assertEquals(LauncherItemType.CLOCK, updatedScrollView.safeChildren.first().type)
        assertFalse(viewModel.uiState.value.showAddContainerDialog)
    }

    @Test
    fun `when manipulating scroll view children, should resize remove and move them`() = runTest {
        viewModel.loadGridItems(isLandscape = false)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.enterGridEditMode()
        viewModel.uiState.value.gridItems.forEach { viewModel.deleteGridItem(it.id) }

        viewModel.addGridItem(GridItemSpec(type = LauncherItemType.SCROLL_VIEW, targetColSpan = 4, targetRowSpan = 4))
        val scrollViewId = viewModel.uiState.value.gridItems.first { it.type == LauncherItemType.SCROLL_VIEW }.id

        viewModel.addChildToScrollView(scrollViewId, LauncherItemType.CLOCK)
        viewModel.addChildToScrollView(scrollViewId, LauncherItemType.APPS_LIST)

        var sv = viewModel.uiState.value.gridItems.first { it.id == scrollViewId }
        val child1Id = sv.safeChildren[0].id
        val child2Id = sv.safeChildren[1].id

        // Resize
        viewModel.resizeChildInScrollView(scrollViewId, child1Id, newColSpan = 2, newRowSpan = 2)
        sv = viewModel.uiState.value.gridItems.first { it.id == scrollViewId }
        assertEquals(2, sv.safeChildren[0].colSpan)
        assertEquals(2, sv.safeChildren[0].rowSpan)

        // Move Down (child1 goes to index 1)
        viewModel.moveChildInScrollView(scrollViewId, child1Id, moveUp = false)
        sv = viewModel.uiState.value.gridItems.first { it.id == scrollViewId }
        assertEquals(child1Id, sv.safeChildren[1].id)
        assertEquals(child2Id, sv.safeChildren[0].id)

        // Remove
        viewModel.removeChildFromScrollView(scrollViewId, child1Id)
        sv = viewModel.uiState.value.gridItems.first { it.id == scrollViewId }
        assertEquals(1, sv.safeChildren.size)
        assertEquals(child2Id, sv.safeChildren[0].id)
    }

    @Test
    fun `when updating scroll view orientation, should reflect in parent state`() = runTest {
        viewModel.loadGridItems(isLandscape = false)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.enterGridEditMode()
        viewModel.uiState.value.gridItems.forEach { viewModel.deleteGridItem(it.id) }

        viewModel.addGridItem(GridItemSpec(type = LauncherItemType.SCROLL_VIEW, targetColSpan = 4, targetRowSpan = 4))
        val scrollViewId = viewModel.uiState.value.gridItems.first { it.type == LauncherItemType.SCROLL_VIEW }.id

        viewModel.updateScrollViewOrientation(scrollViewId, ScrollOrientation.HORIZONTAL)
        val sv = viewModel.uiState.value.gridItems.first { it.id == scrollViewId }
        assertEquals(ScrollOrientation.HORIZONTAL, sv.safeScrollOrientation)
    }

    @Test
    fun `when handling dialog state, should show hide and set editing item`() = runTest {
        viewModel.setShowAddContainerDialog(true)
        assertTrue(viewModel.uiState.value.showAddContainerDialog)

        viewModel.setShowAddContainerDialog(false)
        assertFalse(viewModel.uiState.value.showAddContainerDialog)

        val item =
            LauncherItemState(id = "1", type = LauncherItemType.CLOCK, col = 0, row = 0, colSpan = 1, rowSpan = 1)
        viewModel.setEditingGridItem(item)
        assertEquals(item, viewModel.uiState.value.editingGridItem)
    }

    @Test
    fun `when resetting grid, should clear items and reload defaults`() = runTest {
        val testItem = LauncherItemState(
            id = "test_item",
            type = LauncherItemType.CLOCK,
            col = 0,
            row = 0,
            colSpan = 1,
            rowSpan = 1
        )
        gridRepository.saveGridItems(listOf(testItem), isLandscape = false)
        viewModel.loadGridItems(isLandscape = false)
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(1, viewModel.uiState.value.gridItems.size)

        viewModel.resetGridItems()
        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(
            GridDefaults.getDefaultGridItems(isLandscape = false).map { it.id },
            viewModel.uiState.value.gridItems.map { it.id }
        )
    }
}

private const val VIEW_MODEL_SHUTDOWN_MS = 2_000L
private const val SHUTDOWN_POLL_MS = 5L
