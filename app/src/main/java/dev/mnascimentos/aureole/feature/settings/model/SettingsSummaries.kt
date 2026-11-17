package dev.mnascimentos.aureole.feature.settings.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R

/** Value shown under "Color palette": the palette name, or null (no second line) while the name is blank. */
fun paletteSummary(selectedThemeName: String): String? = selectedThemeName.trim().ifEmpty { null }

/** Value shown under "Change font": the font name, or null (no second line) while the name is blank. */
fun fontSummary(selectedFontName: String): String? = selectedFontName.trim().ifEmpty { null }

/** Value shown under "Set background": a custom image when there is one, a solid color otherwise. */
@StringRes
fun backgroundSummary(isCustomWallpaperSet: Boolean): Int =
    if (isCustomWallpaperSet) R.string.settings_appearance_custom_image else R.string.settings_appearance_solid_color

/** Value shown under "Glass effect": the label of the level the sheet would open on. */
@StringRes
fun glassSummary(uiState: SettingsUiState): Int = uiState.shownGlassLevel().labelRes

/** Next scale type when "Image scale" is tapped: Crop, then Fit, then Fill, then Crop again. */
fun nextWallpaperScaleType(current: String): String = when (current) {
    "Crop" -> "Fit"
    "Fit" -> "Fill"
    else -> "Crop"
}
