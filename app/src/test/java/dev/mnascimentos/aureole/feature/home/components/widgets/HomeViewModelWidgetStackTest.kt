package dev.mnascimentos.aureole.feature.home.components.widgets

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
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.addWidgetId
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.loadWidgetSettings
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.removeWidgetId
import dev.mnascimentos.aureole.feature.home.components.widgets.extensions.reorderWidgets
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.WidgetStackBottomSheetState
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
class HomeViewModelWidgetStackTest {

    private lateinit var viewModel: HomeViewModel
    private lateinit var widgetRepository: WidgetRepository

    private val stack = LauncherItemState(id = "stack_1", type = LauncherItemType.WIDGET_LIST, colSpan = 4, rowSpan = 2)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val context: Context = RuntimeEnvironment.getApplication()
        val settingsRepository = SettingsRepository(context)
        widgetRepository = WidgetRepository(context)
        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = widgetRepository,
                settingsRepository = settingsRepository,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository),
                containerRepository = ContainerRepository(context),
                gridRepository = GridRepository(context)
            )
        )
    }

    @After
    fun tearDown() {
        // Let the init's background loads finish before Dispatchers.Main is swapped back.
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        Dispatchers.resetMain()
    }

    @Test
    fun `when loading widget settings, should read stacks of widget items and their children`() {
        widgetRepository.saveWidgetIdsForStack("stack_1", listOf(1, 2))
        widgetRepository.saveWidgetIdsForStack("child_stack", listOf(5))
        val scroll = LauncherItemState(
            id = "scroll",
            type = LauncherItemType.SCROLL_VIEW,
            children = listOf(LauncherItemState(id = "child_stack", type = LauncherItemType.SINGLE_APP_WIDGET))
        )
        viewModel.updateUiState { MainUiState(portraitGridItems = listOf(stack, scroll)) }

        viewModel.loadWidgetSettings()

        val stacks = viewModel.uiState.value.widgetStacks
        assertEquals(listOf(1, 2), stacks["stack_1"])
        assertEquals(listOf(5), stacks["child_stack"])
        assertFalse(stacks.containsKey("scroll"))
    }

    @Test
    fun `when adding a widget to an explicit stack, should append and save it`() {
        viewModel.updateUiState { MainUiState(gridItems = listOf(stack), widgetStacks = mapOf("stack_1" to listOf(1))) }

        viewModel.addWidgetId(2, targetStackId = "stack_1")

        assertEquals(listOf(1, 2), viewModel.uiState.value.widgetStacks["stack_1"])
        assertEquals(listOf(1, 2), widgetRepository.getWidgetIdsForStack("stack_1"))
        assertNull(viewModel.uiState.value.targetWidgetStackId)
    }

    @Test
    fun `when adding a widget that is already in the stack, should not duplicate it`() {
        viewModel.updateUiState { MainUiState(gridItems = listOf(stack), widgetStacks = mapOf("stack_1" to listOf(1))) }

        viewModel.addWidgetId(1, targetStackId = "stack_1")

        assertEquals(listOf(1), viewModel.uiState.value.widgetStacks["stack_1"])
    }

    @Test
    fun `when no stack is given, should use the target, then the open sheet, then the edited item`() {
        viewModel.updateUiState { MainUiState(gridItems = listOf(stack), targetWidgetStackId = "from_target") }
        viewModel.addWidgetId(10)
        assertEquals(listOf(10), viewModel.uiState.value.widgetStacks["from_target"])

        viewModel.updateUiState {
            it.copy(activeWidgetStackBottomSheet = WidgetStackBottomSheetState(widgetId = null, stackId = "from_sheet"))
        }
        viewModel.addWidgetId(11)
        assertEquals(listOf(11), viewModel.uiState.value.widgetStacks["from_sheet"])

        viewModel.updateUiState { it.copy(activeWidgetStackBottomSheet = null, editingGridItem = stack) }
        viewModel.addWidgetId(12)
        assertEquals(listOf(12), viewModel.uiState.value.widgetStacks["stack_1"])
    }

    @Test
    fun `when nothing points to a stack, should add to the top widgets`() {
        viewModel.updateUiState { MainUiState(gridItems = listOf(stack), topWidgetIds = listOf(3)) }

        viewModel.addWidgetId(4)

        assertEquals(listOf(3, 4), viewModel.uiState.value.topWidgetIds)
    }

    @Test
    fun `when adding a widget and the grid has no widget container, should create one`() {
        viewModel.updateUiState { MainUiState(isAddingSingleWidget = true) }

        viewModel.addWidgetId(20, targetStackId = "stack_x")

        val state = viewModel.uiState.value
        assertTrue(state.gridItems.any { it.safeType == LauncherItemType.WIDGET_LIST })
        assertFalse(state.isAddingSingleWidget)
    }

    @Test
    fun `when a scroll view already holds a widget, should not create another container`() {
        val scroll = LauncherItemState(
            id = "scroll",
            type = LauncherItemType.SCROLL_VIEW,
            children = listOf(LauncherItemState(id = "w", type = LauncherItemType.SINGLE_APP_WIDGET))
        )
        viewModel.updateUiState { MainUiState(gridItems = listOf(scroll)) }

        viewModel.addWidgetId(21, targetStackId = "w")

        assertEquals(1, viewModel.uiState.value.gridItems.size)
    }

    @Test
    fun `when removing a widget from the top stack, should update top ids and save`() {
        viewModel.updateUiState { MainUiState(topWidgetIds = listOf(1, 2, 3)) }

        viewModel.removeWidgetId(2, targetStackId = "top_widgets")

        assertEquals(listOf(1, 3), viewModel.uiState.value.topWidgetIds)
        assertEquals(listOf(1, 3), widgetRepository.getWidgetIdsForStack("top_widgets"))
    }

    @Test
    fun `when removing from another stack, should keep top ids`() {
        viewModel.updateUiState {
            MainUiState(
                topWidgetIds = listOf(9),
                widgetStacks = mapOf("stack_1" to listOf(1, 2))
            )
        }

        viewModel.removeWidgetId(1, targetStackId = "stack_1")

        assertEquals(listOf(2), viewModel.uiState.value.widgetStacks["stack_1"])
        assertEquals(listOf(9), viewModel.uiState.value.topWidgetIds)
    }

    @Test
    fun `when reordering, should store the new order for the right stack`() {
        viewModel.updateUiState {
            MainUiState(
                topWidgetIds = listOf(1, 2),
                widgetStacks = mapOf("stack_1" to listOf(5, 6))
            )
        }

        viewModel.reorderWidgets(listOf(6, 5), targetStackId = "stack_1")
        viewModel.reorderWidgets(listOf(2, 1), targetStackId = "top_widgets")

        assertEquals(listOf(6, 5), viewModel.uiState.value.widgetStacks["stack_1"])
        assertEquals(listOf(2, 1), viewModel.uiState.value.topWidgetIds)
        assertEquals(listOf(6, 5), widgetRepository.getWidgetIdsForStack("stack_1"))
    }
}
