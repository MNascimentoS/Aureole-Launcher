package dev.mnascimentos.aureole.feature.settings.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R

/** Title of the area sheet. */
@StringRes
fun settingsAreaTitle(area: SettingsArea): Int = when (area) {
    SettingsArea.APPEARANCE -> R.string.settings_appearance_title
    SettingsArea.BEHAVIOR -> R.string.settings_behavior_title
    SettingsArea.ADVANCED -> R.string.settings_advanced_title
}

/** Title of the sheet: the picker shown inside it, or the area. */
@StringRes
fun settingsSheetTitle(area: SettingsArea, inner: SettingsInnerKind?): Int = when (inner) {
    SettingsInnerKind.PALETTE -> R.string.settings_appearance_color_palette
    SettingsInnerKind.FONT -> R.string.home_customize_adjust_font
    SettingsInnerKind.BACKGROUND -> R.string.settings_appearance_set_background_title
    null -> settingsAreaTitle(area)
}
