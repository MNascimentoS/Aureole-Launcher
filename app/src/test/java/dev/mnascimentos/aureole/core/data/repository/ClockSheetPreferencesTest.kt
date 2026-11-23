package dev.mnascimentos.aureole.core.data.repository

import androidx.core.content.edit
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/** The "Got it" of the clock editor's onboarding notice is kept for good (relogio-home-sheet RF-D2). */
@RunWith(RobolectricTestRunner::class)
class ClockSheetPreferencesTest {

    private val repository = SettingsRepository(RuntimeEnvironment.getApplication())

    @After
    fun tearDown() {
        repository.prefs.edit { remove("clock_save_hint_dismissed") }
    }

    @Test
    fun `when never dismissed, should show the notice`() {
        assertFalse(repository.isClockSaveHintDismissed)
    }

    @Test
    fun `when dismissed, should remember it in a new repository`() {
        repository.isClockSaveHintDismissed = true

        assertTrue(SettingsRepository(RuntimeEnvironment.getApplication()).isClockSaveHintDismissed)
    }
}
