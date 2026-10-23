package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.Context
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.createNewFolder
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.handleFolderIntent
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.renameFolder
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.saveFolderApps
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.setAddAppToFolderDialogVisible
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.setRenameFolderDialogVisible
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
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
class HomeViewModelFolderExtensionsTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var context: Context
    private lateinit var folderRepository: FolderRepository
    private lateinit var containerRepository: ContainerRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = RuntimeEnvironment.getApplication()
        val settingsRepository = SettingsRepository(context)
        val appRepository = AppRepository(context)
        val widgetRepository = WidgetRepository(context)
        folderRepository = FolderRepository(context)
        val favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository)
        containerRepository = ContainerRepository(context)
        val gridRepository = GridRepository(context)

        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = appRepository,
                widgetRepository = widgetRepository,
                settingsRepository = settingsRepository,
                folderRepository = folderRepository,
                favoriteContainerRepository = favoriteContainerRepository,
                containerRepository = containerRepository,
                gridRepository = gridRepository,
            )
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when setting rename folder dialog visible, should update state`() {
        viewModel.setRenameFolderDialogVisible(true)
        assertTrue(viewModel.uiState.value.isRenameFolderDialogVisible)

        viewModel.setRenameFolderDialogVisible(false)
        assertFalse(viewModel.uiState.value.isRenameFolderDialogVisible)
    }

    @Test
    fun `when setting add app to folder dialog visible, should update state`() {
        viewModel.setAddAppToFolderDialogVisible(true)
        assertTrue(viewModel.uiState.value.isAddAppToFolderDialogVisible)

        viewModel.setAddAppToFolderDialogVisible(false)
        assertFalse(viewModel.uiState.value.isAddAppToFolderDialogVisible)
    }

    @Test
    fun `when creating new folder without target panel, should add folder to repository and open it`() = runTest {
        viewModel.createNewFolder(name = "New Global Folder", icon = "star")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isCreateFolderDialogVisible)
        assertNull(state.targetPanelIdForFolder)
        assertNotNull(state.openedFolderId)
        val created = state.folders.find { it.id == state.openedFolderId }
        assertNotNull(created)
        assertEquals("New Global Folder", created?.name)
        assertEquals("star", created?.icon)
    }

    @Test
    fun `when creating new folder with target panel, should add folder to panel`() = runTest {
        val panelId = "panel_1"
        viewModel.updateUiState { it.copy(targetPanelIdForFolder = panelId) }

        viewModel.createNewFolder(name = "Panel Folder", icon = "home")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isCreateFolderDialogVisible)
        assertNull(state.targetPanelIdForFolder)
        assertNotNull(state.openedFolderId)
    }

    @Test
    fun `when OpenCreateFolderDialog intent is received, should update dialog state and panelId`() = runTest {
        viewModel.handleFolderIntent(FolderViewIntent.OpenCreateFolderDialog(panelId = "panel_2"))

        val state = viewModel.uiState.value
        assertTrue(state.isCreateFolderDialogVisible)
        assertEquals("panel_2", state.targetPanelIdForFolder)
    }

    @Test
    fun `when SubmitFolderName intent is received, should create new folder`() = runTest {
        viewModel.handleFolderIntent(FolderViewIntent.SubmitFolderName("Submitted Folder", "info"))

        val state = viewModel.uiState.value
        assertFalse(state.isCreateFolderDialogVisible)
        assertNotNull(state.openedFolderId)
        assertEquals("Submitted Folder", state.activeFolder?.name)
    }

    @Test
    fun `when OpenFolder intent is received, should open folder and compute active folder grid mode`() = runTest {
        val folder = AppFolder(id = "folder_x", name = "Test Folder", displayAsGrid = true)
        folderRepository.addFolder(folder)
        viewModel.updateUiState { it.copy(folders = listOf(folder)) }

        viewModel.handleFolderIntent(FolderViewIntent.OpenFolder("folder_x", topYPx = 150f))

        val state = viewModel.uiState.value
        assertEquals("folder_x", state.openedFolderId)
        assertNotNull(state.activeFolder)
        assertEquals(150f, state.activeFolderTopYPx)
        assertTrue(state.activeFolder?.displayAsGrid == true)
    }

    @Test
    fun `when CloseFolder intent is received, should clear opened folder and dialog flags`() = runTest {
        viewModel.updateUiState {
            it.copy(
                openedFolderId = "f1",
                activeFolder = AppFolder(id = "f1", name = "F1"),
                isAddAppToFolderDialogVisible = true,
                isRenameFolderDialogVisible = true,
                isCreateFolderDialogVisible = true
            )
        }

        viewModel.handleFolderIntent(FolderViewIntent.CloseFolder)

        val state = viewModel.uiState.value
        assertNull(state.openedFolderId)
        assertNull(state.activeFolder)
        assertFalse(state.isAddAppToFolderDialogVisible)
        assertFalse(state.isRenameFolderDialogVisible)
        assertFalse(state.isCreateFolderDialogVisible)
    }

    @Test
    fun `when AddAppToFolder intent is received, should set isAddAppToFolderDialogVisible to true`() = runTest {
        viewModel.handleFolderIntent(FolderViewIntent.AddAppToFolder("f1"))

        assertTrue(viewModel.uiState.value.isAddAppToFolderDialogVisible)
    }

    @Test
    fun `when SaveFolderApps intent is received, should update folder apps and dismiss dialog`() = runTest {
        val folder = AppFolder(id = "f1", name = "F1", appPackageNames = emptyList())
        folderRepository.addFolder(folder)

        val selectedPackages = listOf("com.example.app1", "com.example.app2")
        viewModel.saveFolderApps(FolderViewIntent.SaveFolderApps("f1", selectedPackages))

        val updatedFolder = folderRepository.getFolders().find { it.id == "f1" }
        assertEquals(selectedPackages, updatedFolder?.appPackageNames)
        assertFalse(viewModel.uiState.value.isAddAppToFolderDialogVisible)
    }

    @Test
    fun `when RenameFolder intent is received, should update folder details and dismiss dialog`() = runTest {
        val folder = AppFolder(id = "f1", name = "Old Name", icon = "old_icon")
        folderRepository.addFolder(folder)

        viewModel.renameFolder(FolderViewIntent.RenameFolder("f1", "Renamed Folder", "new_icon"))

        val updatedFolder = folderRepository.getFolders().find { it.id == "f1" }
        assertEquals("Renamed Folder", updatedFolder?.name)
        assertEquals("new_icon", updatedFolder?.icon)
        assertFalse(viewModel.uiState.value.isRenameFolderDialogVisible)
    }

    @Test
    fun `when DeleteFolder intent is received, should remove folder and close it`() = runTest {
        val folder = AppFolder(id = "f_del", name = "ToDelete")
        folderRepository.addFolder(folder)
        viewModel.updateUiState { it.copy(folders = listOf(folder), openedFolderId = "f_del", activeFolder = folder) }

        viewModel.handleFolderIntent(FolderViewIntent.DeleteFolder("f_del"))

        val state = viewModel.uiState.value
        assertNull(state.openedFolderId)
        assertNull(folderRepository.getFolders().find { it.id == "f_del" })
    }

    @Test
    fun `when removeAppFromFolder is called, should filter out package name and refresh folder`() = runTest {
        val folder = AppFolder(id = "f_rem", name = "RemoveTest", appPackageNames = listOf("app1", "app2", "app3"))
        folderRepository.addFolder(folder)
        viewModel.updateUiState { it.copy(folders = listOf(folder)) }

        val newAppPackages = folder.appPackageNames.filterNot { it == "app2" }
        folderRepository.updateFolderApps("f_rem", newAppPackages)

        val updatedFolder = folderRepository.getFolders().find { it.id == "f_rem" }
        assertEquals(listOf("app1", "app3"), updatedFolder?.appPackageNames)
    }
}
