package dev.mnascimentos.aureole.feature.home.components.clock.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.ClockLayout
import dev.mnascimentos.aureole.core.data.model.ClockMessage

/** Cards per row of the layout picker. */
const val CLOCK_LAYOUT_COLUMNS = 3

/** The clock layouts the user can pick in Style › Layout, in the order of the picker, with their labels. */
enum class ClockLayoutOption(val layout: ClockLayout, @StringRes val labelRes: Int) {
    TIME_ONLY(ClockLayout.TIME_ONLY, R.string.home_clock_time_only),
    DATE_ON_TOP(ClockLayout.DATE_ON_TOP, R.string.home_clock_date_on_top),
    TIME_ON_TOP(ClockLayout.TIME_ON_TOP, R.string.home_clock_layout_time_on_top),
    DATE_TIME_MSG(ClockLayout.DATE_TIME_MSG, R.string.home_clock_layout_date_time_msg),
    MSG_TIME_DATE(ClockLayout.MSG_TIME_DATE, R.string.home_clock_layout_msg_time_date),
    MSG_ON_TOP(ClockLayout.MSG_ON_TOP, R.string.home_clock_layout_msg_on_top),
    MSG_ON_BOTTOM(ClockLayout.MSG_ON_BOTTOM, R.string.home_clock_layout_msg_on_bottom);

    /** Value stored in the settings and in the clock config (`style`). */
    val key: String get() = layout.key

    companion object {
        /** The options split into picker rows of [CLOCK_LAYOUT_COLUMNS] cards. */
        val rows: List<List<ClockLayoutOption>> = entries.chunked(CLOCK_LAYOUT_COLUMNS)
    }
}

/** What the message line shows, in the order of the Style › Message segmented control. */
enum class ClockMessageOption(val message: ClockMessage, @StringRes val labelRes: Int) {
    GREETING(ClockMessage.GREETING, R.string.home_clock_daily_greeting),
    CUSTOM(ClockMessage.CUSTOM, R.string.home_clock_custom_text);

    companion object {
        /** Segment to highlight for the stored message; an unknown one reads as the daily greeting. */
        fun indexOfKey(key: String): Int = ClockMessage.fromKey(key).let { message ->
            entries.indexOfFirst { it.message == message }
        }
    }
}
