package dev.mnascimentos.aureole.core.designsystem.palette

/** Name of a theme built from a saved solid color (see [getPaletteByName]). */
private const val CUSTOM_THEME_NAME = "Custom"

/**
 * The theme a background saved from the removed "Vivid" swatches gives way to (RF-03 of polimento-ui, D-04/D-11),
 * or null when there is nothing to migrate. A solid color is saved as the "Custom" theme seeded by that color, whose
 * Background is the color itself, so the theme goes back to the default palette, whose Background the home then
 * shows. A named palette, a photo or any other color stay as they are.
 *
 * @param themeName The saved theme name ("Custom" for a solid color, with or without " Light").
 * @param seedColor The saved solid color.
 * @param vividColors The colors the "Vivid" section offered.
 */
fun vividBackgroundReplacement(themeName: String, seedColor: Int, vividColors: Collection<Int>): String? {
    val isCustom = themeName.removeSuffix(" Light").trim().equals(CUSTOM_THEME_NAME, ignoreCase = true)
    return frostbitePalette.name.takeIf { isCustom && seedColor in vividColors }
}
