package dev.mnascimentos.aureole.feature.home

import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.feature.home.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContainerFolderReactivityTest {

    @Test
    fun `OpenCreateFolderDialog retains target panelId`() {
        val intentWithPanel = FolderViewIntent.OpenCreateFolderDialog(panelId = "container_1")
        assertEquals("container_1", intentWithPanel.panelId)

        val intentDefault = FolderViewIntent.OpenCreateFolderDialog()
        assertNull(intentDefault.panelId)
    }

    @Test
    fun `MainUiState tracks targetPanelIdForFolder when creating folder`() {
        val state = MainUiState(targetPanelIdForFolder = "container_1")
        assertEquals("container_1", state.targetPanelIdForFolder)
    }

    @Test
    fun `adding new folder to side panel maintains hierarchy and existing folder order`() {
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
    fun `MainUiState containers state map updates reactively with new folder list`() {
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
    fun `multiple side panels maintain isolated folders and empty panels do not leak folders`() {
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
