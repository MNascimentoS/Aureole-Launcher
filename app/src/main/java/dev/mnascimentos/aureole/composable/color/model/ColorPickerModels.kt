package dev.mnascimentos.aureole.composable.color.model

import androidx.core.graphics.ColorUtils

private const val CUSTOM_SATURATION = 0.65f
private const val CUSTOM_LIGHTNESS = 0.45f
private const val HSL_COMPONENTS_COUNT = 3
private const val RGB_MASK = 0xFFFFFF

/** Maximum hue of the custom slider, in degrees. */
const val MAX_HUE = 360f

/** The 12 recommended colors of the picker (two rows of six). */
val RecommendedColors: List<Int> = listOf(
    0xFFFFFFFF.toInt(), 0xFF90CAF9.toInt(), 0xFFE1BEE7.toInt(), 0xFFFFE082.toInt(), 0xFFA5D6A7.toInt(),
    0xFFFFAB91.toInt(), 0xFF455A64.toInt(), 0xFFC62828.toInt(), 0xFF1E88E5.toInt(), 0xFF2E7D32.toInt(),
    0xFFF57F17.toInt(), 0xFF7E57C2.toInt()
)

/** Color shown when nothing else was chosen. */
val DefaultPickerColor: Int = RecommendedColors[1]

/** "#90CAF9": the color without alpha, in upper-case hex. */
fun formatHex(color: Int): String = "#%06X".format(color and RGB_MASK)

/** The color of the custom slider at [hue] degrees: the fixed saturation and lightness of the picker. */
fun colorForHue(hue: Float): Int =
    ColorUtils.HSLToColor(floatArrayOf(hue.coerceIn(0f, MAX_HUE), CUSTOM_SATURATION, CUSTOM_LIGHTNESS))

/** Hue, in degrees, of [color]; where the slider thumb sits for a color picked from the presets. */
fun hueOf(color: Int): Float {
    val hsl = FloatArray(HSL_COMPONENTS_COUNT)
    ColorUtils.colorToHSL(color, hsl)
    return hsl[0]
}
