package dev.mnascimentos.aureole.composable.clock

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.core.data.model.ClockFontChoice
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AvailableFonts

/** Compose family of this font; the launcher font is the family of the current theme. */
@Composable
@ReadOnlyComposable
internal fun ClockFontChoice.fontFamily(): FontFamily {
    // Read unconditionally: a theme read only in the launcher branch would change the reads when the font changes.
    val themeFamily = AureoleDS.typography.fontFamily
    return fontFamilyFor(themeFamily)
}

/** Compose family of this font, given the family of the current theme (also for a key the catalog lost). */
internal fun ClockFontChoice.fontFamilyFor(themeFamily: FontFamily): FontFamily =
    if (followsLauncher) themeFamily else AvailableFonts[fontKey] ?: themeFamily

/** Weight of the clock's time: bold when the bold switch is on. A font without a Bold file gets a synthetic one. */
internal val ClockFontChoice.timeWeight: FontWeight
    get() = if (isBold) FontWeight.Bold else FontWeight.Normal

/** Android [Typeface] of the clock's time, in [timeWeight]. */
@Composable
internal fun ClockFontChoice.timeTypeface(): Typeface {
    val resolver = LocalFontFamilyResolver.current
    val family = fontFamily()
    val weight = timeWeight
    return remember(family, weight, resolver) {
        resolver.resolve(family, weight).value as? Typeface ?: Typeface.DEFAULT
    }
}

/** Android [Typeface] of the clock's date, in the same family as the time but regular weight. */
@Composable
internal fun ClockFontChoice.dateTypeface(): Typeface {
    val resolver = LocalFontFamilyResolver.current
    val family = fontFamily()
    return remember(family, resolver) {
        resolver.resolve(family, FontWeight.Normal).value as? Typeface ?: Typeface.DEFAULT
    }
}
