package dev.mnascimentos.aureole.core.designsystem.palette

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.ColorUtils
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleColors

private const val HSL_SIZE = 3
private const val DARK_LUMINANCE_LIMIT = 0.5f
private const val SURFACE_DELTA = 0.08f
private const val VARIANT_DELTA = 0.16f
private const val OUTLINE_DELTA = 0.28f
private const val TEXT_SATURATION = 0.3f
private const val MEDIUM_LIGHT_ON_DARK = 0.82f
private const val MEDIUM_LIGHT_ON_LIGHT = 0.22f
private const val LOW_LIGHT_ON_DARK = 0.65f
private const val LOW_LIGHT_ON_LIGHT = 0.38f
private const val ON_HIGH_DARK_BG = 0xFFFFFFFF
private const val ON_HIGH_LIGHT_BG = 0xFF101418

data class ThemePalette(
    val name: String,
    val colors: AureoleColors,
    val category: PaletteCategory = PaletteCategory.NEUTRAL,
)

/** Family of a preset palette, used to filter the palettes in the background color picker. */
enum class PaletteCategory { COLD, COOL, NEUTRAL, WARM }

/**
 * Builds the light variant of a dark palette by mirroring the tonal ramp.
 */
fun ThemePalette.toLight(): ThemePalette = ThemePalette(
    name = "$name Light",
    category = category,
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

val frostbitePalette = presetPalette(
    "Frostbite",
    PaletteCategory.COLD,
    "FFFFFF C5D8E8 9BB4C8 7D96AA 4A5D6B 2B373E 12181D"
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
    val hsl = FloatArray(HSL_SIZE)
    ColorUtils.colorToHSL(seedColorInt, hsl)
    val hue = hsl[0]
    val sat = hsl[1]
    val isDarkBg = ColorUtils.calculateLuminance(seedColorInt) < DARK_LUMINANCE_LIMIT

    val offset = if (isDarkBg) 1f else -1f
    fun adjustLightness(delta: Float) = (hsl[2] + offset * delta).coerceIn(0f, 1f)

    val surfaceColor = Color(ColorUtils.HSLToColor(floatArrayOf(hue, sat, adjustLightness(SURFACE_DELTA))))
    val surfaceVariantColor = Color(ColorUtils.HSLToColor(floatArrayOf(hue, sat, adjustLightness(VARIANT_DELTA))))
    val outlineColor = Color(ColorUtils.HSLToColor(floatArrayOf(hue, sat, adjustLightness(OUTLINE_DELTA))))

    val onHigh = if (isDarkBg) Color(ON_HIGH_DARK_BG) else Color(ON_HIGH_LIGHT_BG)
    val textSat = (sat * TEXT_SATURATION).coerceIn(0f, 1f)
    val onMedium = Color(
        ColorUtils.HSLToColor(floatArrayOf(hue, textSat, if (isDarkBg) MEDIUM_LIGHT_ON_DARK else MEDIUM_LIGHT_ON_LIGHT))
    )
    val onLow = Color(
        ColorUtils.HSLToColor(floatArrayOf(hue, textSat, if (isDarkBg) LOW_LIGHT_ON_DARK else LOW_LIGHT_ON_LIGHT))
    )

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
