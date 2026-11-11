package dev.mnascimentos.aureole.composable.clock

import android.graphics.Typeface
import android.icu.text.DateTimePatternGenerator
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import java.util.Locale

internal object ClockHeaderUtils {

    fun resolveClockTimeFormats(timeFormat: String): Pair<String, String> {
        return when (timeFormat.uppercase()) {
            "12H" -> Pair("hh:mm", "hh:mm")
            "24H" -> Pair("HH:mm", "HH:mm")
            else -> Pair("hh:mm", "HH:mm")
        }
    }

    private const val LONG_DATE_SKELETON = "EEEEdMMMM"

    /** The default date follows [locale] (weekday, day, month), so the example never mixes languages. */
    fun resolveClockDatePattern(dateFormat: String, locale: Locale = Locale.getDefault()): String {
        return when (dateFormat.uppercase()) {
            "SHORT" -> "EEE, d MMM"
            "NUMERIC" -> "dd/MM/yyyy"
            "ISO" -> "yyyy-MM-dd"
            else -> DateTimePatternGenerator.getInstance(locale).getBestPattern(LONG_DATE_SKELETON)
        }
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
