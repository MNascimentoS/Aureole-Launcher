package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ClockConfigRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.extensions.updateActiveGridItemsInState
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
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/**
 * The checklist places clock, widget and favorites in that order: each one takes its default spot
 * or the largest free slot, never overlapping another item and never leaving the grid.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelOnboardingPlacementTest {

    private lateinit var context: Context
    private lateinit var viewModel: HomeViewModel

    private val state: MainUiState
        get() = viewModel.uiState.value

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences(GRID_PREFS, Context.MODE_PRIVATE).edit().clear().commit()
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
            )
        )
        awaitInitialLoad()
        useOrientation(isLandscape = false)
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

    private fun useOrientation(isLandscape: Boolean) {
        viewModel.updateUiState {
            it.copy(
                gridItems = emptyList(),
                portraitGridItems = emptyList(),
                landscapeGridItems = emptyList(),
                isLandscape = isLandscape,
                gridLimits = limitsFor(isLandscape),
                favoriteAppPackages = emptyList(),
                containerFavorites = emptyMap(),
                widgetStacks = emptyMap()
            )
        }
    }

    private fun limitsFor(isLandscape: Boolean): GridLimits = if (isLandscape) {
        GridLimits(GridEngineUtils.LANDSCAPE_MAX_COLS, GridEngineUtils.LANDSCAPE_MAX_ROWS)
    } else {
        GridLimits(GridEngineUtils.PORTRAIT_MAX_COLS, GridEngineUtils.PORTRAIT_MAX_ROWS)
    }

    @Test
    fun `when portrait, should place clock, widget and favorites at their default spots`() {
        assertDefaultSpotsInChecklistOrder(isLandscape = false)
    }

    @Test
    fun `when landscape, should place clock, widget and favorites at their default spots`() {
        assertDefaultSpotsInChecklistOrder(isLandscape = true)
    }

    @Test
    fun `when portrait and the widget default spot is taken, should use the largest free slot`() {
        assertWidgetFallsBackWhenDefaultIsTaken(isLandscape = false)
    }

    @Test
    fun `when landscape and the widget default spot is taken, should use the largest free slot`() {
        assertWidgetFallsBackWhenDefaultIsTaken(isLandscape = true)
    }

    private fun assertDefaultSpotsInChecklistOrder(isLandscape: Boolean) {
        useOrientation(isLandscape)

        listOf(LauncherItemType.CLOCK, LauncherItemType.WIDGET_LIST, LauncherItemType.APPS_LIST).forEach { type ->
            val id = viewModel.ensureGridItem(type)
            assertNotNull(id)
            val item = state.gridItems.first { it.id == id }
            val default = GridDefaults.getDefaultItem(type, isLandscape, isLeftHanded = false)
            assertEquals(default?.rect(), item.rect())
        }

        assertEquals(3, state.gridItems.size)
        assertValidLayout()
    }

    private fun assertWidgetFallsBackWhenDefaultIsTaken(isLandscape: Boolean) {
        useOrientation(isLandscape)
        assertNotNull(viewModel.ensureGridItem(LauncherItemType.CLOCK))
        val widgetDefault = GridDefaults.getDefaultItem(LauncherItemType.WIDGET_LIST, isLandscape, isLeftHanded = false)
        assertNotNull(widgetDefault)
        val blocker = widgetDefault!!.copy(id = BLOCKER_ID, type = LauncherItemType.SHORTCUTS_CONTAINER)
        viewModel.updateUiState { updateActiveGridItemsInState(it, it.gridItems + blocker) }

        val widgetId = viewModel.ensureGridItem(LauncherItemType.WIDGET_LIST)

        assertNotNull(widgetId)
        val widget = state.gridItems.first { it.id == widgetId }
        assertNotEquals(widgetDefault.rect(), widget.rect())
        assertValidLayout()
    }

    /** Every item inside the grid limits and no two items overlapping. */
    private fun assertValidLayout() {
        val items = state.gridItems
        items.forEach { item ->
            assertTrue(
                "${item.type} out of bounds: ${item.rect()}",
                GridEngineUtils.isWithinBounds(item.col, item.row, item.colSpan, item.rowSpan, state.gridLimits)
            )
            assertFalse(
                "${item.type} overlaps another item: ${item.rect()}",
                GridEngineUtils.checkCollisionWithOthers(item, items)
            )
        }
    }

    private fun LauncherItemState.rect() = listOf(col, row, colSpan, rowSpan)

    private companion object {
        const val GRID_PREFS = "aureole_grid_prefs"
        const val BLOCKER_ID = "blocker_item"
        const val INIT_LOAD_TIMEOUT_MS = 2_000L
        const val POLL_MS = 10L
    }
}
