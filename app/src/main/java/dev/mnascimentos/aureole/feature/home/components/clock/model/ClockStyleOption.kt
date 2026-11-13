package dev.mnascimentos.aureole.feature.home.components.clock.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R

/** One line of the miniature drawn in a style card: a small text line or the big time block. */
enum class ClockStyleLine { SMALL, LARGE }

/** Cards per row of the style picker. */
const val CLOCK_STYLE_COLUMNS = 3

/**
 * The clock styles the user can pick, in the order of the picker.
 *
 * @property key Value stored in the settings (`clockStyle`).
 * @property labelRes Label under the miniature.
 * @property lines Lines of the miniature, top to bottom.
 */
enum class ClockStyleOption(
    val key: String,
    @StringRes val labelRes: Int,
    val lines: List<ClockStyleLine>
) {
    DATE_ON_TOP("DATE_ON_TOP", R.string.home_clock_date_on_top, listOf(ClockStyleLine.SMALL, ClockStyleLine.LARGE)),
    TIME_ONLY("TIME_ONLY", R.string.home_clock_time_only, listOf(ClockStyleLine.LARGE)),
    GREETING_AND_DATE(
        "GREETING_AND_DATE",
        R.string.home_clock_greeting_and_date,
        listOf(ClockStyleLine.SMALL, ClockStyleLine.LARGE, ClockStyleLine.SMALL)
    ),
    DYNAMIC_GREETING(
        "DYNAMIC_GREETING",
        R.string.home_clock_daily_greeting,
        listOf(ClockStyleLine.SMALL, ClockStyleLine.LARGE)
    ),
    CUSTOM_GREETING(
        "CUSTOM_GREETING",
        R.string.home_clock_custom_text,
        listOf(ClockStyleLine.SMALL, ClockStyleLine.LARGE)
    );

    companion object {
        /** The options split into picker rows of [CLOCK_STYLE_COLUMNS] cards. */
        val rows: List<List<ClockStyleOption>> = entries.chunked(CLOCK_STYLE_COLUMNS)

        /** The option stored as [key], or null for an unknown style. */
        fun fromKey(key: String): ClockStyleOption? = entries.firstOrNull { it.key == key }
    }
}

/** An alignment of the clock inside its card, in the order of the segmented control. */
enum class ClockAlignmentOption(val key: String, @StringRes val labelRes: Int) {
    START("START", R.string.home_clock_align_left),
    CENTER("CENTER", R.string.home_clock_align_center),
    END("END", R.string.home_clock_align_right);

    companion object {
        /** Segment to highlight for the stored alignment; -1 when it matches none. */
        fun indexOfKey(key: String): Int = entries.indexOfFirst { it.key == key }
    }
}
