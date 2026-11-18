package dev.mnascimentos.aureole.feature.home.components.containers.model

import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormMode
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ManageItemsEditorTest {

    private fun item(type: ContainerItemType, folderId: String? = null, quickButtonId: String? = null) =
        ContainerItemEntity(
            id = "item",
            panelId = "p",
            itemType = type,
            folderId = folderId,
            quickButtonId = quickButtonId
        )

    @Test
    fun `a folder and a quick button open their editor, an app opens nothing`() {
        assertEquals(
            ManageItemsPage.FolderEditor("f"),
            item(ContainerItemType.FOLDER, folderId = "f").editorPage()
        )
        assertEquals(
            ManageItemsPage.QuickButtonEditor("q"),
            item(ContainerItemType.QUICK_BUTTON, quickButtonId = "q").editorPage()
        )
        assertEquals(ManageItemsPage.QuickButtonEditor("item"), item(ContainerItemType.QUICK_BUTTON).editorPage())
        assertNull(item(ContainerItemType.APP).editorPage())
        assertNull(item(ContainerItemType.FOLDER).editorPage())
    }

    @Test
    fun `back from an editor goes to the list, from the list it leaves Manage items`() {
        assertEquals(ManageItemsPage.ItemList, ManageItemsPage.FolderEditor("f").backTarget())
        assertEquals(ManageItemsPage.ItemList, ManageItemsPage.QuickButtonEditor("q").backTarget())
        assertNull(ManageItemsPage.ItemList.backTarget())
    }

    @Test
    fun `opening the quick button editor and going back returns to the list`() {
        val editor = ManageItemsEditor()
        assertFalse(editor.isEditing)

        editor.openQuickButton("q")

        assertTrue(editor.isEditing)
        assertEquals(ManageItemsPage.QuickButtonEditor("q"), editor.page)
        assertTrue(editor.back())
        assertEquals(ManageItemsPage.ItemList, editor.page)
        assertFalse(editor.back())
    }

    @Test
    fun `opening a folder starts its draft from the folder, on the form`() {
        val editor = ManageItemsEditor()
        editor.folderMode = FolderFormMode.ALL_ICONS

        editor.openFolder(AppFolder(id = "f", name = "Fun"))

        assertEquals(ManageItemsPage.FolderEditor("f"), editor.page)
        assertEquals("Fun", editor.folderDraft.name)
        assertEquals(FolderFormMode.FORM, editor.folderMode)
        editor.editFolderDraft(editor.folderDraft.copy(name = "Games "))
        assertEquals("Games ", editor.folderDraft.name)
    }
}
