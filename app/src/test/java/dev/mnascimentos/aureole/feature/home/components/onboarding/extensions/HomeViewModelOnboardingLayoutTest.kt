package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.OnboardingProgress
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowLooper

/** How the onboarding shapes the home layout: empty on a fresh install, default when dismissed empty. */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelOnboardingLayoutTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var gridRepository: GridRepository
    private val viewModels = mutableListOf<HomeViewModel>()
    private val testDispatcher = StandardTestDispatcher()

    private val savedClock = LauncherItemState(id = "my_clock", type = LauncherItemType.CLOCK, colSpan = 4, rowSpan = 2)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences(SETTINGS_PREFS, Context.MODE_PRIVATE).edit().clear().commit()
        context.getSharedPreferences(GRID_PREFS, Context.MODE_PRIVATE).edit().clear().commit()
        settingsRepository = SettingsRepository(context)
        settingsRepository.lastSeenVersionCode = 0
        gridRepository = GridRepository(context)
    }

    @After
    fun tearDown() {
        viewModels.forEach { viewModel ->
            val scope = viewModel.viewModelScope.coroutineContext.job
            scope.cancel()
            val deadline = System.currentTimeMillis() + VIEW_MODEL_SHUTDOWN_MS
            while (!scope.isCompleted && System.currentTimeMillis() < deadline) {
                testDispatcher.scheduler.advanceUntilIdle()
                Thread.sleep(SHUTDOWN_POLL_MS)
            }
            runBlocking { scope.cancelAndJoin() }
        }
        context.getSharedPreferences(GRID_PREFS, Context.MODE_PRIVATE).edit().clear().commit()
        Dispatchers.resetMain()
    }

    private fun newViewModel(): HomeViewModel {
        val viewModel = HomeViewModel(
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
        ShadowLooper.idleMainLooper()
        viewModels += viewModel
        return viewModel
    }

    @Test
    fun `when it is a fresh install, should start with an empty home in both orientations`() {
        val viewModel = newViewModel().apply { syncOnboarding() }

        val state = viewModel.uiState.value
        assertTrue(state.gridItems.isEmpty())
        assertTrue(state.portraitGridItems.isEmpty())
        assertTrue(state.landscapeGridItems.isEmpty())
        assertTrue(gridRepository.getGridItems(isLandscape = true).isEmpty())
    }

    @Test
    fun `when an existing user updates, should keep the saved layout`() {
        settingsRepository.lastSeenVersionCode = 826
        gridRepository.saveGridItems(listOf(savedClock), isLandscape = false)

        val viewModel = newViewModel().apply { syncOnboarding() }

        assertEquals(listOf(savedClock.id), viewModel.uiState.value.portraitGridItems.map { it.id })
    }

    @Test
    fun `when a fresh install already has a saved layout, should keep it`() {
        gridRepository.saveGridItems(listOf(savedClock), isLandscape = false)

        val viewModel = newViewModel().apply { syncOnboarding() }

        assertEquals(listOf(savedClock.id), viewModel.uiState.value.portraitGridItems.map { it.id })
    }

    @Test
    fun `when the welcome is replayed, should keep the layout`() {
        settingsRepository.lastSeenVersionCode = 826
        settingsRepository.onboarding.write(OnboardingProgress(stage = OnboardingStage.WELCOME))

        val viewModel = newViewModel().apply { syncOnboarding() }

        assertEquals(OnboardingStage.WELCOME, viewModel.uiState.value.onboarding.stage)
        assertEquals(
            GridDefaults.getDefaultGridItems(isLandscape = false).map { it.id },
            viewModel.uiState.value.portraitGridItems.map { it.id }
        )
    }

    @Test
    fun `when dismissing the checklist with an empty home, should apply the default layout`() {
        val viewModel = newViewModel().apply { syncOnboarding() }
        viewModel.skipIntro()

        viewModel.dismissChecklist()

        val state = viewModel.uiState.value
        assertEquals(OnboardingStage.COMPLETED, state.onboarding.stage)
        // A fresh install has no favorites: the apps spot holds the all apps list.
        assertEquals(
            GridDefaults.getDefaultGridItems(isLandscape = false, hasFavorites = false),
            state.portraitGridItems
        )
        assertEquals(
            GridDefaults.getDefaultGridItems(isLandscape = true, hasFavorites = false),
            state.landscapeGridItems
        )
        assertEquals(
            GridDefaults.getDefaultGridItems(isLandscape = false, hasFavorites = false),
            gridRepository.getGridItems()
        )
    }

    @Test
    fun `when dismissing the checklist after building the home, should keep what was built`() {
        val viewModel = newViewModel().apply { syncOnboarding() }
        viewModel.skipIntro()
        gridRepository.saveGridItems(listOf(savedClock), isLandscape = false)
        viewModel.updateUiState { it.copy(portraitGridItems = listOf(savedClock), gridItems = listOf(savedClock)) }

        viewModel.dismissChecklist()

        val state = viewModel.uiState.value
        assertEquals(savedClock.id, state.portraitGridItems.first().id)
        assertEquals(LauncherItemType.ALL_APPS_LIST, state.portraitGridItems.last().safeType)
        assertEquals(
            GridDefaults.getDefaultGridItems(isLandscape = true, hasFavorites = false),
            state.landscapeGridItems
        )
    }

    private companion object {
        const val SETTINGS_PREFS = "aureole_settings_prefs"
        const val GRID_PREFS = "aureole_grid_prefs"
        const val VIEW_MODEL_SHUTDOWN_MS = 2_000L
        const val SHUTDOWN_POLL_MS = 10L
    }
}
