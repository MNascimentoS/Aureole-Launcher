package dev.mnascimentos.aureole.composable.menu.model

import org.junit.Assert.assertEquals
import org.junit.Test

class MenuModelsTest {

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
