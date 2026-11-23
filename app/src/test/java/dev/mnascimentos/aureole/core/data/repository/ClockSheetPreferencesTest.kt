package dev.mnascimentos.aureole.core.data.repository

import androidx.core.content.edit
import dev.mnascimentos.aureole.core.data.model.CLOCK_CARD_THEME_REF
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

private const val KEY_ENABLED = "clock_background_enabled"
private const val KEY_REF = "clock_background_color_ref"

/**
 * The clock editor's settings: the "Got it" of its onboarding notice (RF-D2) and the card switch of the settings clock
 * look folded into the card color (RF-E3).
 */
@RunWith(RobolectricTestRunner::class)
class ClockSheetPreferencesTest {

    private val repository = SettingsRepository(RuntimeEnvironment.getApplication())
    private val savedRef = ThemeColorRef.Fixed(0xFF1A237E.toInt()).toJson()

    @After
    fun tearDown() {
        repository.resetClockSettings()
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

    @Test
    fun `when the old switch was off, should read no card and delete the switch`() {
        repository.prefs.edit { putBoolean(KEY_ENABLED, false).putString(KEY_REF, savedRef) }

        assertNull(repository.clockBackgroundColorRef)
        assertFalse(repository.isClockBackgroundEnabled)
        assertFalse(repository.prefs.contains(KEY_ENABLED))
    }

    @Test
    fun `when the old switch was on, should keep the color or take the theme card`() {
        repository.prefs.edit { putBoolean(KEY_ENABLED, true).putString(KEY_REF, savedRef) }
        assertEquals(savedRef, repository.clockBackgroundColorRef)

        repository.prefs.edit { putBoolean(KEY_ENABLED, true).remove(KEY_REF) }
        assertEquals(CLOCK_CARD_THEME_REF.toJson(), repository.clockBackgroundColorRef)
        assertTrue(repository.isClockBackgroundEnabled)
    }

    @Test
    fun `when the settings switch the card, should save the theme card or none, never the old switch`() {
        repository.isClockBackgroundEnabled = true
        assertEquals(CLOCK_CARD_THEME_REF.toJson(), repository.clockBackgroundColorRef)

        repository.isClockBackgroundEnabled = false
        assertNull(repository.clockBackgroundColorRef)
        assertFalse(repository.prefs.contains(KEY_ENABLED))
    }
}
