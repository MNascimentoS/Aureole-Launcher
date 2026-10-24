package dev.mnascimentos.aureole.feature.home.model

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainUiStateOverlayTest {

    private val folder = AppFolder(id = "folder_1", name = "Trabalho")
    private val app = AppInfo("Mail", "com.mail", ComponentName("com.mail", "com.mail.Main"), ColorDrawable())

    @Test
    fun `when nothing is open, should report no active overlay`() {
        assertFalse(MainUiState().isOverlayActive)
    }

    @Test
    fun `when any single overlay is open, should report an active overlay`() {
        val overlays = mapOf<String, (MainUiState) -> MainUiState>(
            "grid edit mode" to { it.copy(isGridEditMode = true) },
            "favorite picker" to { it.copy(showFavoritePickerDialog = true) },
            "widget picker" to { it.copy(showWidgetPicker = true) },
            "widget popup" to { it.copy(showWidgetPopup = true) },
            "widget resize" to { it.copy(showWidgetResizeDialog = true) },
            "widget stack sheet" to { it.copy(activeWidgetStackBottomSheet = WidgetStackBottomSheetState(1, "s")) },
            "add container" to { it.copy(showAddContainerDialog = true) },
            "editing grid item" to { it.copy(editingGridItem = LauncherItemState(id = "item")) },
            "edit container" to { it.copy(isEditContainerDialogVisible = true) },
            "container app sheet" to {
                it.copy(activeContainerAppBottomSheet = ContainerAppBottomSheetState(app, "p"))
            },
            "container folder sheet" to {
                it.copy(activeContainerFolderBottomSheet = ContainerFolderBottomSheetState(folder, "p"))
            },
            "folder app sheet" to { it.copy(activeFolderAppBottomSheet = FolderAppBottomSheetState(app, folder)) },
            "release notes" to { it.copy(showReleaseNotesBottomSheet = true) },
            "customize" to { it.copy(showCustomizeBottomSheet = true) },
            "edit clock" to { it.copy(showEditClockBottomSheet = true) },
            "quick button settings" to { it.copy(showQuickButtonSettingsBottomSheet = true) },
            "quick button appearance" to { it.copy(showQuickButtonAppearanceBottomSheet = true) },
            "shortcut selection" to { it.copy(showShortcutSelectionBottomSheet = true) },
            "all apps drawer" to { it.copy(isAllAppsDrawerOpen = true) },
            "search query" to { it.copy(searchQuery = "ma") },
            "opened folder" to { it.copy(activeFolder = folder) },
            "create folder" to { it.copy(isCreateFolderDialogVisible = true) },
            "add app to folder" to { it.copy(isAddAppToFolderDialogVisible = true) },
            "rename folder" to { it.copy(isRenameFolderDialogVisible = true) },
        )

        overlays.forEach { (name, open) ->
            assertTrue("$name should count as an overlay", open(MainUiState()).isOverlayActive)
        }
    }

    @Test
    fun `when a stack has widgets, should return them`() {
        val state = MainUiState(widgetStacks = mapOf("stack_a" to listOf(3, 4)), topWidgetIds = listOf(9))

        assertEquals(listOf(3, 4), state.getWidgetIdsForStack("stack_a"))
    }

    @Test
    fun `when the top stack is not in the map, should fall back to the top widget ids`() {
        val state = MainUiState(topWidgetIds = listOf(9))

        assertEquals(listOf(9), state.getWidgetIdsForStack("top_widgets"))
        assertEquals(emptyList<Int>(), state.getWidgetIdsForStack("unknown"))
    }
}
