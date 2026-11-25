package dev.mnascimentos.aureole.composable.clock

import android.icu.text.DateTimePatternGenerator
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.composable.clock.model.CLOCK_TIME_PATTERN_12H
import dev.mnascimentos.aureole.composable.clock.model.CLOCK_TIME_PATTERN_24H
import dev.mnascimentos.aureole.composable.clock.model.clockDateSkeleton
import java.util.Locale

internal object ClockHeaderUtils {

    /**
     * The `TextClock` patterns (12 h, 24 h) of [timeFormat]. The 12 h one has no leading zero and shows AM/PM
     * ("3:15 PM", onboarding-redesenho RF-C3), on the home, in the preview and in the pickers.
     */
    fun resolveClockTimeFormats(timeFormat: String): Pair<String, String> {
        return when (timeFormat.uppercase()) {
            "12H" -> Pair(CLOCK_TIME_PATTERN_12H, CLOCK_TIME_PATTERN_12H)
            "24H" -> Pair(CLOCK_TIME_PATTERN_24H, CLOCK_TIME_PATTERN_24H)
            else -> Pair(CLOCK_TIME_PATTERN_12H, CLOCK_TIME_PATTERN_24H)
        }
    }

    /** The one pattern the clock uses now: [timeFormat] resolved against the system 24 h setting. */
    fun resolveClockTimePattern(timeFormat: String, isSystem24Hour: Boolean): String {
        val (format12, format24) = resolveClockTimeFormats(timeFormat)
        return if (isSystem24Hour) format24 else format12
    }

    /** The date follows [locale] (order and language of weekday, day and month), so it never mixes languages. */
    fun resolveClockDatePattern(dateFormat: String, locale: Locale = Locale.getDefault()): String {
        if (dateFormat.equals("ISO", ignoreCase = true)) return "yyyy-MM-dd"
        return DateTimePatternGenerator.getInstance(locale).getBestPattern(clockDateSkeleton(dateFormat))
    }

    fun resolveClockAlignments(alignment: String): Pair<Alignment.Horizontal, TextAlign> {
        return when (alignment.uppercase()) {
            "CENTER" -> Pair(Alignment.CenterHorizontally, TextAlign.Center)
            "END", "RIGHT" -> Pair(Alignment.End, TextAlign.End)
            else -> Pair(Alignment.Start, TextAlign.Start)
        }
    }
}
