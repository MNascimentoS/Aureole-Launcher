@file:Suppress(
    "LargeClass",
    "TooManyFunctions",
    "FileLength",
    "MagicNumber",
    "MatchingDeclarationName",
    "unused"
)
// Suppressed detekt rules because this file serves as the unified Design System color palette registry.

package dev.mnascimentos.aureole.core.designsystem.palette

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleColors

data class ThemePalette(
    val name: String,
    val colors: AureoleColors,
)

/**
 * Builds the light variant of a dark palette by mirroring the tonal ramp.
 */
fun ThemePalette.toLight(): ThemePalette = ThemePalette(
    name = "$name Light",
    colors = AureoleColors(
        onSurfaceHigh = colors.background,
        onSurfaceMedium = colors.surface,
        onSurfaceLow = colors.surfaceVariant,
        outline = colors.outline,
        surfaceVariant = colors.onSurfaceLow,
        surface = colors.onSurfaceMedium,
        background = colors.onSurfaceHigh,
    )
)

val frostbitePalette = ThemePalette(
    name = "Frostbite",
    colors = AureoleColors(
        onSurfaceHigh = Color(0xFFFFFFFF),
        onSurfaceMedium = Color(0xFFC5D8E8),
        onSurfaceLow = Color(0xFF9BB4C8),
        outline = Color(0xFF7D96AA),
        surfaceVariant = Color(0xFF4A5D6B),
        surface = Color(0xFF2B373E),
        background = Color(0xFF12181D),
    )
)

/**
 * Every base dark palette from pallets.html.
 */
val allPalettes = listOf(frostbitePalette) + presetPalettes

/**
 * Creates a dynamic [ThemePalette] from a seed ARGB color int.
 */
fun createPaletteFromSeedColor(seedColorInt: Int, isDarkTheme: Boolean = true): ThemePalette {
    val colors = buildSeedAureoleColors(seedColorInt)
    return ThemePalette(
        name = if (isDarkTheme) "Custom" else "Custom Light",
        colors = colors
    )
}

private fun buildSeedAureoleColors(seedColorInt: Int): AureoleColors {
    val hsl = FloatArray(3)
    ColorUtils.colorToHSL(seedColorInt, hsl)
    val hue = hsl[0]
    val sat = hsl[1]
    val isDarkBg = ColorUtils.calculateLuminance(seedColorInt) < 0.5f

    val offset = if (isDarkBg) 1f else -1f
    fun adjustLightness(delta: Float) = (hsl[2] + offset * delta).coerceIn(0f, 1f)

    val surfaceColor = Color(ColorUtils.HSLToColor(floatArrayOf(hue, sat, adjustLightness(0.08f))))
    val surfaceVariantColor = Color(ColorUtils.HSLToColor(floatArrayOf(hue, sat, adjustLightness(0.16f))))
    val outlineColor = Color(ColorUtils.HSLToColor(floatArrayOf(hue, sat, adjustLightness(0.28f))))

    val onHigh = if (isDarkBg) Color(0xFFFFFFFF) else Color(0xFF101418)
    val onMedium =
        Color(ColorUtils.HSLToColor(floatArrayOf(hue, (sat * 0.3f).coerceIn(0f, 1f), if (isDarkBg) 0.82f else 0.22f)))
    val onLow =
        Color(ColorUtils.HSLToColor(floatArrayOf(hue, (sat * 0.3f).coerceIn(0f, 1f), if (isDarkBg) 0.65f else 0.38f)))

    return AureoleColors(
        background = Color(seedColorInt),
        surface = surfaceColor,
        surfaceVariant = surfaceVariantColor,
        outline = outlineColor,
        onSurfaceLow = onLow,
        onSurfaceMedium = onMedium,
        onSurfaceHigh = onHigh
    )
}

/**
 * Returns the theme palette corresponding to [name].
 * If [isDarkTheme] is true, returns the dark variant; otherwise returns the light variant.
 */
fun getPaletteByName(
    name: String,
    isDarkTheme: Boolean = true,
    seedColor: Int? = null
): ThemePalette {
    val cleanName = name.removeSuffix(" Light").trim()
    if (cleanName.equals("Custom", ignoreCase = true)) {
        return createPaletteFromSeedColor(
            seedColor ?: SettingsRepository.DEFAULT_SEED_COLOR,
            isDarkTheme
        )
    }
    val darkPalette =
        allPalettes.find { it.name.equals(cleanName, ignoreCase = true) } ?: frostbitePalette
    return if (isDarkTheme) darkPalette else darkPalette.toLight()
}

/**
 * Returns the light variant of the palette corresponding to [name].
 */
fun getLightPaletteByName(name: String, seedColor: Int? = null): ThemePalette {
    return getPaletteByName(name, isDarkTheme = false, seedColor = seedColor)
}
