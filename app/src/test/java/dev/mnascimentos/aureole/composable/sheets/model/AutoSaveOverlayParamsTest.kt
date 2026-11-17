package dev.mnascimentos.aureole.composable.sheets.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AutoSaveOverlayParamsTest {

    @Test
    fun `a change commits`() {
        assertEquals(CloseOutcome.COMMIT, closeOutcome(initial = 1, current = 2))
    }

    @Test
    fun `no change does not commit`() {
        assertEquals(CloseOutcome.NO_CHANGE, closeOutcome(initial = "a", current = "a"))
    }

    @Test
    fun `closing twice commits only once`() {
        assertEquals(CloseOutcome.NO_CHANGE, closeOutcome(isChanged = true, alreadyCommitted = true))
        assertEquals(CloseOutcome.COMMIT, closeOutcome(isChanged = true, alreadyCommitted = false))
    }
}
