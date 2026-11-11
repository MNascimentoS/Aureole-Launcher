package dev.mnascimentos.aureole.composable.menu.model

import org.junit.Assert.assertEquals
import org.junit.Test

class MenuModelsTest {

    @Test
    fun `thumbOrder puts the most used item at the right end for right-handed users`() {
        assertEquals(
            listOf("uninstall", "info", "favorite"),
            thumbOrder(listOf("favorite", "info", "uninstall"), false)
        )
    }

    @Test
    fun `thumbOrder puts the most used item at the left end for left-handed users`() {
        assertEquals(listOf("favorite", "info", "uninstall"), thumbOrder(listOf("favorite", "info", "uninstall"), true))
    }

    @Test
    fun `thumbOrder keeps an empty or single list as is`() {
        assertEquals(emptyList<String>(), thumbOrder(emptyList<String>(), false))
        assertEquals(listOf("a"), thumbOrder(listOf("a"), false))
    }

    @Test
    fun `holdProgress grows linearly and stays between 0 and 1`() {
        assertEquals(0f, holdProgress(0L), 0f)
        assertEquals(0.5f, holdProgress(MENU_HOLD_MS / 2), 0.001f)
        assertEquals(1f, holdProgress(MENU_HOLD_MS), 0f)
        assertEquals(1f, holdProgress(MENU_HOLD_MS * 3), 0f)
        assertEquals(0f, holdProgress(-5L), 0f)
    }

    @Test
    fun `holdProgress is complete at once when the hold time is zero`() {
        assertEquals(1f, holdProgress(0L, 0L), 0f)
    }
}
