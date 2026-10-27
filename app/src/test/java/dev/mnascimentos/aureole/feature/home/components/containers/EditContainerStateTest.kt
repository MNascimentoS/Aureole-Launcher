package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerOptionsState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditContainerStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createDefaultOptionsState(
        position: String = "Center",
        orientation: String = "Vertical",
        isGridFolderEnabled: Boolean = false
    ) = ContainerOptionsState(
        position = position,
        orientation = orientation,
        isBackgroundEnabled = true,
        isExpandCell = false,
        showAddFolderButton = true,
        showFolderLabels = true,
        isGridFolderEnabled = isGridFolderEnabled,
        hideText = false,
        textColor = null,
        backgroundColor = null,
        strokeEnabled = false,
        strokeColor = null,
        folderColor = null,
        folderStrokeEnabled = false,
        scrollUpShortcut = null,
        scrollDownShortcut = null
    )

    @Test
    fun `when panel items is empty, rememberEditContainerState should build initial items`() {
        val folder = AppFolder(id = "f1", panelId = "p1", name = "Folder 1")
        val panel = ContainerModel(
            id = "p1",
            items = emptyList(),
            folders = listOf(folder),
            appPackageNames = listOf("com.example.app1")
        )

        var state: EditContainerState? = null
        composeTestRule.setContent {
            AureoleLauncherTheme {
                state = rememberEditContainerState(panel)
            }
        }

        assertNotNull(state)
        val loadedState = state!!
        assertEquals(2, loadedState.itemsList.size)
        assertEquals(ContainerItemType.FOLDER, loadedState.itemsList[0].itemType)
        assertEquals("f1", loadedState.itemsList[0].folderId)
        assertEquals(ContainerItemType.APP, loadedState.itemsList[1].itemType)
        assertEquals("com.example.app1", loadedState.itemsList[1].packageName)
    }

    @Test
    fun `when panel items is not empty, rememberEditContainerState should use existing items`() {
        val existingItem = ContainerItemEntity(
            id = "item1",
            panelId = "p1",
            itemType = ContainerItemType.APP,
            packageName = "com.app"
        )
        val panel = ContainerModel(id = "p1", items = listOf(existingItem))

        var state: EditContainerState? = null
        composeTestRule.setContent {
            AureoleLauncherTheme {
                state = rememberEditContainerState(panel)
            }
        }

        assertNotNull(state)
        val loadedState = state!!
        assertEquals(1, loadedState.itemsList.size)
        assertEquals("item1", loadedState.itemsList[0].id)
    }

    @Test
    fun `when moveItemUp with valid index, should swap with previous element`() {
        val state = createTestState()
        assertEquals("app1", state.itemsList[0].packageName)
        assertEquals("app2", state.itemsList[1].packageName)

        state.moveItemUp(1)

        assertEquals("app2", state.itemsList[0].packageName)
        assertEquals("app1", state.itemsList[1].packageName)
    }

    @Test
    fun `when moveItemUp with index 0, should do nothing`() {
        val state = createTestState()
        state.moveItemUp(0)
        assertEquals("app1", state.itemsList[0].packageName)
    }

    @Test
    fun `when moveItemDown with valid index, should swap with next element`() {
        val state = createTestState()
        state.moveItemDown(0)
        assertEquals("app2", state.itemsList[0].packageName)
        assertEquals("app1", state.itemsList[1].packageName)
    }

    @Test
    fun `when moveItemDown with last index, should do nothing`() {
        val state = createTestState()
        val lastIdx = state.itemsList.size - 1
        val lastPkg = state.itemsList[lastIdx].packageName
        state.moveItemDown(lastIdx)
        assertEquals(lastPkg, state.itemsList[lastIdx].packageName)
    }

    @Test
    fun `when removeItem for FOLDER item, should remove item and associated folder`() {
        val folder = AppFolder(id = "f1", panelId = "p1", name = "Test Folder")
        val folderItem = ContainerItemEntity(
            id = "item_f1",
            panelId = "p1",
            itemType = ContainerItemType.FOLDER,
            folderId = "f1"
        )
        val state = EditContainerState(
            panel = ContainerModel(id = "p1"),
            options = createDefaultOptionsState(),
            itemsList = listOf(folderItem),
            folders = listOf(folder)
        )

        state.removeItem(0)

        assertTrue(state.itemsList.isEmpty())
        assertTrue(state.folders.isEmpty())
    }

    @Test
    fun `when removeItem for APP item, should remove item without modifying folders`() {
        val folder = AppFolder(id = "f1", panelId = "p1", name = "Folder")
        val appItem = ContainerItemEntity(
            id = "item_a1",
            panelId = "p1",
            itemType = ContainerItemType.APP,
            packageName = "com.app"
        )
        val state = EditContainerState(
            panel = ContainerModel(id = "p1"),
            options = createDefaultOptionsState(),
            itemsList = listOf(appItem),
            folders = listOf(folder)
        )

        state.removeItem(0)

        assertTrue(state.itemsList.isEmpty())
        assertEquals(1, state.folders.size)
    }

    @Test
    fun `when removeItem with invalid index, should do nothing`() {
        val state = createTestState()
        val initialSize = state.itemsList.size
        state.removeItem(-1)
        state.removeItem(100)
        assertEquals(initialSize, state.itemsList.size)
    }

    @Test
    fun `when addFolder, should append new folder and folder item`() {
        val state = EditContainerState(
            panel = ContainerModel(id = "p1"),
            options = createDefaultOptionsState(),
            itemsList = emptyList(),
            folders = emptyList()
        )

        state.addFolder("New Folder")

        assertEquals(1, state.folders.size)
        assertEquals("New Folder", state.folders[0].name)
        assertEquals(1, state.itemsList.size)
        assertEquals(ContainerItemType.FOLDER, state.itemsList[0].itemType)
        assertEquals(state.folders[0].id, state.itemsList[0].folderId)
    }

    @Test
    fun `when addApp for new package, should append item`() {
        val state = createTestState()
        val initialSize = state.itemsList.size

        state.addApp("com.new.app")

        assertEquals(initialSize + 1, state.itemsList.size)
        assertEquals("com.new.app", state.itemsList.last().packageName)
    }

    @Test
    fun `when addApp for existing package, should not duplicate`() {
        val state = createTestState()
        val initialSize = state.itemsList.size

        state.addApp("app1")

        assertEquals(initialSize, state.itemsList.size)
    }

    @Test
    fun `when toModel called, should return updated ContainerModel with reordered items`() {
        val folder = AppFolder(id = "f1", panelId = "p1", name = "Folder 1")
        val folderItem = ContainerItemEntity(
            id = "item_f1",
            panelId = "p1",
            itemType = ContainerItemType.FOLDER,
            folderId = "f1"
        )
        val appItem = ContainerItemEntity(
            id = "item_a1",
            panelId = "p1",
            itemType = ContainerItemType.APP,
            packageName = "com.app"
        )

        val options = createDefaultOptionsState(
            position = "Center",
            orientation = "Horizontal",
            isGridFolderEnabled = true
        )
        val state = EditContainerState(
            panel = ContainerModel(id = "p1"),
            options = options,
            itemsList = listOf(folderItem, appItem),
            folders = listOf(folder)
        )

        val model = state.toModel()

        assertEquals("Center", model.position)
        assertEquals("Horizontal", model.orientation)
        assertEquals(listOf("com.app"), model.appPackageNames)
        assertEquals(1, model.folders.size)
        assertTrue(model.folders[0].displayAsGrid)
        assertEquals(2, model.items.size)
        assertEquals(0, model.items[0].orderIndex)
        assertEquals(1, model.items[1].orderIndex)
    }

    @Test
    fun `when addQuickButton called, should append quick button item`() {
        val state = createTestState()
        val initialSize = state.itemsList.size

        state.addQuickButton()

        assertEquals(initialSize + 1, state.itemsList.size)
        assertEquals(ContainerItemType.QUICK_BUTTON, state.itemsList.last().itemType)
        assertEquals(1, state.quickButtons.size)

        val model = state.toModel()
        assertEquals(1, model.quickButtons.size)
    }

    private fun createTestState(): EditContainerState {
        val item1 = ContainerItemEntity(
            id = "1",
            panelId = "p1",
            itemType = ContainerItemType.APP,
            packageName = "app1"
        )
        val item2 = ContainerItemEntity(
            id = "2",
            panelId = "p1",
            itemType = ContainerItemType.APP,
            packageName = "app2"
        )
        return EditContainerState(
            panel = ContainerModel(id = "p1"),
            options = createDefaultOptionsState(),
            itemsList = listOf(item1, item2),
            folders = emptyList()
        )
    }
}
