package dev.mnascimentos.aureole.feature.settings.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EntryTapGuardTest {
    @Test
    fun `should ignore a tap right after the screen opens`() {
        assertFalse(isDeliberateTap(openedAtMs = 1_000, nowMs = 1_050))
    }

    @Test
    fun `should accept a tap once the guard window has passed`() {
        assertTrue(isDeliberateTap(openedAtMs = 1_000, nowMs = 1_000 + ENTRY_TAP_GUARD_MS))
    }
}
