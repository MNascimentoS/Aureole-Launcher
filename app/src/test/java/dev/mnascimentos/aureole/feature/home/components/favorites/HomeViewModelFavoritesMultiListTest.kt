package dev.mnascimentos.aureole.feature.home.components.favorites

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.AppFolder
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
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.addFavoritesList
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.dismissFavoritePicker
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.updateContainerFavorites
import dev.mnascimentos.aureole.feature.home.extensions.onUndo
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.extensions.removeGridItemWithUndo
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import dev.mnascimentos.aureole.feature.home.model.favoritesOf
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
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/** Several favorites lists on the home: independent contents, isolated removal, undo and the legacy list. */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelFavoritesMultiListTest {

    private lateinit var context: Context
    private lateinit var gridRepository: GridRepository
    private lateinit var favoriteRepository: FavoriteContainerRepository
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: HomeViewModel
    private val touchedIds = mutableSetOf(LEGACY_ID, OTHER_ID)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = RuntimeEnvironment.getApplication()
        context.getSharedPreferences(GRID_PREFS, Context.MODE_PRIVATE).edit().clear().commit()
        settingsRepository = SettingsRepository(context)
        settingsRepository.isLeftHandedMode = false
        settingsRepository.favoriteAppPackages = emptyList()
        gridRepository = GridRepository(context)
        favoriteRepository = FavoriteContainerRepository(context, settingsRepository)
        runBlocking { touchedIds.forEach { favoriteRepository.deleteContainer(it) } }
        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepository,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = favoriteRepository,
                containerRepository = ContainerRepository(context),
                gridRepository = gridRepository
            )
        )
        awaitInitialLoad()
    }

    @After
    fun tearDown() {
        // Room is shared by every test class: stop pending work, then delete what the tests wrote.
        runBlocking {
            viewModel.viewModelScope.coroutineContext.job.cancelAndJoin()
            touchedIds.forEach { favoriteRepository.deleteContainer(it) }
        }
        settingsRepository.favoriteAppPackages = emptyList()
        context.getSharedPreferences(GRID_PREFS, Context.MODE_PRIVATE).edit().clear().commit()
        Dispatchers.resetMain()
    }

    private fun awaitInitialLoad() {
        val sentinel = AppFolder(id = "__init_sentinel__", name = "")
        viewModel.updateUiState { it.copy(folders = listOf(sentinel)) }
        awaitUntil { viewModel.uiState.value.folders != listOf(sentinel) }
    }

    private fun awaitUntil(condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + TIMEOUT_MS
        while (!condition() && System.currentTimeMillis() < deadline) Thread.sleep(POLL_MS)
        assertTrue("condition not met in time", condition())
    }

    private fun arrangeHome(vararg items: LauncherItemState) {
        val list = items.toList()
        viewModel.updateUiState {
            it.copy(
                gridItems = list,
                portraitGridItems = list,
                landscapeGridItems = emptyList(),
                isLandscape = false,
                isGridEditMode = false,
                gridLimits = GridLimits(GridEngineUtils.PORTRAIT_MAX_COLS, GridEngineUtils.PORTRAIT_MAX_ROWS),
                favoriteAppPackages = settingsRepository.favoriteAppPackages,
                containerFavorites = emptyMap()
            )
        }
    }

    private fun list(id: String, col: Int, row: Int) =
        LauncherItemState(id = id, type = LauncherItemType.APPS_LIST, col = col, row = row, colSpan = 2, rowSpan = 2)

    private fun favorites(id: String) = viewModel.uiState.value.favoritesOf(id)

    private fun savedFavorites(id: String) = runBlocking { favoriteRepository.getAllContainerFavorites()[id] }

    @Test
    fun `adding favorites twice creates two lists with independent favorites`() {
        arrangeHome()
        viewModel.addFavoritesList()
        val first = viewModel.uiState.value.customizeFavoritesListId!!
        viewModel.updateContainerFavorites(first, listOf("a"))
        awaitUntil { favorites(first) == listOf("a") }
        viewModel.dismissFavoritePicker()

        viewModel.addFavoritesList()
        val second = viewModel.uiState.value.customizeFavoritesListId!!
        touchedIds += listOf(first, second)

        assertNotEquals(first, second)
        assertEquals(emptyList<String>(), favorites(second))
        viewModel.updateContainerFavorites(second, listOf("b", "c"))
        awaitUntil { favorites(second) == listOf("b", "c") }
        assertEquals(listOf("a"), favorites(first))
        assertEquals(2, viewModel.uiState.value.gridItems.count { it.safeType == LauncherItemType.APPS_LIST })
    }

    @Test
    fun `a new list does not take over the global favorites of the legacy list`() {
        settingsRepository.favoriteAppPackages = listOf("old")
        arrangeHome(list(LEGACY_ID, col = 0, row = 0))

        viewModel.addFavoritesList()
        val created = viewModel.uiState.value.customizeFavoritesListId!!
        touchedIds += created
        viewModel.updateContainerFavorites(created, listOf("new"))
        awaitUntil { favorites(created) == listOf("new") }

        assertEquals(listOf("old"), favorites(LEGACY_ID))
        assertEquals(listOf("old"), settingsRepository.favoriteAppPackages)
        assertEquals(listOf("old"), viewModel.uiState.value.favoriteAppPackages)
    }

    @Test
    fun `editing the legacy list still updates the global favorites`() {
        arrangeHome(list(LEGACY_ID, col = 0, row = 0))

        viewModel.updateContainerFavorites(LEGACY_ID, listOf("x"))
        awaitUntil { settingsRepository.favoriteAppPackages == listOf("x") }

        assertEquals(listOf("x"), favorites(LEGACY_ID))
        assertEquals(listOf("x"), viewModel.uiState.value.favoriteAppPackages)
    }

    @Test
    fun `removing one list deletes only its favorites and the other keeps them`() {
        arrangeHome(list(LEGACY_ID, col = 0, row = 0), list(OTHER_ID, col = 2, row = 0))
        viewModel.updateContainerFavorites(LEGACY_ID, listOf("a"))
        viewModel.updateContainerFavorites(OTHER_ID, listOf("b"))
        awaitUntil { savedFavorites(OTHER_ID) == listOf("b") && savedFavorites(LEGACY_ID) == listOf("a") }

        viewModel.removeGridItemWithUndo(OTHER_ID)
        awaitUntil { savedFavorites(OTHER_ID) == null }

        assertEquals(listOf(LEGACY_ID), viewModel.uiState.value.gridItems.map { it.id })
        assertEquals(listOf("a"), savedFavorites(LEGACY_ID))
        assertEquals(listOf("a"), favorites(LEGACY_ID))
        assertEquals(listOf("a"), settingsRepository.favoriteAppPackages)
    }

    @Test
    fun `undoing the removal of a list restores its favorites`() {
        arrangeHome(list(LEGACY_ID, col = 0, row = 0), list(OTHER_ID, col = 2, row = 0))
        viewModel.updateContainerFavorites(OTHER_ID, listOf("b", "c"))
        awaitUntil { savedFavorites(OTHER_ID) == listOf("b", "c") }
        viewModel.removeGridItemWithUndo(OTHER_ID)
        awaitUntil { savedFavorites(OTHER_ID) == null }

        viewModel.onUndo()
        awaitUntil { savedFavorites(OTHER_ID) == listOf("b", "c") }

        assertEquals(listOf("b", "c"), favorites(OTHER_ID))
        assertTrue(viewModel.uiState.value.gridItems.any { it.id == OTHER_ID })
        assertNull(viewModel.uiState.value.undoSnackbar)
    }

    @Test
    fun `removing and undoing the legacy list gives its global favorites back`() {
        settingsRepository.favoriteAppPackages = listOf("old")
        arrangeHome(list(LEGACY_ID, col = 0, row = 0))

        viewModel.removeGridItemWithUndo(LEGACY_ID)
        awaitUntil { settingsRepository.favoriteAppPackages.isEmpty() }
        assertEquals(emptyList<String>(), viewModel.uiState.value.favoriteAppPackages)

        viewModel.onUndo()
        awaitUntil { settingsRepository.favoriteAppPackages == listOf("old") }

        assertEquals(listOf("old"), favorites(LEGACY_ID))
    }

    private companion object {
        const val GRID_PREFS = "aureole_grid_prefs"
        const val LEGACY_ID = GridDefaults.APPS_LIST_ITEM_ID
        const val OTHER_ID = "multi_list_other"
        const val TIMEOUT_MS = 3_000L
        const val POLL_MS = 10L
    }
}
