package dev.mnascimentos.aureole.core.data.repository

import androidx.core.content.edit
import dev.mnascimentos.aureole.core.designsystem.palette.resolveThemeSeedColor
import dev.mnascimentos.aureole.core.designsystem.palette.vividBackgroundReplacement

private const val KEY_LEGACY_SHOW_ALL_APPS_ON_HOME = "show_all_apps_on_home"
private const val KEY_LEGACY_SHOW_WIDGET_DOTS = "show_widget_dots"
private const val KEY_VIVID_BACKGROUND_MIGRATED = "vivid_background_migrated"

/**
 * Migration of the removed "show all apps on Home" switch: true once, when the user had turned it on, so the
 * caller clears the saved favorites (an empty favorites list now means "show all"). The old key is deleted, so
 * later calls return false. A switch that was never saved keeps the favorites: the Home never listed all apps.
 */
fun SettingsRepository.consumeLegacyShowAllApps(): Boolean {
    val wasOn = prefs.getBoolean(KEY_LEGACY_SHOW_ALL_APPS_ON_HOME, false)
    if (prefs.contains(KEY_LEGACY_SHOW_ALL_APPS_ON_HOME)) prefs.edit { remove(KEY_LEGACY_SHOW_ALL_APPS_ON_HOME) }
    return wasOn
}

/**
 * One-time migration of the removed global widget indicator switch: returns true when it was saved as off,
 * and always erases the key so this answers only once.
 */
fun SettingsRepository.consumeLegacyWidgetDotsOff(): Boolean {
    if (!prefs.contains(KEY_LEGACY_SHOW_WIDGET_DOTS)) return false
    val wasOff = !prefs.getBoolean(KEY_LEGACY_SHOW_WIDGET_DOTS, true)
    prefs.edit { remove(KEY_LEGACY_SHOW_WIDGET_DOTS) }
    return wasOff
}

/**
 * One-time migration of the removed "Vivid" background swatches (RF-03 of polimento-ui): a background saved from
 * one of [vividColors] becomes the default palette (see [vividBackgroundReplacement]). Runs once; later calls do
 * nothing.
 */
fun SettingsRepository.migrateVividBackground(vividColors: Collection<Int>) {
    if (prefs.getBoolean(KEY_VIVID_BACKGROUND_MIGRATED, false)) return
    vividBackgroundReplacement(selectedThemeName, manualSeedColor, vividColors)?.let { themeName ->
        selectedThemeName = themeName
        manualSeedColor = resolveThemeSeedColor(themeName)
    }
    prefs.edit { putBoolean(KEY_VIVID_BACKGROUND_MIGRATED, true) }
}
