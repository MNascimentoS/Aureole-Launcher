package dev.mnascimentos.aureole.composable.clock

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.composable.clock.model.ClockFontSpec
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

private val RoundedFontFamily: FontFamily by lazy { FontFamily(Typeface.create("sans-serif-rounded", Typeface.NORMAL)) }

/** Compose family of this option; [ClockFontSpec.THEME] is the family of the current theme. */
@Composable
@ReadOnlyComposable
internal fun ClockFontSpec.fontFamily(): FontFamily = when (this) {
    ClockFontSpec.THEME -> AureoleDS.typography.fontFamily
    ClockFontSpec.BOLD -> FontFamily.SansSerif
    ClockFontSpec.SERIF -> FontFamily.Serif
    ClockFontSpec.MONO -> FontFamily.Monospace
    ClockFontSpec.ROUNDED -> RoundedFontFamily
}

/** Android [Typeface] of the clock's time: the theme font for [ClockFontSpec.THEME], a system face otherwise. */
@Composable
internal fun ClockFontSpec.timeTypeface(): Typeface {
    val resolver = LocalFontFamilyResolver.current
    val themeFamily = AureoleDS.typography.fontFamily
    return remember(this, resolver, themeFamily) {
        if (this == ClockFontSpec.THEME) {
            resolver.resolve(themeFamily, FontWeight.Bold).value as? Typeface ?: Typeface.DEFAULT_BOLD
        } else {
            ClockHeaderUtils.resolveClockTypeface(storageKey)
        }
    }
}

/** Android [Typeface] of the clock's date, in the same family as the time but regular weight. */
@Composable
internal fun ClockFontSpec.dateTypeface(): Typeface {
    val resolver = LocalFontFamilyResolver.current
    val family = fontFamily()
    return remember(family, resolver) {
        resolver.resolve(family, FontWeight.Normal).value as? Typeface ?: Typeface.DEFAULT
    }
}
