package dev.mnascimentos.aureole.feature.home

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.handleFolderIntent
import dev.mnascimentos.aureole.feature.home.components.containers.folder.extensions.removeAppFromFolder
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.toggleFavorite
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.onUpdateQuickButtonShortcuts
import dev.mnascimentos.aureole.feature.home.extensions.onUndo
import dev.mnascimentos.aureole.feature.home.extensions.onUndoExpired
import dev.mnascimentos.aureole.feature.home.extensions.showUndo
import dev.mnascimentos.aureole.feature.home.grid.extensions.removeGridItemWithUndo
import dev.mnascimentos.aureole.feature.home.grid.extensions.restoreGridItem
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/** Each action that offers "Undo" for 4 s: state before, the action, the undo, state back. */
@RunWith(RobolectricTestRunner::class)
class HomeViewModelUndoTest {

    private lateinit var context: Context
    private lateinit var folderRepository: FolderRepository
    private lateinit var favoriteRepository: FavoriteContainerRepository
    private lateinit var gridRepository: GridRepository
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = RuntimeEnvironment.getApplication()
        val settingsRepository = SettingsRepository(context)
        folderRepository = FolderRepository(context)
        favoriteRepository = FavoriteContainerRepository(context, settingsRepository)
        gridRepository = GridRepository(context)
        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepository,
                folderRepository = folderRepository,
                favoriteContainerRepository = favoriteRepository,
                containerRepository = ContainerRepository(context),
                gridRepository = gridRepository,
            )
        )
    }

    @After
    fun tearDown() {
        // Room is shared by every test class: stop pending work, then delete what the tests wrote.
        runBlocking {
            viewModel.viewModelScope.coroutineContext.job.cancelAndJoin()
            folderRepository.deleteFolder(FOLDER_ID)
            favoriteRepository.deleteContainer(CONTAINER_ID)
        }
        gridRepository.resetToDefault()
        Dispatchers.resetMain()
    }

    @Test
    fun `removing an app from a folder offers undo that puts it back at the same position`() = runBlocking {
        val folder = AppFolder(id = FOLDER_ID, name = "F", appPackageNames = listOf("a", "b", "c"))
        folderRepository.addFolder(folder)
        viewModel.updateUiState { it.copy(folders = listOf(folder), apps = listOf(app("b", "Beta"))) }

        viewModel.removeAppFromFolder(FOLDER_ID, "b")
        awaitUntil { viewModel.uiState.value.undoSnackbar != null }

        assertEquals(listOf("a", "c"), folderPackages())
        assertEquals(
            TextResource.fromResId(R.string.home_undo_removed_from_folder, "Beta"),
            viewModel.uiState.value.undoSnackbar?.message
        )

        viewModel.onUndo()
        awaitUntil { folderPackages().size == 3 }

        assertEquals(listOf("a", "b", "c"), folderPackages())
        assertEquals(FOLDER_ID, viewModel.uiState.value.openedFolderId)
        assertNull(viewModel.uiState.value.undoSnackbar)
    }

    @Test
    fun `removing an app that is not in the folder offers no undo`() = runBlocking {
        val folder = AppFolder(id = FOLDER_ID, name = "F", appPackageNames = listOf("a"))
        folderRepository.addFolder(folder)
        viewModel.updateUiState { it.copy(folders = listOf(folder)) }

        viewModel.removeAppFromFolder(FOLDER_ID, "zzz")
        awaitUntil { viewModel.uiState.value.openedFolderId == FOLDER_ID }

        assertNull(viewModel.uiState.value.undoSnackbar)
    }

    @Test
    fun `adding a favorite offers undo that restores the previous list`() = runBlocking {
        viewModel.updateUiState {
            it.copy(containerFavorites = mapOf(CONTAINER_ID to listOf("a")), apps = listOf(app("b", "Beta")))
        }

        viewModel.toggleFavorite("b", CONTAINER_ID)
        awaitUntil { favorites() == listOf("a", "b") }

        assertEquals(listOf("a", "b"), favorites())
        assertEquals(
            TextResource.fromResId(R.string.home_undo_favorite_added, "Beta"),
            viewModel.uiState.value.undoSnackbar?.message
        )

        viewModel.onUndo()
        awaitUntil { favorites() == listOf("a") }

        assertEquals(listOf("a"), favorites())
        assertNull(viewModel.uiState.value.undoSnackbar)
    }

    @Test
    fun `removing a favorite offers undo with its own message that brings it back`() = runBlocking {
        viewModel.updateUiState {
            it.copy(containerFavorites = mapOf(CONTAINER_ID to listOf("a", "b")), apps = listOf(app("b", "Beta")))
        }

        viewModel.toggleFavorite("b", CONTAINER_ID)
        awaitUntil { favorites() == listOf("a") }

        assertEquals(listOf("a"), favorites())
        assertEquals(
            TextResource.fromResId(R.string.home_undo_favorite_removed, "Beta"),
            viewModel.uiState.value.undoSnackbar?.message
        )

        viewModel.onUndo()
        awaitUntil { favorites() == listOf("a", "b") }

        assertEquals(listOf("a", "b"), favorites())
    }

    @Test
    fun `changing a shortcut offers undo that restores the previous shortcuts`() {
        val before = QuickButtonShortcuts()
        val model = QuickButtonModel(id = BUTTON_ID, shortcuts = before)
        val item = LauncherItemState(id = BUTTON_ID, quickButtonModel = model)
        viewModel.updateUiState {
            it.copy(
                activeQuickButtonId = BUTTON_ID,
                activeQuickButtonModel = model,
                gridItems = listOf(item),
                portraitGridItems = listOf(item),
                landscapeGridItems = listOf(item),
            )
        }
        val after = before.copy(click = ShortcutAction.Settings)

        viewModel.onUpdateQuickButtonShortcuts(after)

        assertEquals(after, viewModel.uiState.value.activeQuickButtonModel?.shortcuts)
        assertEquals(
            TextResource.fromResId(R.string.home_undo_shortcut_changed, ShortcutAction.Settings.displayName),
            viewModel.uiState.value.undoSnackbar?.message
        )

        viewModel.onUndo()

        assertEquals(before, viewModel.uiState.value.activeQuickButtonModel?.shortcuts)
        assertEquals(before, viewModel.uiState.value.gridItems.first().quickButtonModel?.shortcuts)
    }

    @Test
    fun `an unchanged shortcut offers no undo`() {
        val model = QuickButtonModel(id = BUTTON_ID)
        viewModel.updateUiState { it.copy(activeQuickButtonId = BUTTON_ID, activeQuickButtonModel = model) }

        viewModel.onUpdateQuickButtonShortcuts(model.shortcuts)

        assertNull(viewModel.uiState.value.undoSnackbar)
    }

    @Test
    fun `removing a folder offers undo that puts the folder back with its apps`() = runBlocking {
        val folder = AppFolder(id = FOLDER_ID, name = "F", appPackageNames = listOf("a", "b"))
        folderRepository.addFolder(folder)
        viewModel.updateUiState { it.copy(folders = listOf(folder)) }

        viewModel.handleFolderIntent(FolderViewIntent.DeleteFolder(FOLDER_ID))

        assertTrue(viewModel.uiState.value.folders.none { it.id == FOLDER_ID })
        assertEquals(
            TextResource.fromResId(R.string.home_undo_folder_removed),
            viewModel.uiState.value.undoSnackbar?.message
        )

        viewModel.onUndo()
        awaitUntil { viewModel.uiState.value.folders.any { it.id == FOLDER_ID } }

        assertEquals(listOf("a", "b"), folderPackages())
        assertNull(viewModel.uiState.value.undoSnackbar)
    }

    @Test
    fun `removing a folder that does not exist offers no undo`() = runBlocking {
        viewModel.handleFolderIntent(FolderViewIntent.DeleteFolder("missing_folder"))

        assertNull(viewModel.uiState.value.undoSnackbar)
    }

    @Test
    fun `undoing a folder removal twice does not duplicate the folder`() = runBlocking {
        val folder = AppFolder(id = FOLDER_ID, name = "F")
        folderRepository.addFolder(folder)
        viewModel.updateUiState { it.copy(folders = listOf(folder)) }
        viewModel.handleFolderIntent(FolderViewIntent.DeleteFolder(FOLDER_ID))
        folderRepository.addFolder(folder)

        viewModel.onUndo()
        awaitUntil { viewModel.uiState.value.folders.any { it.id == FOLDER_ID } }

        assertEquals(1, folderRepository.getFolders().count { it.id == FOLDER_ID })
    }

    @Test
    fun `removing a grid item offers undo that puts it back in the same cell`() {
        val item = LauncherItemState(id = GRID_ITEM_ID, type = LauncherItemType.CLOCK, col = 1, row = 2)
        viewModel.updateUiState {
            it.copy(gridItems = listOf(item), portraitGridItems = listOf(item), landscapeGridItems = listOf(item))
        }

        viewModel.removeGridItemWithUndo(GRID_ITEM_ID)

        assertTrue(viewModel.uiState.value.gridItems.none { it.id == GRID_ITEM_ID })
        assertEquals(
            TextResource.fromResId(R.string.home_undo_component_removed),
            viewModel.uiState.value.undoSnackbar?.message
        )

        viewModel.onUndo()

        assertEquals(item, viewModel.uiState.value.gridItems.single { it.id == GRID_ITEM_ID })
        assertNull(viewModel.uiState.value.undoSnackbar)
    }

    @Test
    fun `removing a grid item that does not exist offers no undo`() {
        viewModel.removeGridItemWithUndo("missing_item")

        assertNull(viewModel.uiState.value.undoSnackbar)
    }

    @Test
    fun `restoring a grid item that is already on the grid keeps a single copy`() {
        val item = LauncherItemState(id = GRID_ITEM_ID, type = LauncherItemType.CLOCK)
        viewModel.updateUiState { it.copy(gridItems = listOf(item), portraitGridItems = listOf(item)) }

        viewModel.restoreGridItem(item)

        assertEquals(1, viewModel.uiState.value.gridItems.count { it.id == GRID_ITEM_ID })
    }

    @Test
    fun `restoring a grid item in edit mode does not write to the repository`() {
        val item = LauncherItemState(id = GRID_ITEM_ID, type = LauncherItemType.CLOCK)
        viewModel.updateUiState { it.copy(isGridEditMode = true) }

        viewModel.restoreGridItem(item)

        assertEquals(1, viewModel.uiState.value.gridItems.count { it.id == GRID_ITEM_ID })
    }

    @Test
    fun `an expired offer with the current id clears the snackbar and a stale id does not`() {
        viewModel.showUndo(TextResource.fromString("A")) {}
        val id = viewModel.uiState.value.undoSnackbar!!.id

        viewModel.onUndoExpired(id + 1)
        assertNotNull(viewModel.uiState.value.undoSnackbar)

        viewModel.onUndoExpired(id)
        assertNull(viewModel.uiState.value.undoSnackbar)
    }

    /** Repositories hop to Dispatchers.IO, which the test dispatcher does not control: poll for the result. */
    private fun awaitUntil(condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + AWAIT_MS
        while (!condition() && System.currentTimeMillis() < deadline) Thread.sleep(POLL_MS)
    }

    private fun folderPackages() = viewModel.uiState.value.folders.first { it.id == FOLDER_ID }.appPackageNames

    private fun favorites() = viewModel.uiState.value.containerFavorites[CONTAINER_ID]

    private fun app(packageName: String, label: String) = AppInfo(
        label = label,
        packageName = packageName,
        componentName = ComponentName(packageName, "$packageName.Main"),
        icon = ColorDrawable(),
    )

    private companion object {
        const val FOLDER_ID = "undo_test_folder"
        const val CONTAINER_ID = "undo_test_container"
        const val AWAIT_MS = 5000L
        const val POLL_MS = 20L
        const val BUTTON_ID = "undo_test_button"
        const val GRID_ITEM_ID = "undo_test_grid_item"
    }
}
