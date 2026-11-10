package dev.mnascimentos.aureole.core.designsystem.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SheetHandleActionsTest {

    @Test
    fun `when right handed at rest, should offer expand then settings`() {
        assertEquals(
            listOf(SheetHandleAction.Expand, SheetHandleAction.Settings),
            sheetHandleActions(isExpanded = false, hasSettings = true, isLeftHanded = false)
        )
    }

    @Test
    fun `when expanded, should offer collapse`() {
        assertEquals(
            listOf(SheetHandleAction.Collapse, SheetHandleAction.Settings),
            sheetHandleActions(isExpanded = true, hasSettings = true, isLeftHanded = false)
        )
    }

    @Test
    fun `when left handed, should put settings first`() {
        assertEquals(
            listOf(SheetHandleAction.Settings, SheetHandleAction.Expand),
            sheetHandleActions(isExpanded = false, hasSettings = true, isLeftHanded = true)
        )
    }

    @Test
    fun `when there is no settings callback, should only offer resize`() {
        assertEquals(
            listOf(SheetHandleAction.Collapse),
            sheetHandleActions(isExpanded = true, hasSettings = false, isLeftHanded = true)
        )
    }
}
