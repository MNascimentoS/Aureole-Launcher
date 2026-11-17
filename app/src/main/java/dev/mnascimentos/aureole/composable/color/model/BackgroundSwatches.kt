package dev.mnascimentos.aureole.composable.color.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.ColorUtils
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.palette.PaletteCategory
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleColors

private const val RAMP_SATURATION = 0.32f
private const val LIGHT_LUMINANCE = 0.5f
private const val RAMP_L0 = 0.08f
private const val RAMP_L1 = 0.14f
private const val RAMP_L2 = 0.22f
private const val RAMP_L3 = 0.32f
private const val RAMP_L4 = 0.45f
private const val RAMP_L5 = 0.62f
private const val RAMP_L6 = 0.80f
private val RampLightness = floatArrayOf(RAMP_L0, RAMP_L1, RAMP_L2, RAMP_L3, RAMP_L4, RAMP_L5, RAMP_L6)

/** A color of the current theme offered as a background, with the name of its role. */
data class ThemeSwatch(@StringRes val labelRes: Int, val color: Int)

/** A color taken from a named palette. */
data class PaletteSwatch(val paletteName: String, val color: Int)

/** Which tone of each palette becomes the background color (D-07: the variant, `surfaceVariant`). */
enum class PaletteTone { BACKGROUND, SURFACE, VARIANT }

/** Background, surface, variant and outline of the current theme, in that order. */
fun themeSwatches(colors: AureoleColors): List<ThemeSwatch> = listOf(
    ThemeSwatch(R.string.settings_background_swatch_background, colors.background.toArgb()),
    ThemeSwatch(R.string.settings_background_swatch_surface, colors.surface.toArgb()),
    ThemeSwatch(R.string.settings_background_swatch_variant, colors.surfaceVariant.toArgb()),
    ThemeSwatch(R.string.settings_background_swatch_outline, colors.outline.toArgb())
)

private fun ThemePalette.toneOf(tone: PaletteTone): Int = when (tone) {
    PaletteTone.BACKGROUND -> colors.background
    PaletteTone.SURFACE -> colors.surface
    PaletteTone.VARIANT -> colors.surfaceVariant
}.toArgb()

/** One [tone] of every palette of [category], each palette once, in catalog order. */
fun paletteSwatches(
    category: PaletteCategory,
    tone: PaletteTone = PaletteTone.VARIANT,
    palettes: List<ThemePalette> = allPalettes
): List<PaletteSwatch> = palettes
    .filter { it.category == category }
    .distinctBy { it.name }
    .map { PaletteSwatch(it.name, it.toneOf(tone)) }

/** Seven tones of [hue], from the darkest to the lightest, with the same low saturation. */
fun toneRamp(hue: Float): List<Int> = RampLightness.map {
    ColorUtils.HSLToColor(floatArrayOf(hue.coerceIn(0f, MAX_HUE), RAMP_SATURATION, it))
}

/** True when [color] as a background makes a light theme (same rule as the seed theme builder). */
fun isLightBackground(color: Int): Boolean = ColorUtils.calculateLuminance(color) >= LIGHT_LUMINANCE

/** The category open on the sheet: the one of the palette whose tone is [color], else cold. */
fun initialPaletteCategory(
    color: Int,
    tone: PaletteTone = PaletteTone.VARIANT,
    palettes: List<ThemePalette> = allPalettes
): PaletteCategory = palettes.firstOrNull { it.toneOf(tone) == color }?.category ?: PaletteCategory.COLD
