package dev.mnascimentos.aureole.composable.dialogs.model

import dev.mnascimentos.aureole.core.designsystem.icons.AureoleFolderIcons
import dev.mnascimentos.aureole.core.designsystem.icons.FolderIconCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
    fun `icon rows have the grid columns and keep every icon of the category`() {
        FolderIconCategory.entries.forEach { category ->
            val rows = folderIconRows(category)

            assertTrue(rows.dropLast(1).all { it.size == FOLDER_ICON_COLUMNS })
            assertEquals(folderIconKeys(category), rows.flatten())
        }
    }

    @Test
    fun `categories cover the 80 icons without repeating any`() {
        val keys = FolderIconCategory.entries.flatMap { folderIconKeys(it) }

        assertEquals(80, keys.size)
        assertEquals(keys.size, keys.toSet().size)
        assertEquals(AureoleFolderIcons.all.map { it.key }.toSet(), keys.toSet())
    }

    @Test
    fun `the first category is shown when no icon is set, and an unknown key shows the folder icon category`() {
        assertEquals(FolderIconCategory.WORK, initialFolderIconCategory(null))
        assertEquals(FolderIconCategory.TOOLS, initialFolderIconCategory("nope"))
    }

    @Test
    fun `the initial category is the one of the saved icon, old keys included`() {
        assertEquals(FolderIconCategory.MEDIA, initialFolderIconCategory("music_note"))
        assertEquals(FolderIconCategory.MEDIA, initialFolderIconCategory("play"))
        assertEquals(FolderIconCategory.MONEY, initialFolderIconCategory("cart"))
    }

    @Test
    fun `an icon is marked when its key or the old key that maps to it is saved`() {
        assertTrue(isFolderIconSelected("shopping_cart", "cart"))
        assertTrue(isFolderIconSelected("work", "work"))
        assertFalse(isFolderIconSelected("work", "star"))
        assertFalse(isFolderIconSelected("work", null))
    }
}
