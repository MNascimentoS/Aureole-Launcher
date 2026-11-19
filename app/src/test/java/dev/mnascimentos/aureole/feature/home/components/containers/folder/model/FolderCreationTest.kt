package dev.mnascimentos.aureole.feature.home.components.containers.folder.model

import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FolderCreationTest {

    private val opened = MainUiState(activeFolder = AppFolder(id = "f1", name = "Bank"))

    @Test
    fun `create and pick apps opens the app picker of the new folder`() {
        assertTrue(opened.afterFolderCreated(pickApps = true).isAddAppToFolderDialogVisible)
    }

    @Test
    fun `plain create leaves the folder open without the picker`() {
        assertEquals(opened, opened.afterFolderCreated(pickApps = false))
    }

    @Test
    fun `without an open folder there is no picker to open`() {
        val state = MainUiState()

        assertEquals(state, state.afterFolderCreated(pickApps = true))
    }
}
