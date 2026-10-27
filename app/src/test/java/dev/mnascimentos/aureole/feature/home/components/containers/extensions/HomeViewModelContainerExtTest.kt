package dev.mnascimentos.aureole.feature.home.components.containers.extensions

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelContainerExtTest {

    private lateinit var context: Context
    private lateinit var containerRepository: ContainerRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = RuntimeEnvironment.getApplication()
        val settingsRepository = SettingsRepository(context)
        containerRepository = ContainerRepository(context)
        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepository,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository),
                containerRepository = containerRepository,
                gridRepository = GridRepository(context)
            )
        )
        awaitInitialLoad()
    }

    @After
    fun tearDown() {
        // Let the init's background loads finish before Dispatchers.Main is swapped back.
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        Dispatchers.resetMain()
    }

    /**
     * HomeViewModel's init loads folders and containers on Dispatchers.IO and then replaces them in
     * the state. Wait for that before arranging a test, or the load can wipe the arranged state.
     */
    private fun awaitInitialLoad() {
        val folder = AppFolder(id = "__init_sentinel__", name = "")
        val container = ContainerModel(id = "__init_sentinel__")
        viewModel.updateUiState { it.copy(folders = listOf(folder), containers = mapOf(container.id to container)) }
        val deadline = System.currentTimeMillis() + INIT_LOAD_TIMEOUT_MS
        fun isLoading() = viewModel.uiState.value.let { it.folders == listOf(folder) || container.id in it.containers }
        while (isLoading() && System.currentTimeMillis() < deadline) {
            Thread.sleep(10)
        }
        viewModel.updateUiState { it.copy(folders = emptyList(), containers = emptyMap()) }
    }

    @Test
    fun `when opening the edit dialog for an unknown panel, should create it and show the dialog`() {
        viewModel.openEditContainerDialog(PANEL_ID)

        val state = awaitState { it.isEditContainerDialogVisible }
        assertEquals(PANEL_ID, state.editingContainerId)
        assertNotNull(state.containers[PANEL_ID])
        assertNotNull(runBlocking { containerRepository.getContainer(PANEL_ID) })
    }

    @Test
    fun `when closing the edit dialog, should hide it and clear the editing panel`() {
        viewModel.openEditContainerDialog(PANEL_ID)
        awaitState { it.isEditContainerDialogVisible }

        viewModel.closeEditContainerDialog()

        val state = viewModel.uiState.value
        assertFalse(state.isEditContainerDialogVisible)
        assertNull(state.editingContainerId)
    }

    @Test
    fun `when saving a container, should persist it and close the dialog`() {
        viewModel.openEditContainerDialog(PANEL_ID)
        val opened = awaitState { it.containers[PANEL_ID] != null }
        val renamed = requireNotNull(opened.containers[PANEL_ID]).copy(title = "Trabalho", hideText = true)

        viewModel.saveContainerModel(renamed)

        val state = awaitState { it.containers[PANEL_ID]?.title == "Trabalho" }
        assertFalse(state.isEditContainerDialogVisible)
        assertNull(state.editingContainerId)
        assertTrue(requireNotNull(runBlocking { containerRepository.getContainer(PANEL_ID) }).hideText)
    }

    @Test
    fun `when deleting a container, should remove it from state and storage`() {
        viewModel.openEditContainerDialog(PANEL_ID)
        awaitState { it.containers[PANEL_ID] != null }

        viewModel.deleteContainerInstance(PANEL_ID)

        val state = awaitState { it.containers[PANEL_ID] == null }
        assertFalse(state.isEditContainerDialogVisible)
        assertNull(runBlocking { containerRepository.getContainer(PANEL_ID) })
    }

    @Test
    fun `when removing an app from a container, should drop its item and package name`() {
        val panel = runBlocking { containerRepository.ensureContainerExists(PANEL_ID) }.copy(
            items = listOf(appItem("com.keep", 0), appItem("com.remove", 1)),
            appPackageNames = listOf("com.keep", "com.remove")
        )
        viewModel.updateUiState { it.copy(containers = mapOf(PANEL_ID to panel)) }

        viewModel.removeAppFromContainer(PANEL_ID, "com.remove")

        val state = awaitState { it.containers[PANEL_ID]?.appPackageNames == listOf("com.keep") }
        assertEquals(listOf("com.keep"), state.containers[PANEL_ID]?.items?.mapNotNull { it.packageName })
    }

    @Test
    fun `when removing an app from an unknown container, should leave state untouched`() {
        val before = viewModel.uiState.value

        viewModel.removeAppFromContainer("missing_panel", "com.any")

        assertEquals(before, viewModel.uiState.value)
    }

    @Test
    fun `when loading containers, should create the missing ones referenced by the grid`() {
        viewModel.updateUiState {
            it.copy(
                gridItems = listOf(
                    LauncherItemState(id = PANEL_ID, type = LauncherItemType.SHORTCUTS_CONTAINER),
                    LauncherItemState(id = "clock_1", type = LauncherItemType.CLOCK)
                )
            )
        }

        viewModel.loadContainers()

        val state = awaitState { it.containers.containsKey(PANEL_ID) }
        assertFalse(state.containers.containsKey("clock_1"))
        assertNotNull(runBlocking { containerRepository.getContainer(PANEL_ID) })
    }

    private fun appItem(packageName: String, order: Int) = ContainerItemEntity(
        id = "${PANEL_ID}_$packageName",
        panelId = PANEL_ID,
        itemType = ContainerItemType.APP,
        packageName = packageName,
        orderIndex = order
    )

    /** Repository calls hop to Dispatchers.IO, so the state settles on another thread. */
    private fun awaitState(condition: (MainUiState) -> Boolean): MainUiState {
        val deadline = System.currentTimeMillis() + TIMEOUT_MS
        while (!condition(viewModel.uiState.value)) {
            check(System.currentTimeMillis() < deadline) { "State did not settle: ${viewModel.uiState.value}" }
            Thread.sleep(POLL_MS)
        }
        return viewModel.uiState.value
    }

    private companion object {
        const val INIT_LOAD_TIMEOUT_MS = 1_000L
        const val PANEL_ID = "panel_test"
        const val TIMEOUT_MS = 5_000L
        const val POLL_MS = 10L
    }
}
