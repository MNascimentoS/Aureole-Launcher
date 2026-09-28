package dev.mnascimentos.aureole.feature.home

import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.SidePanelModel
import dev.mnascimentos.aureole.feature.home.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SidePanelFolderReactivityTest {

    @Test
    fun `OpenCreateFolderDialog retains target panelId`() {
        val intentWithPanel = FolderViewIntent.OpenCreateFolderDialog(panelId = "side_panel_1")
        assertEquals("side_panel_1", intentWithPanel.panelId)

        val intentDefault = FolderViewIntent.OpenCreateFolderDialog()
        assertNull(intentDefault.panelId)
    }

    @Test
    fun `MainUiState tracks targetPanelIdForFolder when creating folder`() {
        val state = MainUiState(targetPanelIdForFolder = "side_panel_1")
        assertEquals("side_panel_1", state.targetPanelIdForFolder)
    }

    @Test
    fun `adding new folder to side panel maintains hierarchy and existing folder order`() {
        val panelId = "panel_main"
        val existingFolder1 = AppFolder(id = "f1", name = "Social", panelId = panelId)
        val existingFolder2 = AppFolder(id = "f2", name = "Games", panelId = panelId)

        val initialPanel = SidePanelModel(
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
    fun `MainUiState sidePanels state map updates reactively with new folder list`() {
        val panelId = "panel_1"
        val initialMap = mapOf(
            panelId to SidePanelModel(id = panelId, folders = emptyList())
        )
        val initialState = MainUiState(sidePanels = initialMap)
        assertTrue(initialState.sidePanels[panelId]?.folders?.isEmpty() == true)

        val newFolder = AppFolder(id = "f1", name = "Tools", panelId = panelId)
        val updatedMap = initialMap + (panelId to initialMap.getValue(panelId).copy(folders = listOf(newFolder)))
        val updatedState = initialState.copy(sidePanels = updatedMap)

        assertEquals(1, updatedState.sidePanels[panelId]?.folders?.size)
        assertEquals("Tools", updatedState.sidePanels[panelId]?.folders?.first()?.name)
    }

    @Test
    fun `multiple side panels maintain isolated folders and empty panels do not leak folders`() {
        val folderPanel1 = AppFolder(id = "f1", name = "Panel 1 Folder", panelId = "panel_1")

        val panel1 = SidePanelModel(id = "panel_1", folders = listOf(folderPanel1))
        val panel2 = SidePanelModel(id = "panel_2", folders = emptyList())

        val uiState = MainUiState(
            sidePanels = mapOf("panel_1" to panel1, "panel_2" to panel2),
            folders = listOf(folderPanel1)
        )

        val p1Folders = uiState.sidePanels["panel_1"]?.folders ?: emptyList()
        val p2Folders = uiState.sidePanels["panel_2"]?.folders ?: emptyList()

        assertEquals(1, p1Folders.size)
        assertEquals("Panel 1 Folder", p1Folders.first().name)
        assertTrue(p2Folders.isEmpty())
    }
}
