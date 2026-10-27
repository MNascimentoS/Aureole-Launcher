package dev.mnascimentos.aureole.feature.home.extensions

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
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
class HomeViewModelUiActionsTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = RuntimeEnvironment.getApplication()
        settingsRepository = SettingsRepository(context)
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
        awaitInitialLoad()
    }

    /**
     * HomeViewModel's init loads the settings on Dispatchers.IO and then writes them to the state.
     * Wait for that first, or it can overwrite a setting a test has just changed.
     */
    private fun awaitInitialLoad() {
        val sentinel = AppFolder(id = "__init_sentinel__", name = "")
        viewModel.updateUiState { it.copy(folders = listOf(sentinel)) }
        val deadline = System.currentTimeMillis() + INIT_LOAD_TIMEOUT_MS
        while (viewModel.uiState.value.folders == listOf(sentinel) && System.currentTimeMillis() < deadline) {
            Thread.sleep(POLL_MS)
        }
    }

    @After
    fun tearDown() {
        // Let the init's background loads finish before Dispatchers.Main is swapped back.
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        Dispatchers.resetMain()
    }

    @Test
    fun `when setting the container position, should save it and update state`() {
        viewModel.setContainerPosition("Space Between")

        assertEquals("Space Between", viewModel.uiState.value.containerPosition)
        assertEquals("Space Between", settingsRepository.containerPosition)
    }

    @Test
    fun `when toggling home button opens all apps, should flip state and save it`() {
        val initial = viewModel.uiState.value.homeButtonOpensAllApps

        viewModel.toggleHomeOpensAllApps()

        assertEquals(!initial, viewModel.uiState.value.homeButtonOpensAllApps)
        assertEquals(!initial, settingsRepository.homeButtonOpensAllApps)
    }

    @Test
    fun `when showing and hiding dialogs, should update their flags`() {
        viewModel.setShowSettingsDialog(true)
        viewModel.setShowCustomizeBottomSheet(true)
        assertTrue(viewModel.uiState.value.showSettingsDialog)
        assertTrue(viewModel.uiState.value.showCustomizeBottomSheet)

        viewModel.setShowSettingsDialog(false)
        viewModel.setShowCustomizeBottomSheet(false)
        assertFalse(viewModel.uiState.value.showSettingsDialog)
        assertFalse(viewModel.uiState.value.showCustomizeBottomSheet)
    }

    @Test
    fun `when opening the drawer from the home button, should clear the search and close folders`() {
        viewModel.updateUiState { it.copy(searchQuery = "mail", openedFolderId = "folder_1") }

        viewModel.setAllAppsDrawerOpen(open = true, fromHomeButton = true)

        val state = viewModel.uiState.value
        assertTrue(state.isAllAppsDrawerOpen)
        assertTrue(state.isAllAppsOpenedFromBottom)
        assertEquals("", state.searchQuery)
        assertNull(state.openedFolderId)
    }

    @Test
    fun `when closing the drawer, should keep where it was opened from`() {
        viewModel.setAllAppsDrawerOpen(open = true, fromHomeButton = true)

        viewModel.setAllAppsDrawerOpen(open = false)

        val state = viewModel.uiState.value
        assertFalse(state.isAllAppsDrawerOpen)
        assertTrue(state.isAllAppsOpenedFromBottom)
    }
}

private const val INIT_LOAD_TIMEOUT_MS = 1_000L
private const val POLL_MS = 10L
