package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.AppFolder
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
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSignal
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.blocksHome
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
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

/** Item tasks open their own screen with no edit mode, are judged when it closes (RF-40–43) and peek at the home. */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelOnboardingTaskTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private lateinit var context: Context
    private lateinit var gridRepository: GridRepository
    private lateinit var viewModel: HomeViewModel

    private val state: MainUiState
        get() = viewModel.uiState.value

    private val onboarding: OnboardingUiState
        get() = state.onboarding

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
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
                gridRepository = gridRepository,
                clockConfigRepository = ClockConfigRepository(context, gridRepository, settingsRepository)
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

    private fun closeScreen(open: (MainUiState) -> MainUiState, close: (MainUiState) -> MainUiState) {
        openEditor(open)
        viewModel.updateUiState(close)
    }

    private fun closeClockSheet() = closeScreen(
        open = { it.copy(showEditClockBottomSheet = true) },
        close = { it.copy(showEditClockBottomSheet = false) }
    )

    private fun closeFavorites(favorites: List<String>) = closeScreen(
        open = { it.copy(showFavoritePickerDialog = true) },
        close = { state ->
            state.copy(
                showFavoritePickerDialog = false,
                containerFavorites = state.containerFavorites + (GridDefaults.APPS_LIST_ITEM_ID to favorites)
            )
        }
    )

    private fun advancePeek(extraMs: Long = 0L) {
        dispatcher.scheduler.advanceTimeBy(onboardingHomePeekMillis() + extraMs)
        dispatcher.scheduler.runCurrent()
    }

    @Test
    fun `when an item task starts, should save the item with no edit mode and collapse the list`() {
        val id = viewModel.startGridTask(OnboardingTask.ADD_CLOCK)!!

        val clock = state.gridItems.single()
        assertEquals(id, clock.id)
        assertEquals(listOf(10, 4), listOf(clock.colSpan, clock.rowSpan))
        assertEquals(listOf(id), gridRepository.getGridItems().map { it.id })
        assertFalse(state.isGridEditMode)
        assertEquals(OnboardingTask.ADD_CLOCK, onboarding.pendingTask)
        assertEquals(id, onboarding.createdItemId)
        assertFalse(onboarding.isChecklistExpanded)
    }

    @Test
    fun `when the clock screen closes, should complete the task and show the home before the list`() {
        viewModel.startGridTask(OnboardingTask.ADD_CLOCK)

        closeClockSheet()

        assertEquals(setOf(OnboardingTask.ADD_CLOCK), onboarding.completedTasks)
        assertNull(onboarding.pendingTask)
        assertTrue(onboarding.isHomePeeking)
        assertFalse(onboarding.isChecklistExpanded)
        assertEquals(1, state.gridItems.size)

        advancePeek(extraMs = -1L)
        assertTrue(onboarding.isHomePeeking)
        assertFalse(onboarding.isChecklistExpanded)

        advancePeek(extraMs = 1L)
        assertFalse(onboarding.isHomePeeking)
        assertTrue(onboarding.isChecklistExpanded)
    }

    @Test
    fun `when the last task closes its screen, should celebrate and end the peek without the list`() {
        val allButClock = OnboardingTask.entries.toSet() - OnboardingTask.ADD_CLOCK
        viewModel.updateUiState { it.copy(onboarding = it.onboarding.copy(completedTasks = allButClock)) }
        viewModel.startGridTask(OnboardingTask.ADD_CLOCK)

        closeClockSheet()
        advancePeek()

        assertEquals(OnboardingStage.COMPLETED, onboarding.stage)
        assertTrue(onboarding.isCelebrating)
        assertFalse(onboarding.isHomePeeking)
    }

    @Test
    fun `when favorites are marked with the screen still open, should not judge the task`() {
        viewModel.startGridTask(OnboardingTask.ADD_FAVORITE_APPS)
        openEditor { it.copy(showFavoritePickerDialog = true) }

        viewModel.updateUiState { it.copy(containerFavorites = mapOf(GridDefaults.APPS_LIST_ITEM_ID to listOf("a"))) }

        assertTrue(onboarding.completedTasks.isEmpty())
        assertEquals(OnboardingTask.ADD_FAVORITE_APPS, onboarding.pendingTask)
        assertFalse(onboarding.isChecklistExpanded)
    }

    @Test
    fun `when the favorites screen closes with favorites, should complete the task`() {
        val listId = viewModel.startGridTask(OnboardingTask.ADD_FAVORITE_APPS)!!
        viewModel.updateUiState { it.copy(containerFavorites = mapOf(listId to listOf("a"))) }

        closeScreen(
            open = { it.copy(showFavoritePickerDialog = true) },
            close = { it.copy(showFavoritePickerDialog = false) }
        )

        assertEquals(setOf(OnboardingTask.ADD_FAVORITE_APPS), onboarding.completedTasks)
        assertNull(onboarding.createdItemId)
        assertEquals(LauncherItemType.APPS_LIST, state.gridItems.single().safeType)
    }

    @Test
    fun `when the favorites screen closes without favorites, should keep the list on all apps and the task pending`() {
        val listId = viewModel.startGridTask(OnboardingTask.ADD_FAVORITE_APPS)

        closeFavorites(emptyList())

        val list = state.gridItems.single()
        assertEquals(listId, list.id)
        assertTrue(list.isShowingAllApps)
        assertEquals(listOf(list), gridRepository.getGridItems())
        assertTrue(onboarding.completedTasks.isEmpty())
        assertNull(onboarding.pendingTask)
        assertTrue(onboarding.isHomePeeking)

        advancePeek()
        assertTrue(onboarding.isChecklistExpanded)
        assertEquals(listId, viewModel.startGridTask(OnboardingTask.ADD_FAVORITE_APPS))
        assertEquals(1, state.gridItems.size)
    }

    @Test
    fun `when the container editor saves empty and closes, should complete the task and keep the container`() {
        val id = viewModel.startGridTask(OnboardingTask.FILL_CONTAINER)!!
        openEditor { it.copy(isEditContainerDialogVisible = true) }

        viewModel.confirmContainerTaskOnSave()

        assertTrue(onboarding.isPendingTaskSaved)
        assertTrue(onboarding.completedTasks.isEmpty())
        assertEquals(OnboardingTask.FILL_CONTAINER, onboarding.pendingTask)

        viewModel.updateUiState { it.copy(isEditContainerDialogVisible = false) }

        assertEquals(setOf(OnboardingTask.FILL_CONTAINER), onboarding.completedTasks)
        assertFalse(onboarding.isPendingTaskSaved)
        assertFalse(state.isGridEditMode)
        assertEquals(listOf(id), state.gridItems.map { it.id })
        assertEquals(listOf(id), gridRepository.getGridItems().map { it.id })
    }

    @Test
    fun `when the container editor closes without saving, should keep the container and the task pending`() {
        val id = viewModel.startGridTask(OnboardingTask.FILL_CONTAINER)!!

        closeScreen(
            open = { it.copy(isEditContainerDialogVisible = true) },
            close = { it.copy(isEditContainerDialogVisible = false) }
        )

        assertTrue(onboarding.completedTasks.isEmpty())
        assertNull(onboarding.pendingTask)
        assertEquals(listOf(id), state.gridItems.map { it.id })
        assertTrue(onboarding.isHomePeeking)
    }

    @Test
    fun `when the container save runs with no container task, should leave the onboarding alone`() {
        viewModel.startGridTask(OnboardingTask.ADD_CLOCK)

        viewModel.confirmContainerTaskOnSave()

        assertEquals(OnboardingTask.ADD_CLOCK, onboarding.pendingTask)
        assertFalse(onboarding.isPendingTaskSaved)
    }

    @Test
    fun `when a widget is picked and the picker closes, should complete with no edit mode behind`() {
        val id = viewModel.startGridTask(OnboardingTask.ADD_WIDGET)!!

        closeScreen(
            open = { it.copy(showWidgetPicker = true) },
            close = { it.copy(showWidgetPicker = false, widgetStacks = mapOf(id to listOf(5))) }
        )

        assertEquals(setOf(OnboardingTask.ADD_WIDGET), onboarding.completedTasks)
        assertFalse(state.isGridEditMode)
        assertEquals(listOf(id), state.gridItems.map { it.id })
        advancePeek()
        assertTrue(onboarding.isChecklistExpanded)
    }

    @Test
    fun `when a widget is picked and its bind screen opens, should keep the task pending until it ends`() {
        val id = viewModel.startGridTask(OnboardingTask.ADD_WIDGET)!!
        openEditor { it.copy(showWidgetPicker = true) }

        viewModel.updateUiState { it.copy(showWidgetPicker = false, pendingWidgetId = 9) }
        assertEquals(OnboardingTask.ADD_WIDGET, onboarding.pendingTask)
        assertTrue(onboarding.completedTasks.isEmpty())

        viewModel.updateUiState { it.copy(widgetStacks = mapOf(id to listOf(9))) }
        viewModel.updateUiState { it.copy(pendingWidgetId = -1) }

        assertEquals(setOf(OnboardingTask.ADD_WIDGET), onboarding.completedTasks)
        assertFalse(state.isGridEditMode)
    }

    @Test
    fun `when the widget picker closes with no widget, should remove the created list and keep the task pending`() {
        viewModel.startGridTask(OnboardingTask.ADD_WIDGET)

        closeScreen(
            open = { it.copy(showWidgetPicker = true) },
            close = { it.copy(showWidgetPicker = false) }
        )

        assertTrue(state.gridItems.isEmpty())
        assertTrue(gridRepository.getGridItems().isEmpty())
        assertTrue(onboarding.completedTasks.isEmpty())
        assertNull(onboarding.pendingTask)
        assertFalse(state.isGridEditMode)
        advancePeek()
        assertTrue(onboarding.isChecklistExpanded)
    }

    @Test
    fun `when the screen closes, should hide the pill and the list during the peek`() {
        viewModel.startGridTask(OnboardingTask.ADD_CLOCK)

        closeClockSheet()

        assertFalse(onboarding.blocksHome)
        assertFalse(onboarding.isChecklistExpanded)
    }

    @Test
    fun `when something else is open at the end of the peek, should not bring the list back over it`() {
        viewModel.startGridTask(OnboardingTask.ADD_CLOCK)
        closeClockSheet()
        viewModel.onTaskClick(OnboardingTask.CHOOSE_PALETTE)

        advancePeek()

        assertFalse(onboarding.isHomePeeking)
        assertEquals(AppearancePicker.PALETTE, onboarding.activePicker)
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
    fun `when the default spot is taken, should place the item in the largest free slot`() {
        val blocker = LauncherItemState(id = "blocker", type = LauncherItemType.QUICK_BUTTON, colSpan = 4, rowSpan = 3)
        viewModel.updateUiState { it.copy(gridItems = listOf(blocker), portraitGridItems = listOf(blocker)) }

        val id = viewModel.startGridTask(OnboardingTask.ADD_CLOCK)

        assertTrue(id != null && id != GridDefaults.CLOCK_ITEM_ID)
        assertEquals(2, state.gridItems.size)
    }

    @Test
    fun `when the grid has no room, should add nothing and keep the list open`() {
        viewModel.updateUiState { it.copy(gridLimits = GridLimits(1, 1)) }
        val full = LauncherItemState(id = "full", type = LauncherItemType.QUICK_BUTTON, colSpan = 1, rowSpan = 1)
        viewModel.updateUiState { it.copy(gridItems = listOf(full)) }

        assertNull(viewModel.startGridTask(OnboardingTask.ADD_CLOCK))

        assertEquals(1, state.gridItems.size)
        assertNull(onboarding.pendingTask)
        assertTrue(onboarding.isChecklistExpanded)
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
