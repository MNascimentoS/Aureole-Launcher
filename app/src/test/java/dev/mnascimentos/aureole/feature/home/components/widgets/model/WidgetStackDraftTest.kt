package dev.mnascimentos.aureole.feature.home.components.widgets.model

import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetStackDraftTest {

    @Test
    fun `selected falls back to the first widget`() {
        assertEquals(1, WidgetStackDraft(listOf(1, 2, 3)).selected)
    }

    @Test
    fun `selected is null for an empty stack`() {
        assertEquals(null, WidgetStackDraft(emptyList()).selected)
    }

    @Test
    fun `drag moves the card and selects it`() {
        val draft = WidgetStackDraft(listOf(1, 2, 3))
        draft.drag(0, 2)
        assertEquals(listOf(2, 3, 1), draft.widgets)
        assertEquals(1, draft.selected)
    }

    @Test
    fun `moveTo reorders, selects and bumps the reveal token`() {
        val draft = WidgetStackDraft(listOf(1, 2, 3))
        draft.moveTo(3, 0)
        assertEquals(listOf(3, 1, 2), draft.widgets)
        assertEquals(3, draft.selected)
        assertEquals(1, draft.revealToken)
    }

    @Test
    fun `removing the selected widget falls back to the first`() {
        val draft = WidgetStackDraft(listOf(1, 2, 3))
        draft.select(2)
        draft.remove(2)
        assertEquals(listOf(1, 3), draft.widgets)
        assertEquals(1, draft.selected)
    }

    @Test
    fun `sync restores the saved order`() {
        val draft = WidgetStackDraft(listOf(1, 2))
        draft.remove(1)
        draft.sync(listOf(1, 2))
        assertEquals(listOf(1, 2), draft.widgets)
    }
}
