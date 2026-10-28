package dev.mnascimentos.aureole.feature.home.components.favorites

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
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.addFavoritesList
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.dismissFavoritePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
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

/** Customize › Add favorites (FUE-23). */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelFavoritesListTest {

    private lateinit var context: Context
    private lateinit var gridRepository: GridRepository
    private lateinit var viewModel: HomeViewModel
    private lateinit var settingsRepository: SettingsRepository

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

    @Test
    fun `when add favorites runs on an empty home, should place a list and open its picker`() {
        viewModel.addFavoritesList()

        val list = state.gridItems.single()
        assertEquals(LauncherItemType.APPS_LIST, list.safeType)
        assertTrue(state.showFavoritePickerDialog)
        assertEquals(list.id, state.activeFavoriteContainerId)
        assertEquals(list.id, state.customizeFavoritesListId)
    }

    @Test
    fun `when the picker closes without favorites, should remove the placed list`() {
        viewModel.addFavoritesList()

        viewModel.dismissFavoritePicker()

        assertTrue(state.gridItems.isEmpty())
        assertFalse(state.showFavoritePickerDialog)
        assertNull(state.customizeFavoritesListId)
    }

    @Test
    fun `when the picker closes with favorites, should keep the placed list`() {
        viewModel.addFavoritesList()
        val id = state.gridItems.single().id
        viewModel.updateUiState { it.copy(containerFavorites = mapOf(id to listOf("a"))) }

        viewModel.dismissFavoritePicker()

        assertEquals(listOf(id), state.gridItems.map { it.id })
    }

    @Test
    fun `when a favorites list is already on the home, should edit it and never remove it`() {
        val existing = LauncherItemState(id = "fav", type = LauncherItemType.APPS_LIST, colSpan = 5, rowSpan = 7)
        viewModel.updateUiState { it.copy(gridItems = listOf(existing), portraitGridItems = listOf(existing)) }

        viewModel.addFavoritesList()
        assertEquals("fav", state.activeFavoriteContainerId)
        assertNull(state.customizeFavoritesListId)

        viewModel.dismissFavoritePicker()

        assertEquals(listOf(existing.id), state.gridItems.map { it.id })
    }

    private companion object {
        const val GRID_PREFS = "aureole_grid_prefs"
        const val INIT_LOAD_TIMEOUT_MS = 1_000L
        const val POLL_MS = 10L
    }
}
