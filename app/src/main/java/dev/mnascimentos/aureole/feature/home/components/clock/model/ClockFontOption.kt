package dev.mnascimentos.aureole.feature.home.components.clock.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.clock.model.ClockFontSpec

/** Columns of the font tiles. */
const val CLOCK_FONT_COLUMNS = 2

/** One tile of the font picker, in picker order. */
enum class ClockFontOption(val font: ClockFontSpec, @StringRes val labelRes: Int) {
    THEME(ClockFontSpec.THEME, R.string.home_clock_font_default),
    BOLD(ClockFontSpec.BOLD, R.string.home_clock_font_bold),
    SERIF(ClockFontSpec.SERIF, R.string.home_clock_font_serif),
    MONO(ClockFontSpec.MONO, R.string.home_clock_font_monospaced),
    ROUNDED(ClockFontSpec.ROUNDED, R.string.home_clock_font_rounded)
}
