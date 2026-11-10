package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
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
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSignal
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.extensions.cancelGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.extensions.saveGridEditMode
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

/** Grid tasks complete only when the user saves (FUE-17), and failed ones undo their item. */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelOnboardingTaskTest {

    private lateinit var context: Context
    private lateinit var gridRepository: GridRepository
    private lateinit var viewModel: HomeViewModel

    private val state: MainUiState
        get() = viewModel.uiState.value

    private val onboarding: OnboardingUiState
        get() = state.onboarding

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences(GRID_PREFS, Context.MODE_PRIVATE).edit().clear().commit()
        val settingsRepository = SettingsRepository(context)
        settingsRepository.isLeftHandedMode = false
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
        awaitInitialLoad()
        viewModel.updateUiState {
            it.copy(
                gridItems = emptyList(),
                portraitGridItems = emptyList(),
                landscapeGridItems = emptyList(),
                isLandscape = false,
                gridLimits = GridLimits(GridEngineUtils.PORTRAIT_MAX_COLS, GridEngineUtils.PORTRAIT_MAX_ROWS),
                favoriteAppPackages = emptyList(),
                containerFavorites = emptyMap(),
                widgetStacks = emptyMap(),
                onboarding = OnboardingUiState(stage = OnboardingStage.CHECKLIST, isChecklistExpanded = false)
            )
        }
    }

    @After
    fun tearDown() {
        // Let the init's background loads finish before Dispatchers.Main is swapped back.
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        context.getSharedPreferences(GRID_PREFS, Context.MODE_PRIVATE).edit().clear().commit()
        Dispatchers.resetMain()
    }

    /** The init loads favorites and folders on Dispatchers.IO; wait so they cannot wipe the arranged state. */
    private fun awaitInitialLoad() {
        val sentinel = AppFolder(id = "__init_sentinel__", name = "")
        viewModel.updateUiState { it.copy(folders = listOf(sentinel)) }
        val deadline = System.currentTimeMillis() + INIT_LOAD_TIMEOUT_MS
        while (state.folders == listOf(sentinel) && System.currentTimeMillis() < deadline) {
            Thread.sleep(POLL_MS)
        }
    }

    private fun openEditor(transform: (MainUiState) -> MainUiState) = viewModel.updateUiState(transform)

    @Test
    fun `when favorites are marked with the picker open, should not complete`() {
        viewModel.startGridTask(OnboardingTask.ADD_FAVORITE_APPS)
        openEditor { it.copy(showFavoritePickerDialog = true) }

        viewModel.updateUiState { it.copy(containerFavorites = mapOf(GridDefaults.APPS_LIST_ITEM_ID to listOf("a"))) }

        assertTrue(onboarding.completedTasks.isEmpty())
        assertEquals(OnboardingTask.ADD_FAVORITE_APPS, onboarding.pendingTask)
        assertFalse(onboarding.isChecklistExpanded)
    }

    @Test
    fun `when the picker closes with the edit mode still open, should not complete`() {
        viewModel.startGridTask(OnboardingTask.ADD_FAVORITE_APPS)
        openEditor { it.copy(showFavoritePickerDialog = true, favoriteAppPackages = listOf("a")) }

        viewModel.updateUiState { it.copy(showFavoritePickerDialog = false) }

        assertTrue(onboarding.completedTasks.isEmpty())
        assertEquals(OnboardingTask.ADD_FAVORITE_APPS, onboarding.pendingTask)
    }

    @Test
    fun `when the layout is saved with favorites, should complete and reopen the list`() {
        viewModel.startGridTask(OnboardingTask.ADD_FAVORITE_APPS)
        openEditor { it.copy(showFavoritePickerDialog = true) }
        viewModel.updateUiState {
            it.copy(
                showFavoritePickerDialog = false,
                containerFavorites = mapOf(GridDefaults.APPS_LIST_ITEM_ID to listOf("a"))
            )
        }

        viewModel.saveGridEditMode()

        assertEquals(setOf(OnboardingTask.ADD_FAVORITE_APPS), onboarding.completedTasks)
        assertNull(onboarding.pendingTask)
        assertNull(onboarding.createdItemId)
        assertTrue(onboarding.isChecklistExpanded)
        assertEquals(LauncherItemType.APPS_LIST, state.gridItems.single().safeType)
    }

    @Test
    fun `when the layout is saved without favorites, should show all apps instead and keep the task`() {
        viewModel.startGridTask(OnboardingTask.ADD_FAVORITE_APPS)
        openEditor { it.copy(showFavoritePickerDialog = true) }
        viewModel.updateUiState { it.copy(showFavoritePickerDialog = false) }

        viewModel.saveGridEditMode()

        val allApps = state.gridItems.single()
        val defaultApps = GridDefaults.getDefaultGridItems()[2]
        assertEquals(LauncherItemType.ALL_APPS_LIST, allApps.safeType)
        assertEquals(listOf(defaultApps.col, defaultApps.row), listOf(allApps.col, allApps.row))
        assertEquals(listOf(allApps), gridRepository.getGridItems())
        assertTrue(onboarding.completedTasks.isEmpty())
        assertNull(onboarding.pendingTask)
        assertTrue(onboarding.isChecklistExpanded)
    }

    @Test
    fun `when the edit mode is cancelled, should drop the item and not complete`() {
        viewModel.startGridTask(OnboardingTask.ADD_CLOCK)
        openEditor { it.copy(showEditClockBottomSheet = true) }
        viewModel.updateUiState { it.copy(showEditClockBottomSheet = false) }

        viewModel.cancelGridEditMode()

        assertTrue(state.gridItems.isEmpty())
        assertTrue(onboarding.completedTasks.isEmpty())
        assertNull(onboarding.pendingTask)
        assertTrue(onboarding.isChecklistExpanded)
    }

    @Test
    fun `when the container is saved empty, should remove the created container`() {
        viewModel.startGridTask(OnboardingTask.FILL_CONTAINER)
        openEditor { it.copy(isEditContainerDialogVisible = true) }
        viewModel.updateUiState { it.copy(isEditContainerDialogVisible = false) }

        viewModel.saveGridEditMode()

        assertTrue(state.gridItems.isEmpty())
        assertTrue(gridRepository.getGridItems().isEmpty())
        assertTrue(onboarding.completedTasks.isEmpty())
    }

    @Test
    fun `when the container editor saves, should complete even empty and save the layout`() {
        val id = viewModel.startGridTask(OnboardingTask.FILL_CONTAINER)!!
        openEditor { it.copy(isEditContainerDialogVisible = true) }

        viewModel.confirmContainerTaskOnSave()

        assertEquals(setOf(OnboardingTask.FILL_CONTAINER), onboarding.completedTasks)
        assertFalse(state.isGridEditMode)
        assertEquals(listOf(id), gridRepository.getGridItems().map { it.id })

        viewModel.updateUiState { it.copy(isEditContainerDialogVisible = false) }
        assertEquals(listOf(id), state.gridItems.map { it.id })
    }

    @Test
    fun `when a filled container editor closes without its save, should remove the created container`() {
        val id = viewModel.startGridTask(OnboardingTask.FILL_CONTAINER)!!
        val app = ContainerItemEntity(id = "i", panelId = id, itemType = ContainerItemType.APP, packageName = "a")
        openEditor { it.copy(isEditContainerDialogVisible = true) }
        viewModel.updateUiState {
            it.copy(
                isEditContainerDialogVisible = false,
                containers = mapOf(id to ContainerModel(id = id, items = listOf(app)))
            )
        }

        viewModel.saveGridEditMode()

        assertTrue(onboarding.completedTasks.isEmpty())
        assertTrue(state.gridItems.isEmpty())
    }

    @Test
    fun `when the container save runs with no container task, should leave the onboarding alone`() {
        viewModel.startGridTask(OnboardingTask.ADD_CLOCK)

        viewModel.confirmContainerTaskOnSave()

        assertEquals(OnboardingTask.ADD_CLOCK, onboarding.pendingTask)
        assertTrue(state.isGridEditMode)
    }

    @Test
    fun `when settings is opened, should complete only after returning to the home`() {
        viewModel.onOnboardingSignal(OnboardingSignal.SETTINGS_OPENED)
        viewModel.onHomeHidden()
        assertTrue(onboarding.completedTasks.isEmpty())

        viewModel.onReturnedToHome()

        assertEquals(setOf(OnboardingTask.EXPLORE_SETTINGS), onboarding.completedTasks)
        assertTrue(onboarding.isHomeInForeground)
        assertTrue(onboarding.isChecklistExpanded)
    }

    @Test
    fun `when settings is the last task, should celebrate only after returning to the home`() {
        val allButSettings = OnboardingTask.entries.toSet() - OnboardingTask.EXPLORE_SETTINGS
        viewModel.updateUiState { it.copy(onboarding = it.onboarding.copy(completedTasks = allButSettings)) }
        viewModel.onOnboardingSignal(OnboardingSignal.SETTINGS_OPENED)
        viewModel.onHomeHidden()
        assertFalse(onboarding.isCelebrating)

        viewModel.onReturnedToHome()

        assertEquals(OnboardingStage.COMPLETED, onboarding.stage)
        assertTrue(onboarding.isCelebrating)
    }

    @Test
    fun `when settings was done and the last task is saved on the home, should celebrate right away`() {
        val allButClock = OnboardingTask.entries.toSet() - OnboardingTask.ADD_CLOCK
        viewModel.updateUiState { it.copy(onboarding = it.onboarding.copy(completedTasks = allButClock)) }
        viewModel.startGridTask(OnboardingTask.ADD_CLOCK)

        viewModel.saveGridEditMode()

        assertEquals(OnboardingStage.COMPLETED, onboarding.stage)
        assertTrue(onboarding.isCelebrating)
    }

    @Test
    fun `when the last task completes with the home hidden, should celebrate on the next resume`() {
        val allButDefaultHome = OnboardingTask.entries.toSet() - OnboardingTask.SET_DEFAULT_HOME
        viewModel.updateUiState { it.copy(onboarding = it.onboarding.copy(completedTasks = allButDefaultHome)) }
        viewModel.onHomeHidden()

        viewModel.updateUiState { it.copy(onboarding = it.onboarding.copy(isDefaultLauncher = true)) }
        assertEquals(OnboardingStage.COMPLETED, onboarding.stage)
        assertFalse(onboarding.isCelebrating)
        assertTrue(onboarding.isCelebrationPending)

        viewModel.onReturnedToHome()

        assertTrue(onboarding.isCelebrating)
        assertFalse(onboarding.isCelebrationPending)
    }

    @Test
    fun `when the widget picker is cancelled, should remove the created widget list`() {
        viewModel.startGridTask(OnboardingTask.ADD_WIDGET)
        openEditor { it.copy(showWidgetPicker = true) }
        viewModel.updateUiState { it.copy(showWidgetPicker = false) }

        viewModel.saveGridEditMode()

        assertTrue(state.gridItems.isEmpty())
        assertTrue(onboarding.completedTasks.isEmpty())
    }

    @Test
    fun `when a widget lands in the placed list and the layout is saved, should complete`() {
        val id = viewModel.startGridTask(OnboardingTask.ADD_WIDGET)!!
        openEditor { it.copy(showWidgetPicker = true) }
        viewModel.updateUiState { it.copy(showWidgetPicker = false, widgetStacks = mapOf(id to listOf(5))) }

        viewModel.saveGridEditMode()

        assertEquals(setOf(OnboardingTask.ADD_WIDGET), onboarding.completedTasks)
    }

    @Test
    fun `when the clock is saved, should complete`() {
        viewModel.startGridTask(OnboardingTask.ADD_CLOCK)
        openEditor { it.copy(showEditClockBottomSheet = true) }
        viewModel.updateUiState { it.copy(showEditClockBottomSheet = false) }

        viewModel.saveGridEditMode()

        assertEquals(setOf(OnboardingTask.ADD_CLOCK), onboarding.completedTasks)
        assertEquals(GridDefaults.CLOCK_ITEM_ID, state.gridItems.single().id)
    }

    @Test
    fun `when the default spot is taken, should place the item in the largest free slot`() {
        val blocker = LauncherItemState(id = "blocker", type = LauncherItemType.QUICK_BUTTON, colSpan = 4, rowSpan = 3)
        viewModel.updateUiState { it.copy(gridItems = listOf(blocker), portraitGridItems = listOf(blocker)) }

        val id = viewModel.startGridTask(OnboardingTask.ADD_CLOCK)

        assertTrue(id != null && id != GridDefaults.CLOCK_ITEM_ID)
        assertEquals(2, state.gridItems.size)
    }

    @Test
    fun `when the task is not a grid task, should not start anything`() {
        assertNull(viewModel.startGridTask(OnboardingTask.CHOOSE_FONT))
        assertFalse(state.isGridEditMode)
    }

    private companion object {
        const val GRID_PREFS = "aureole_grid_prefs"
        const val INIT_LOAD_TIMEOUT_MS = 1_000L
        const val POLL_MS = 10L
    }
}
