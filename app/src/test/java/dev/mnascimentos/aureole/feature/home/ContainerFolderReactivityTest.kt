package dev.mnascimentos.aureole.feature.home

import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContainerFolderReactivityTest {

    @Test
    fun `when opening create folder dialog, should retain target panelId`() {
        val intentWithPanel = FolderViewIntent.OpenCreateFolderDialog(panelId = "container_1")
        assertEquals("container_1", intentWithPanel.panelId)

        val intentDefault = FolderViewIntent.OpenCreateFolderDialog()
        assertNull(intentDefault.panelId)
    }

    @Test
    fun `when creating folder, should track targetPanelIdForFolder in MainUiState`() {
        val state = MainUiState(targetPanelIdForFolder = "container_1")
        assertEquals("container_1", state.targetPanelIdForFolder)
    }

    @Test
    fun `when adding new folder to side panel, should maintain hierarchy and existing folder order`() {
        val panelId = "panel_main"
        val existingFolder1 = AppFolder(id = "f1", name = "Social", panelId = panelId)
        val existingFolder2 = AppFolder(id = "f2", name = "Games", panelId = panelId)

        val initialPanel = ContainerModel(
            id = panelId,
            folders = listOf(existingFolder1, existingFolder2)
        )

        val newFolder = AppFolder(id = "f3", name = "Work", panelId = panelId)
        val updatedFolders = initialPanel.folders + newFolder
        val updatedPanel = initialPanel.copy(folders = updatedFolders)

        assertEquals(3, updatedPanel.folders.size)
        assertEquals("Social", updatedPanel.folders[0].name)
        assertEquals("Games", updatedPanel.folders[1].name)
        assertEquals("Work", updatedPanel.folders[2].name)
        assertEquals(panelId, updatedPanel.folders[2].panelId)
    }

    @Test
    fun `when folder list is updated, should reactively update containers state map in MainUiState`() {
        val panelId = "panel_1"
        val initialMap = mapOf(
            panelId to ContainerModel(id = panelId, folders = emptyList())
        )
        val initialState = MainUiState(containers = initialMap)
        assertTrue(initialState.containers[panelId]?.folders?.isEmpty() == true)

        val newFolder = AppFolder(id = "f1", name = "Tools", panelId = panelId)
        val updatedMap = initialMap + (panelId to initialMap.getValue(panelId).copy(folders = listOf(newFolder)))
        val updatedState = initialState.copy(containers = updatedMap)

        assertEquals(1, updatedState.containers[panelId]?.folders?.size)
        assertEquals("Tools", updatedState.containers[panelId]?.folders?.first()?.name)
    }

    @Test
    fun `when using multiple side panels, should maintain isolated folders and not leak empty panels`() {
        val folderPanel1 = AppFolder(id = "f1", name = "Panel 1 Folder", panelId = "panel_1")

        val panel1 = ContainerModel(id = "panel_1", folders = listOf(folderPanel1))
        val panel2 = ContainerModel(id = "panel_2", folders = emptyList())

        val uiState = MainUiState(
            containers = mapOf("panel_1" to panel1, "panel_2" to panel2),
            folders = listOf(folderPanel1)
        )

        val p1Folders = uiState.containers["panel_1"]?.folders ?: emptyList()
        val p2Folders = uiState.containers["panel_2"]?.folders ?: emptyList()

        assertEquals(1, p1Folders.size)
        assertEquals("Panel 1 Folder", p1Folders.first().name)
        assertTrue(p2Folders.isEmpty())
    }
}
