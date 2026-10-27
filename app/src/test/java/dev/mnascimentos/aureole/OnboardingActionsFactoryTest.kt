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
        onOpenEditContainerDialog = { id -> calls += "container:$id" },
        onAddGridItem = { type, _, _ -> calls += "add:$type" },
        onSetIsAddingSingleWidget = { adding -> calls += "singleWidget:$adding" },
        onAddWidgetClick = { stack -> calls += "widgetPicker:$stack" }
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        val settingsRepository = SettingsRepository(context)
        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepository,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository),
                containerRepository = ContainerRepository(context),
                gridRepository = GridRepository(context)
            )
        )
        viewModel.updateUiState {
            it.copy(gridItems = emptyList(), onboarding = OnboardingUiState(stage = OnboardingStage.CHECKLIST))
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
    fun `when the favorites task is tapped, should collapse the list and open the first apps list picker`() {
        withGrid(LauncherItemState(id = "apps_1", type = LauncherItemType.APPS_LIST))

        actions().onTaskClick(OnboardingTask.ADD_FAVORITE_APPS)

        assertEquals(listOf("favorites:apps_1"), calls)
        assertFalse(viewModel.uiState.value.onboarding.isChecklistExpanded)
    }

    @Test
    fun `when there is no apps list, should edit the global favorites`() {
        actions().onTaskClick(OnboardingTask.ADD_FAVORITE_APPS)

        assertEquals(listOf("favorites:null"), calls)
    }

    @Test
    fun `when the container task is tapped, should edit the first container, even inside a scroll view`() {
        val container = LauncherItemState(id = "box", type = LauncherItemType.SHORTCUTS_CONTAINER)
        withGrid(
            LauncherItemState(id = "scroll", type = LauncherItemType.SCROLL_VIEW, children = listOf(container))
        )

        actions().onTaskClick(OnboardingTask.FILL_CONTAINER)

        assertEquals(listOf("container:box"), calls)
    }

    @Test
    fun `when there is no container, should add one`() {
        actions().onTaskClick(OnboardingTask.FILL_CONTAINER)

        assertEquals(listOf("add:${LauncherItemType.SHORTCUTS_CONTAINER}"), calls)
    }

    @Test
    fun `when the widget task is tapped, should go straight to the single widget picker`() {
        actions().onTaskClick(OnboardingTask.ADD_WIDGET)

        assertEquals(listOf("singleWidget:true", "widgetPicker:null"), calls)
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
