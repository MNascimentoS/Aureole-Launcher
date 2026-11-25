package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.composable.clock.ClockHeaderUtils
import dev.mnascimentos.aureole.composable.clock.model.formatClockSample
import java.util.Locale

/**
 * The time on the mini-home of the Visual sheet (RF-C1, D-06): [nowMillis] written as the user's clock writes it,
 * so "3:15 PM" in 12 h (no leading zero, RF-C3) and "15:15" in 24 h.
 */
internal fun visualPreviewTime(
    timeFormat: String,
    isSystem24Hour: Boolean,
    locale: Locale,
    nowMillis: Long
): String = formatClockSample(ClockHeaderUtils.resolveClockTimePattern(timeFormat, isSystem24Hour), locale, nowMillis)
