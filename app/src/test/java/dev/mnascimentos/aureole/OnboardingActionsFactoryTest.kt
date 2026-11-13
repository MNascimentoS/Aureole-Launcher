package dev.mnascimentos.aureole

import android.app.Application
import android.app.role.RoleManager
import android.content.Intent
import android.provider.Settings
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ClockConfigRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.extensions.cancelGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.extensions.saveGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class OnboardingActionsFactoryTest {

    private val context: Application = RuntimeEnvironment.getApplication()
    private lateinit var viewModel: HomeViewModel
    private val calls = mutableListOf<String>()
    private val portraitLimits = GridLimits(GridEngineUtils.PORTRAIT_MAX_COLS, GridEngineUtils.PORTRAIT_MAX_ROWS)
    private val launchedIntents = mutableListOf<Intent>()

    private val homeActions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = { calls += "settings" },
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {},
        onOpenFavoritePicker = { id -> calls += "favorites:$id" },
        onChooseFavorites = { id -> calls += "chooseFavorites:$id" },
        onOpenEditContainerDialog = { id -> calls += "container:$id" },
        onAddGridItem = { type, _, _ -> calls += "add:$type" },
        onSetIsAddingSingleWidget = { adding -> calls += "singleWidget:$adding" },
        onAddWidgetClick = { stack -> calls += "widgetPicker:$stack" },
        onOpenEditClockBottomSheet = { calls += "clock:$it" },
        onSaveGridEditMode = { calls += "saveGrid" }
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val settingsRepository = SettingsRepository(context)
        settingsRepository.isLeftHandedMode = false
        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepository,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository),
                containerRepository = ContainerRepository(context),
                gridRepository = GridRepository(context),
                clockConfigRepository = ClockConfigRepository(context, GridRepository(context), settingsRepository)
            ),
            gridLoadDispatcher = UnconfinedTestDispatcher()
        )
        viewModel.updateUiState {
            it.copy(
                gridItems = emptyList(),
                gridLimits = portraitLimits,
                onboarding = OnboardingUiState(stage = OnboardingStage.CHECKLIST)
            )
        }
    }

    @After
    fun tearDown() {
        // Let the init's background loads finish before Dispatchers.Main is swapped back.
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        Dispatchers.resetMain()
    }

    private fun actions(): OnboardingActions =
        OnboardingActionsFactory(context, viewModel, homeActions) { launchedIntents += it }.createOnboardingActions()

    private fun withGrid(vararg items: LauncherItemState) {
        viewModel.updateUiState { it.copy(gridItems = items.toList()) }
    }

    @Test
    fun `when the favorites task is tapped, should collapse the list and open Choose favorites of the first list`() {
        withGrid(LauncherItemState(id = "apps_1", type = LauncherItemType.APPS_LIST))

        actions().onTaskClick(OnboardingTask.ADD_FAVORITE_APPS)

        assertEquals(listOf("chooseFavorites:apps_1"), calls)
        assertFalse(viewModel.uiState.value.onboarding.isChecklistExpanded)
    }

    @Test
    fun `when there is no apps list, should place one with no edit mode and open its favorites`() {
        actions().onTaskClick(OnboardingTask.ADD_FAVORITE_APPS)

        val state = viewModel.uiState.value
        val list = state.gridItems.single()
        assertEquals(listOf("chooseFavorites:${list.id}"), calls)
        assertEquals(LauncherItemType.APPS_LIST, list.safeType)
        assertFalse(state.isGridEditMode)
        assertEquals(OnboardingTask.ADD_FAVORITE_APPS, state.onboarding.pendingTask)
        assertEquals(list.id, state.onboarding.createdItemId)
    }

    @Test
    fun `when the container task is tapped with a container on the home, should open the editor of that one`() {
        withGrid(LauncherItemState(id = "box", type = LauncherItemType.SHORTCUTS_CONTAINER, colSpan = 4, rowSpan = 4))

        actions().onTaskClick(OnboardingTask.FILL_CONTAINER)

        val state = viewModel.uiState.value
        assertEquals(listOf("container:box"), calls)
        assertEquals(OnboardingTask.FILL_CONTAINER, state.onboarding.pendingTask)
        assertNull(state.onboarding.createdItemId)
        assertFalse(state.isGridEditMode)
    }

    @Test
    fun `when there is no container, should save a 4x6 one and open its editor, task pending`() {
        actions().onTaskClick(OnboardingTask.FILL_CONTAINER)

        val state = viewModel.uiState.value
        val container = state.gridItems.single()
        assertEquals(listOf("container:${container.id}"), calls)
        assertEquals(LauncherItemType.SHORTCUTS_CONTAINER, container.safeType)
        assertEquals(4, container.colSpan)
        assertEquals(6, container.rowSpan)
        assertFalse(state.isGridEditMode)
        assertEquals(OnboardingTask.FILL_CONTAINER, state.onboarding.pendingTask)
        assertEquals(container.id, state.onboarding.createdItemId)
        assertFalse(state.onboarding.isChecklistExpanded)
    }

    @Test
    fun `when the widget task is tapped, should save a compact widget list and open the picker for it`() {
        actions().onTaskClick(OnboardingTask.ADD_WIDGET)

        val state = viewModel.uiState.value
        val list = state.gridItems.single()
        assertEquals(listOf("widgetPicker:${list.id}"), calls)
        assertEquals(LauncherItemType.WIDGET_LIST, list.safeType)
        assertFalse(state.isGridEditMode)
        assertEquals(OnboardingTask.ADD_WIDGET, state.onboarding.pendingTask)
        assertEquals(list.id, state.onboarding.createdItemId)
    }

    @Test
    fun `when the clock task is tapped, should save a 10x4 clock and open its editor, task pending`() {
        actions().onTaskClick(OnboardingTask.ADD_CLOCK)

        val state = viewModel.uiState.value
        val clock = state.gridItems.single()
        assertEquals(listOf("clock:${clock.id}"), calls)
        assertEquals(LauncherItemType.CLOCK, clock.safeType)
        assertEquals(10, clock.colSpan)
        assertEquals(4, clock.rowSpan)
        assertTrue(state.clockConfigs.containsKey(clock.id))
        assertFalse(state.isGridEditMode)
        assertEquals(OnboardingTask.ADD_CLOCK, state.onboarding.pendingTask)
        assertEquals(clock.id, state.onboarding.createdItemId)
        assertFalse(state.onboarding.isChecklistExpanded)
    }

    @Test
    fun `when the grid has no room, should add no item and bring the list back`() {
        viewModel.updateUiState { it.copy(gridLimits = GridLimits(1, 1)) }
        withGrid(LauncherItemState(id = "full", type = LauncherItemType.QUICK_BUTTON, colSpan = 1, rowSpan = 1))

        actions().onTaskClick(OnboardingTask.ADD_CLOCK)

        val state = viewModel.uiState.value
        assertTrue(calls.isEmpty())
        assertEquals(1, state.gridItems.size)
        assertTrue(OnboardingTask.ADD_CLOCK !in state.onboarding.completedTasks)
        assertTrue(state.onboarding.isChecklistExpanded)
    }

    @Test
    fun `when the layout task is tapped before any item is done, should only warn`() {
        actions().onTaskClick(OnboardingTask.ADJUST_LAYOUT)

        val state = viewModel.uiState.value
        assertFalse(state.isGridEditMode)
        assertNull(state.onboarding.pendingTask)
        assertTrue(state.onboarding.isChecklistExpanded)
        assertNotNull(state.gridErrorMessage)
    }

    @Test
    fun `when the layout task is tapped after an item is done, should open the edit mode and wait for the save`() {
        viewModel.updateUiState {
            it.copy(onboarding = it.onboarding.copy(completedTasks = setOf(OnboardingTask.ADD_CLOCK)))
        }

        actions().onTaskClick(OnboardingTask.ADJUST_LAYOUT)
        assertTrue(viewModel.uiState.value.isGridEditMode)
        assertEquals(OnboardingTask.ADJUST_LAYOUT, viewModel.uiState.value.onboarding.pendingTask)
        assertFalse(viewModel.uiState.value.onboarding.isChecklistExpanded)

        viewModel.saveGridEditMode()
        assertTrue(OnboardingTask.ADJUST_LAYOUT in viewModel.uiState.value.onboarding.completedTasks)
        assertNull(viewModel.uiState.value.onboarding.pendingTask)
    }

    @Test
    fun `when the layout edit mode is cancelled, should keep the task pending`() {
        viewModel.updateUiState {
            it.copy(onboarding = it.onboarding.copy(completedTasks = setOf(OnboardingTask.ADD_CLOCK)))
        }

        actions().onTaskClick(OnboardingTask.ADJUST_LAYOUT)
        viewModel.cancelGridEditMode()

        val onboarding = viewModel.uiState.value.onboarding
        assertTrue(OnboardingTask.ADJUST_LAYOUT !in onboarding.completedTasks)
        assertNull(onboarding.pendingTask)
        assertTrue(onboarding.isChecklistExpanded)
    }

    @Test
    fun `when a task is skipped and tapped again, should be pending again and start`() {
        val actions = actions()

        actions.onSkipTask(OnboardingTask.ADD_CLOCK)
        assertEquals(setOf(OnboardingTask.ADD_CLOCK), viewModel.uiState.value.onboarding.skippedTasks)
        assertTrue(viewModel.uiState.value.gridItems.isEmpty())

        actions.onTaskClick(OnboardingTask.ADD_CLOCK)
        assertTrue(viewModel.uiState.value.onboarding.skippedTasks.isEmpty())
        assertEquals(OnboardingTask.ADD_CLOCK, viewModel.uiState.value.onboarding.pendingTask)
        assertEquals(1, viewModel.uiState.value.gridItems.size)
    }

    @Test
    fun `when the edit hint saves, should save the grid`() {
        actions().onSaveGridEdit()

        assertEquals(listOf("saveGrid"), calls)
    }

    @Test
    fun `when the settings task is tapped, should open settings`() {
        actions().onTaskClick(OnboardingTask.EXPLORE_SETTINGS)

        assertEquals(listOf("settings"), calls)
    }

    @Test
    fun `when an appearance task is tapped, should open its picker without other editors`() {
        actions().onTaskClick(OnboardingTask.CHOOSE_PALETTE)

        assertTrue(calls.isEmpty())
        assertEquals(AppearancePicker.PALETTE, viewModel.uiState.value.onboarding.activePicker)
    }

    @Test
    @Config(sdk = [33])
    fun `when the role dialog is available, should launch it`() {
        shadowOf(context.getSystemService(RoleManager::class.java)).addAvailableRole(RoleManager.ROLE_HOME)

        actions().onTaskClick(OnboardingTask.SET_DEFAULT_HOME)

        assertEquals(1, launchedIntents.size)
        assertFalse(viewModel.uiState.value.onboarding.defaultHomeAttempted)
    }

    @Test
    @Config(sdk = [28])
    fun `when the role dialog is unavailable, should open the system screen and remember the attempt`() {
        actions().onRequestDefaultHome()

        assertTrue(launchedIntents.isEmpty())
        assertEquals(Settings.ACTION_HOME_SETTINGS, shadowOf(context).nextStartedActivity.action)
        assertTrue(viewModel.uiState.value.onboarding.defaultHomeAttempted)
    }

    @Test
    fun `when show me is used during gestures, should open the drawer`() {
        viewModel.updateUiState { it.copy(onboarding = OnboardingUiState(stage = OnboardingStage.GESTURES)) }

        actions().onShowMeGesture()

        assertTrue(viewModel.uiState.value.isAllAppsDrawerOpen)
    }

    @Test
    fun `when the scrubber reports a letter during gestures, should complete the scrub step`() {
        viewModel.updateUiState { it.copy(onboarding = OnboardingUiState(stage = OnboardingStage.GESTURES)) }

        actions().onAlphabetScrubbed()

        assertTrue(viewModel.uiState.value.onboarding.completedSteps.isNotEmpty())
    }

    @Test
    fun `when checklist callbacks run, should reach the view model`() {
        val actions = actions()

        actions.onSetChecklistExpanded(false)
        assertFalse(viewModel.uiState.value.onboarding.isChecklistExpanded)

        actions.onTaskClick(OnboardingTask.CHOOSE_FONT)
        actions.onSelectFont("Space Grotesk")
        actions.onConfirmAppearancePicker()
        assertTrue(OnboardingTask.CHOOSE_FONT in viewModel.uiState.value.onboarding.completedTasks)

        actions.onTaskClick(OnboardingTask.CHOOSE_PALETTE)
        actions.onSelectPalette("Abyss")
        actions.onDismissAppearancePicker()
        assertEquals("Abyss", viewModel.uiState.value.selectedThemeName)

        actions.onDismissChecklist()
        assertEquals(OnboardingStage.COMPLETED, viewModel.uiState.value.onboarding.stage)
        actions.onFinishCelebration()
    }

    @Test
    fun `when the intro callbacks run, should move through the stages`() {
        viewModel.updateUiState { it.copy(onboarding = OnboardingUiState(stage = OnboardingStage.WELCOME)) }
        val actions = actions()

        actions.onStart()
        assertEquals(OnboardingStage.DEFAULT_HOME, viewModel.uiState.value.onboarding.stage)
        actions.onPostponeDefaultHome()
        assertEquals(OnboardingStage.GESTURES, viewModel.uiState.value.onboarding.stage)
        actions.onSkipGestures()
        assertEquals(OnboardingStage.CHECKLIST, viewModel.uiState.value.onboarding.stage)

        viewModel.updateUiState { it.copy(onboarding = OnboardingUiState(stage = OnboardingStage.WELCOME)) }
        actions.onSkipIntro()
        assertEquals(OnboardingStage.CHECKLIST, viewModel.uiState.value.onboarding.stage)
    }
}
