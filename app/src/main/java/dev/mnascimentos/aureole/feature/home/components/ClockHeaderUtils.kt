package dev.mnascimentos.aureole.feature.home.components

import android.graphics.Typeface
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign

internal object ClockHeaderUtils {

    fun resolveClockTimeFormats(timeFormat: String): Pair<String, String> {
        return when (timeFormat.uppercase()) {
            "12H" -> Pair("hh:mm", "hh:mm")
            "24H" -> Pair("HH:mm", "HH:mm")
            else -> Pair("hh:mm", "HH:mm")
        }
    }

    fun resolveClockDatePattern(dateFormat: String): String {
        return when (dateFormat.uppercase()) {
            "SHORT" -> "EEE, d MMM"
            "NUMERIC" -> "dd/MM/yyyy"
            "ISO" -> "yyyy-MM-dd"
            else -> "EEEE, d 'de' MMMM"
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
