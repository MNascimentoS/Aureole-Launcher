package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
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

/** The onboarding never ends with a home that has no way to the apps (FUE-22). */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelOnboardingAppsTest {

    private lateinit var context: Context
    private lateinit var gridRepository: GridRepository
    private lateinit var viewModel: HomeViewModel
    private lateinit var settingsRepository: SettingsRepository

    private val clock = LauncherItemState(id = "clock", type = LauncherItemType.CLOCK, colSpan = 20, rowSpan = 6)

    private val state: MainUiState
        get() = viewModel.uiState.value

    private val onboarding: OnboardingUiState
        get() = state.onboarding

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences(GRID_PREFS, Context.MODE_PRIVATE).edit().clear().commit()
        settingsRepository = SettingsRepository(context)
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

    private fun allApps(items: List<LauncherItemState>) = items.filter { it.safeType == LauncherItemType.ALL_APPS_LIST }

    private fun place(vararg items: LauncherItemState, landscape: List<LauncherItemState> = emptyList()) {
        viewModel.updateUiState {
            it.copy(gridItems = items.toList(), portraitGridItems = items.toList(), landscapeGridItems = landscape)
        }
    }

    @Test
    fun `when the checklist completes without the apps step, should add all apps at the default spots`() {
        place(clock)

        viewModel.dismissChecklist()

        val portraitApps = allApps(state.portraitGridItems).single()
        val defaultSpot = GridDefaults.getDefaultItem(LauncherItemType.ALL_APPS_LIST, false, false)!!
        assertEquals(listOf(defaultSpot.col, defaultSpot.row), listOf(portraitApps.col, portraitApps.row))
        assertEquals(1, allApps(gridRepository.getGridItems(isLandscape = false)).size)
    }

    @Test
    fun `when an empty favorites list is on the home, should swap it for all apps in place`() {
        val emptyList = LauncherItemState(
            id = "fav",
            type = LauncherItemType.APPS_LIST,
            col = 2,
            row = 3,
            colSpan = 5,
            rowSpan = 7
        )
        place(emptyList)

        viewModel.ensureAppsComponent()

        val swapped = state.portraitGridItems.single()
        assertEquals(LauncherItemType.ALL_APPS_LIST, swapped.safeType)
        assertEquals(listOf(2, 3, 5, 7), listOf(swapped.col, swapped.row, swapped.colSpan, swapped.rowSpan))
    }

    @Test
    fun `when the favorites list has favorites, should change nothing in that orientation`() {
        val list = LauncherItemState(id = "fav", type = LauncherItemType.APPS_LIST, colSpan = 5, rowSpan = 7)
        place(list)
        viewModel.updateUiState { it.copy(containerFavorites = mapOf("fav" to listOf("a"))) }

        viewModel.ensureAppsComponent()

        assertEquals(listOf(list), state.portraitGridItems)
    }

    @Test
    fun `when an all apps list is already there, should change nothing in that orientation`() {
        val list = LauncherItemState(id = "all", type = LauncherItemType.ALL_APPS_LIST, colSpan = 5, rowSpan = 7)
        place(list)

        viewModel.ensureAppsComponent()

        assertEquals(listOf(list), state.portraitGridItems)
    }

    @Test
    fun `when the default spot is taken, should use the largest free slot`() {
        val blocker = LauncherItemState(
            id = "blocker",
            type = LauncherItemType.QUICK_BUTTON,
            row = 12,
            colSpan = 20,
            rowSpan = 4
        )
        place(blocker)

        viewModel.ensureAppsComponent()

        val apps = allApps(state.portraitGridItems).single()
        assertFalse(GridEngineUtils.checkCollisionWithOthers(apps, state.portraitGridItems))
        assertTrue(apps.row != 12)
    }

    @Test
    fun `when the grid is full, should add nothing and show no warning`() {
        val full = LauncherItemState(id = "full", type = LauncherItemType.QUICK_BUTTON, colSpan = 20, rowSpan = 30)
        place(full)

        viewModel.ensureAppsComponent()

        assertEquals(listOf(full), state.portraitGridItems)
        assertNull(state.gridErrorMessage)
    }

    @Test
    fun `when left-handed, should add all apps at the mirrored spot in both orientations`() {
        settingsRepository.isLeftHandedMode = true
        place(clock, landscape = listOf(clock))

        viewModel.ensureAppsComponent()

        val portrait = allApps(state.portraitGridItems).single()
        val landscape = allApps(state.landscapeGridItems).single()
        assertEquals(4, portrait.col)
        assertEquals(listOf(16, 7), listOf(landscape.col, landscape.row))
    }

    @Test
    fun `when the onboarding was already completed, should not touch the home on updates`() {
        viewModel.updateUiState { it.copy(onboarding = it.onboarding.copy(stage = OnboardingStage.COMPLETED)) }
        place(clock)

        viewModel.updateUiState { it.copy(searchQuery = "a") }

        assertEquals(listOf(clock), state.portraitGridItems)
    }

    private companion object {
        const val GRID_PREFS = "aureole_grid_prefs"
        const val INIT_LOAD_TIMEOUT_MS = 1_000L
        const val POLL_MS = 10L
    }
}
