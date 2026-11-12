package dev.mnascimentos.aureole.feature.home.components.clock.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.clock.model.ClockDateFormat
import dev.mnascimentos.aureole.composable.clock.model.ClockTimeFormat

/** Columns of the hour tiles (System, 12 h, 24 h). */
const val CLOCK_TIME_COLUMNS = 3

/** Columns of the date tiles. */
const val CLOCK_DATE_COLUMNS = 2

/** One tile of the hour picker, in picker order. */
enum class ClockTimeOption(val format: ClockTimeFormat, @StringRes val labelRes: Int) {
    SYSTEM(ClockTimeFormat.SYSTEM, R.string.home_clock_time_system),
    H12(ClockTimeFormat.H12, R.string.home_clock_time_12h),
    H24(ClockTimeFormat.H24, R.string.home_clock_time_24h)
}

/** One tile of the date picker, in picker order. */
enum class ClockDateOption(val format: ClockDateFormat, @StringRes val labelRes: Int) {
    FULL(ClockDateFormat.FULL, R.string.home_clock_date_default),
    SHORT(ClockDateFormat.SHORT, R.string.home_clock_date_short),
    MEDIUM(ClockDateFormat.MEDIUM, R.string.home_clock_date_medium),
    NUMERIC(ClockDateFormat.NUMERIC, R.string.home_clock_date_numeric)
}
