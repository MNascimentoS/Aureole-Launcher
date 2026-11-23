package dev.mnascimentos.aureole.core.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import dev.mnascimentos.aureole.core.data.model.CLOCK_CARD_THEME_REF
import dev.mnascimentos.aureole.core.data.model.migratedClockCardRef

private const val KEY_CLOCK_SAVE_HINT_DISMISSED = "clock_save_hint_dismissed"

/** The removed card switch of the settings clock look: read once by [migrateClockCard], then deleted. */
private const val KEY_LEGACY_CLOCK_BACKGROUND_ENABLED = "clock_background_enabled"
private const val KEY_CLOCK_BACKGROUND_COLOR = "clock_background_color"
private const val KEY_CLOCK_BACKGROUND_COLOR_REF = "clock_background_color_ref"

/**
 * Whether the user tapped "Got it" on the onboarding notice of the clock editor ("Drag down to close · everything
 * you change here is already saved", relogio-home-sheet RF-D2). Once true the notice never shows again.
 */
var SettingsRepository.isClockSaveHintDismissed: Boolean
    get() = prefs.getBoolean(KEY_CLOCK_SAVE_HINT_DISMISSED, false)
    set(value) = prefs.edit { putBoolean(KEY_CLOCK_SAVE_HINT_DISMISSED, value) }

/** The settings preferences, with the old card switch already folded into the card color ([migrateClockCard]). */
internal fun SettingsRepository.clockCardPrefs(): SharedPreferences {
    migrateClockCard()
    return prefs
}

/**
 * One-time migration of the removed "Show background card" switch of the settings clock look (RF-E3): off becomes
 * no card color ("None"), on keeps its color or takes the theme card ([migratedClockCardRef]). The switch key is
 * deleted, so this runs once and the key is never written again.
 */
internal fun SettingsRepository.migrateClockCard() {
    val settings = prefs
    if (!settings.contains(KEY_LEGACY_CLOCK_BACKGROUND_ENABLED)) return
    val ref = migratedClockCardRef(
        isEnabled = settings.getBoolean(KEY_LEGACY_CLOCK_BACKGROUND_ENABLED, false),
        legacyArgb = settings.getInt(KEY_CLOCK_BACKGROUND_COLOR, 0),
        refJson = settings.getString(KEY_CLOCK_BACKGROUND_COLOR_REF, null)
    )
    settings.edit {
        remove(KEY_LEGACY_CLOCK_BACKGROUND_ENABLED)
        putInt(KEY_CLOCK_BACKGROUND_COLOR, 0)
        if (ref == null) remove(KEY_CLOCK_BACKGROUND_COLOR_REF) else putString(KEY_CLOCK_BACKGROUND_COLOR_REF, ref)
    }
}

/** Gives the settings clock look the theme card ([CLOCK_CARD_THEME_REF]) or none; a card it has keeps its color. */
internal fun SettingsRepository.setClockCard(hasCard: Boolean) {
    if (hasCard == isClockBackgroundEnabled) return
    clockBackgroundColor = 0
    clockBackgroundColorRef = if (hasCard) CLOCK_CARD_THEME_REF.toJson() else null
}
