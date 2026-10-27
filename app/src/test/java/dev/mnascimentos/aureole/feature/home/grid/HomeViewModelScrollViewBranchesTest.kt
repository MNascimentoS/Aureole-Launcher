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
import dev.mnascimentos.aureole.feature.home.grid.extensions.moveChildInScrollView
import dev.mnascimentos.aureole.feature.home.grid.extensions.openAddContainerForParent
import dev.mnascimentos.aureole.feature.home.grid.extensions.removeChildFromScrollView
import dev.mnascimentos.aureole.feature.home.grid.extensions.resizeChildInScrollView
import dev.mnascimentos.aureole.feature.home.grid.extensions.updateScrollViewOrientation
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
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelScrollViewBranchesTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var gridRepository: GridRepository

    private val childA = LauncherItemState(id = "a", type = LauncherItemType.CLOCK)
    private val childB = LauncherItemState(id = "b", type = LauncherItemType.APPS_LIST)
    private val childC = LauncherItemState(id = "c", type = LauncherItemType.QUICK_BUTTON)
    private val parent = LauncherItemState(
        id = PARENT,
        type = LauncherItemType.SCROLL_VIEW,
        colSpan = 4,
        rowSpan = 4,
        children = listOf(childA, childB, childC)
    )
    private val other = LauncherItemState(id = "other", type = LauncherItemType.CLOCK, col = 0, row = 5)

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

    private fun givenGrid(isEditMode: Boolean, isEditingParent: Boolean) {
        viewModel.updateUiState {
            MainUiState(
                gridItems = listOf(parent, other),
                portraitGridItems = listOf(parent, other),
                isGridEditMode = isEditMode,
                editingGridItem = if (isEditingParent) parent else other
            )
        }
    }

    private fun parentChildren(state: MainUiState = viewModel.uiState.value) =
        state.gridItems.first { it.id == PARENT }.safeChildren.map { it.id }

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
    fun `when opening add container for a parent, should remember the parent and show the dialog`() {
        viewModel.openAddContainerForParent(PARENT)

        assertEquals(PARENT, viewModel.uiState.value.targetParentContainerId)
        assertTrue(viewModel.uiState.value.showAddContainerDialog)
    }

    @Test
    fun `when changing orientation outside edit mode, should save it and keep the other editing item`() {
        givenGrid(isEditMode = false, isEditingParent = false)

        viewModel.updateScrollViewOrientation(PARENT, ScrollOrientation.HORIZONTAL)

        val state = viewModel.uiState.value
        assertEquals(ScrollOrientation.HORIZONTAL, state.gridItems.first { it.id == PARENT }.scrollOrientation)
        assertEquals(ScrollOrientation.VERTICAL, state.gridItems.first { it.id == "other" }.scrollOrientation)
        assertSame(other, state.editingGridItem)
        assertEquals(
            ScrollOrientation.HORIZONTAL,
            gridRepository.getGridItems(isLandscape = false).first { it.id == PARENT }.scrollOrientation
        )
    }

    @Test
    fun `when changing orientation of the item being edited, should update the editing copy`() {
        givenGrid(isEditMode = true, isEditingParent = true)

        viewModel.updateScrollViewOrientation(PARENT, ScrollOrientation.HORIZONTAL)

        assertEquals(ScrollOrientation.HORIZONTAL, viewModel.uiState.value.editingGridItem?.scrollOrientation)
    }

    @Test
    fun `when adding a child, should append it, cap its span and close the add dialog`() {
        givenGrid(isEditMode = true, isEditingParent = true)
        viewModel.openAddContainerForParent(PARENT)

        viewModel.addChildToScrollView(PARENT, LauncherItemType.WIDGET_LIST, widgetId = 7)

        val state = viewModel.uiState.value
        val added = state.gridItems.first { it.id == PARENT }.safeChildren.last()
        assertEquals(LauncherItemType.WIDGET_LIST, added.type)
        assertEquals(7, added.widgetId)
        assertTrue(added.colSpan <= MAX_CHILD_SPAN && added.rowSpan <= MAX_CHILD_SPAN)
        assertEquals(4, state.editingGridItem?.safeChildren?.size)
        assertFalse(state.showAddContainerDialog)
        assertNull(state.targetParentContainerId)
    }

    @Test
    fun `when adding a child outside edit mode to a parent not being edited, should save it`() {
        givenGrid(isEditMode = false, isEditingParent = false)

        viewModel.addChildToScrollView(PARENT, LauncherItemType.CLOCK)

        assertEquals(4, gridRepository.getGridItems(isLandscape = false).first { it.id == PARENT }.safeChildren.size)
        assertSame(other, viewModel.uiState.value.editingGridItem)
    }

    @Test
    fun `when resizing a child, should change only that child and never go below one cell`() {
        givenGrid(isEditMode = true, isEditingParent = true)

        viewModel.resizeChildInScrollView(PARENT, "b", newColSpan = 0, newRowSpan = 2)

        val children = viewModel.uiState.value.gridItems.first { it.id == PARENT }.safeChildren
        assertEquals(1, children.first { it.id == "b" }.colSpan)
        assertEquals(2, children.first { it.id == "b" }.rowSpan)
        assertEquals(childA, children.first { it.id == "a" })
        assertEquals(2, viewModel.uiState.value.editingGridItem?.safeChildren?.first { it.id == "b" }?.rowSpan)
    }

    @Test
    fun `when resizing outside edit mode, should save the new span`() {
        givenGrid(isEditMode = false, isEditingParent = false)

        viewModel.resizeChildInScrollView(PARENT, "a", newColSpan = 3, newRowSpan = 3)

        val saved = gridRepository.getGridItems(isLandscape = false).first { it.id == PARENT }.safeChildren
        assertEquals(3, saved.first { it.id == "a" }.colSpan)
    }

    @Test
    fun `when removing a child, should drop it from the grid and the editing copy`() {
        givenGrid(isEditMode = true, isEditingParent = true)

        viewModel.removeChildFromScrollView(PARENT, "b")

        assertEquals(listOf("a", "c"), parentChildren())
        assertEquals(listOf("a", "c"), viewModel.uiState.value.editingGridItem?.safeChildren?.map { it.id })
    }

    @Test
    fun `when removing a child outside edit mode, should save the change`() {
        givenGrid(isEditMode = false, isEditingParent = false)

        viewModel.removeChildFromScrollView(PARENT, "a")

        val saved = gridRepository.getGridItems(isLandscape = false).first { it.id == PARENT }.safeChildren
        assertEquals(listOf("b", "c"), saved.map { it.id })
    }

    @Test
    fun `when moving a child up and down, should reorder it`() {
        givenGrid(isEditMode = true, isEditingParent = true)

        viewModel.moveChildInScrollView(PARENT, "b", moveUp = true)
        assertEquals(listOf("b", "a", "c"), parentChildren())

        viewModel.moveChildInScrollView(PARENT, "b", moveUp = false)
        assertEquals(listOf("a", "b", "c"), parentChildren())
        assertEquals(listOf("a", "b", "c"), viewModel.uiState.value.editingGridItem?.safeChildren?.map { it.id })
    }

    @Test
    fun `when moving outside edit mode, should save the new order`() {
        givenGrid(isEditMode = false, isEditingParent = false)

        viewModel.moveChildInScrollView(PARENT, "c", moveUp = true)

        val saved = gridRepository.getGridItems(isLandscape = false).first { it.id == PARENT }.safeChildren
        assertEquals(listOf("a", "c", "b"), saved.map { it.id })
    }

    @Test
    fun `when a move is impossible, should leave the state untouched`() {
        givenGrid(isEditMode = true, isEditingParent = true)
        val before = viewModel.uiState.value

        viewModel.moveChildInScrollView(PARENT, "a", moveUp = true)
        viewModel.moveChildInScrollView(PARENT, "c", moveUp = false)
        viewModel.moveChildInScrollView(PARENT, "missing", moveUp = true)

        assertEquals(before.gridState(), viewModel.uiState.value.gridState())
    }

    @Test
    fun `when the parent is missing and nothing is being edited, should leave the state untouched`() {
        viewModel.updateUiState { MainUiState(gridItems = listOf(other)) }
        val before = viewModel.uiState.value

        viewModel.moveChildInScrollView("ghost", "a", moveUp = false)

        assertEquals(before.gridState(), viewModel.uiState.value.gridState())
    }

    @Test
    fun `when the parent only exists as the editing item, should move its children`() {
        viewModel.updateUiState {
            MainUiState(
                gridItems = listOf(other),
                editingGridItem = parent,
                isGridEditMode = true
            )
        }

        viewModel.moveChildInScrollView(PARENT, "a", moveUp = false)

        assertEquals(listOf("b", "a", "c"), viewModel.uiState.value.editingGridItem?.safeChildren?.map { it.id })
    }

    private companion object {
        const val PARENT = "scroll_parent"
        const val MAX_CHILD_SPAN = 3
    }
}
