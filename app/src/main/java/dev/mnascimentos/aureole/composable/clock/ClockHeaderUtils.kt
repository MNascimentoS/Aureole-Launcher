package dev.mnascimentos.aureole.composable.clock

import android.graphics.Typeface
import android.icu.text.DateTimePatternGenerator
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.composable.clock.model.clockDateSkeleton
import java.util.Locale

internal object ClockHeaderUtils {

    fun resolveClockTimeFormats(timeFormat: String): Pair<String, String> {
        return when (timeFormat.uppercase()) {
            "12H" -> Pair("hh:mm", "hh:mm")
            "24H" -> Pair("HH:mm", "HH:mm")
            else -> Pair("hh:mm", "HH:mm")
        }
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

    fun resolveClockTypeface(fontFamily: String): Typeface {
        return when (fontFamily.uppercase()) {
            "BOLD" -> Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
            "SERIF" -> Typeface.create(Typeface.SERIF, Typeface.BOLD)
            "MONOSPACE" -> Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            "ROUNDED" -> Typeface.create("sans-serif-rounded", Typeface.BOLD)
            else -> Typeface.create("sans-serif-medium", Typeface.BOLD)
        }
    }
}
