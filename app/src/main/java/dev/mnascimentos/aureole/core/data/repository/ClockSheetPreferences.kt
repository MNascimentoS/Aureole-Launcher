package dev.mnascimentos.aureole.core.data.repository

import androidx.core.content.edit

private const val KEY_CLOCK_SAVE_HINT_DISMISSED = "clock_save_hint_dismissed"

/**
 * Whether the user tapped "Got it" on the onboarding notice of the clock editor ("Drag down to close · everything
 * you change here is already saved", relogio-home-sheet RF-D2). Once true the notice never shows again.
 */
var SettingsRepository.isClockSaveHintDismissed: Boolean
    get() = prefs.getBoolean(KEY_CLOCK_SAVE_HINT_DISMISSED, false)
    set(value) = prefs.edit { putBoolean(KEY_CLOCK_SAVE_HINT_DISMISSED, value) }
