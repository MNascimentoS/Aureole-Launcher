package dev.mnascimentos.aureole.feature.home.components.widgets.model

import android.os.Build
import androidx.annotation.RequiresApi

/** ARGB colors of the Aureole theme that stand in for the system's Material You colors inside widgets. */
data class WidgetPalette(
    val accent1: Int,
    val accent2: Int,
    val accent3: Int,
    val neutral1: Int,
    val neutral2: Int
)

private const val SHADE_MIN = 0
private const val SHADE_FAINT = 10
private const val SHADE_SOFT = 50
private const val SHADE_STEP = 100
private const val SHADE_MID = 500
private const val SHADE_MAX = 1000
private const val ALPHA_SHIFT = 24
private const val RED_SHIFT = 16
private const val GREEN_SHIFT = 8
private const val CHANNEL_MASK = 0xFF
private const val OPAQUE = 0xFF
private const val WHITE = 0xFFFFFFFF.toInt()
private const val BLACK = 0xFF000000.toInt()

/** The shades of a system tonal ramp, in the order of the resource lists below. */
private val SHADES = listOf(SHADE_MIN, SHADE_FAINT, SHADE_SOFT) +
    generateSequence(SHADE_STEP) { it + SHADE_STEP }.takeWhile { it < SHADE_MAX }.toList() + SHADE_MAX

private val ACCENT1_IDS = listOf(
    android.R.color.system_accent1_0,
    android.R.color.system_accent1_10,
    android.R.color.system_accent1_50,
    android.R.color.system_accent1_100,
    android.R.color.system_accent1_200,
    android.R.color.system_accent1_300,
    android.R.color.system_accent1_400,
    android.R.color.system_accent1_500,
    android.R.color.system_accent1_600,
    android.R.color.system_accent1_700,
    android.R.color.system_accent1_800,
    android.R.color.system_accent1_900,
    android.R.color.system_accent1_1000
)

private val ACCENT2_IDS = listOf(
    android.R.color.system_accent2_0,
    android.R.color.system_accent2_10,
    android.R.color.system_accent2_50,
    android.R.color.system_accent2_100,
    android.R.color.system_accent2_200,
    android.R.color.system_accent2_300,
    android.R.color.system_accent2_400,
    android.R.color.system_accent2_500,
    android.R.color.system_accent2_600,
    android.R.color.system_accent2_700,
    android.R.color.system_accent2_800,
    android.R.color.system_accent2_900,
    android.R.color.system_accent2_1000
)

private val ACCENT3_IDS = listOf(
    android.R.color.system_accent3_0,
    android.R.color.system_accent3_10,
    android.R.color.system_accent3_50,
    android.R.color.system_accent3_100,
    android.R.color.system_accent3_200,
    android.R.color.system_accent3_300,
    android.R.color.system_accent3_400,
    android.R.color.system_accent3_500,
    android.R.color.system_accent3_600,
    android.R.color.system_accent3_700,
    android.R.color.system_accent3_800,
    android.R.color.system_accent3_900,
    android.R.color.system_accent3_1000
)

private val NEUTRAL1_IDS = listOf(
    android.R.color.system_neutral1_0,
    android.R.color.system_neutral1_10,
    android.R.color.system_neutral1_50,
    android.R.color.system_neutral1_100,
    android.R.color.system_neutral1_200,
    android.R.color.system_neutral1_300,
    android.R.color.system_neutral1_400,
    android.R.color.system_neutral1_500,
    android.R.color.system_neutral1_600,
    android.R.color.system_neutral1_700,
    android.R.color.system_neutral1_800,
    android.R.color.system_neutral1_900,
    android.R.color.system_neutral1_1000
)

private val NEUTRAL2_IDS = listOf(
    android.R.color.system_neutral2_0,
    android.R.color.system_neutral2_10,
    android.R.color.system_neutral2_50,
    android.R.color.system_neutral2_100,
    android.R.color.system_neutral2_200,
    android.R.color.system_neutral2_300,
    android.R.color.system_neutral2_400,
    android.R.color.system_neutral2_500,
    android.R.color.system_neutral2_600,
    android.R.color.system_neutral2_700,
    android.R.color.system_neutral2_800,
    android.R.color.system_neutral2_900,
    android.R.color.system_neutral2_1000
)

/**
 * Maps every system accent/neutral color a widget may use to the matching tone of the Aureole [palette]: the lightest
 * shade is white, the darkest is black and the middle one is the palette color itself.
 */
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun buildWidgetColorMap(palette: WidgetPalette): Map<Int, Int> = buildMap {
    putRamp(ACCENT1_IDS, palette.accent1)
    putRamp(ACCENT2_IDS, palette.accent2)
    putRamp(ACCENT3_IDS, palette.accent3)
    putRamp(NEUTRAL1_IDS, palette.neutral1)
    putRamp(NEUTRAL2_IDS, palette.neutral2)
}

private fun MutableMap<Int, Int>.putRamp(ids: List<Int>, base: Int) {
    ids.zip(SHADES).forEach { (resId, shade) -> put(resId, toneOf(base, shade)) }
}

/** The [base] color lightened toward white below the middle shade and darkened toward black above it. */
fun toneOf(base: Int, shade: Int): Int = if (shade <= SHADE_MID) {
    mix(WHITE, base, shade.toFloat() / SHADE_MID)
} else {
    mix(base, BLACK, (shade - SHADE_MID).toFloat() / SHADE_MID)
}

private fun mix(from: Int, to: Int, ratio: Float): Int {
    fun channel(shift: Int): Int {
        val a = (from shr shift) and CHANNEL_MASK
        val b = (to shr shift) and CHANNEL_MASK
        return (a + (b - a) * ratio).toInt().coerceIn(0, CHANNEL_MASK)
    }
    return (OPAQUE shl ALPHA_SHIFT) or (channel(RED_SHIFT) shl RED_SHIFT) or
        (channel(GREEN_SHIFT) shl GREEN_SHIFT) or channel(0)
}
