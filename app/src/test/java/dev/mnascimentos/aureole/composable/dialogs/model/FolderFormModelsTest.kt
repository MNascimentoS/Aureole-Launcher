package dev.mnascimentos.aureole.composable.dialogs.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FolderFormModelsTest {

    @Test
    fun `a blank name becomes the placeholder`() {
        assertEquals("New Folder", resolveFolderName("", "New Folder"))
        assertEquals("New Folder", resolveFolderName("   ", "New Folder"))
    }

    @Test
    fun `a typed name is trimmed`() {
        assertEquals("Work", resolveFolderName("  Work ", "New Folder"))
    }

    @Test
    fun `icon rows start with the letter option and have the grid columns`() {
        val keys = List(FOLDER_ICON_COLUMNS + 1) { "k$it" }
        val rows = folderIconRows(keys)

        assertNull(rows.first().first())
        assertEquals(FOLDER_ICON_COLUMNS, rows.first().size)
        assertEquals(keys.takeLast(2), rows.last())
        assertEquals(2, rows.size)
    }

    @Test
    fun `no icons still gives the letter option`() {
        assertEquals(listOf(listOf<String?>(null)), folderIconRows(emptyList()))
    }
}
