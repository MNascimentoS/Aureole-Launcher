package dev.mnascimentos.aureole.feature.home.components.containers.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ManageItemsEditorQuickButtonFlowTest {

    @Test
    fun `when creating, leaving the appearance should open the shortcut selection`() {
        val editor = ManageItemsEditor()
        editor.openQuickButton("qb", isNew = true)

        assertTrue(editor.back())

        assertEquals(ManageItemsPage.QuickButtonShortcut("qb"), editor.page)
    }

    @Test
    fun `when on the shortcut selection, back should return to the appearance`() {
        val editor = ManageItemsEditor()
        editor.openQuickButton("qb", isNew = true)
        editor.back()

        assertTrue(editor.back())

        assertEquals(ManageItemsPage.QuickButtonEditor("qb"), editor.page)
        assertTrue(editor.isCreatingQuickButton)
    }

    @Test
    fun `when a shortcut is chosen, the creation ends and the list shows again`() {
        val editor = ManageItemsEditor()
        editor.openQuickButton("qb", isNew = true)
        editor.back()

        editor.finishQuickButtonCreation()

        assertEquals(ManageItemsPage.ItemList, editor.page)
        assertFalse(editor.isCreatingQuickButton)
    }

    @Test
    fun `when editing an existing button, back should only return to the list`() {
        val editor = ManageItemsEditor()
        editor.openQuickButton("qb")

        assertTrue(editor.back())

        assertEquals(ManageItemsPage.ItemList, editor.page)
        assertFalse(editor.back())
    }
}
