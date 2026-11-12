package dev.mnascimentos.aureole.composable.clock

import androidx.annotation.StringRes
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.R
import java.util.Calendar

private const val MORNING_HOUR_START = 5
private const val MORNING_HOUR_END = 11
private const val AFTERNOON_HOUR_START = 12
private const val AFTERNOON_HOUR_END = 17

/*
 * Pure helpers of ClockHeader. Every theme value they need is read by ClockHeader itself, unconditionally,
 * so that toggling glass, background or style never changes which composition locals a recomposition reads.
 */

/** Greeting string for [hour] (0–23): morning 5–11, afternoon 12–17, evening otherwise. */
@StringRes
internal fun greetingRes(hour: Int): Int = when (hour) {
    in MORNING_HOUR_START..MORNING_HOUR_END -> R.string.home_clock_greeting_morning
    in AFTERNOON_HOUR_START..AFTERNOON_HOUR_END -> R.string.home_clock_greeting_afternoon
    else -> R.string.home_clock_greeting_evening
}

/** Hour of the day (0–23) at [timeMillis] in the default time zone. */
internal fun hourOfDay(timeMillis: Long = System.currentTimeMillis()): Int =
    Calendar.getInstance().apply { this.timeInMillis = timeMillis }.get(Calendar.HOUR_OF_DAY)

/** Background behind the clock: transparent when off, [surfaceColor] unless a custom [bgColorInt] is set. */
internal fun resolveClockBgColor(
    isBgEnabled: Boolean,
    isHazeEnabled: Boolean,
    bgColorInt: Int,
    hazeOpacity: Float,
    surfaceColor: Color
): Color {
    if (!isBgEnabled) return Color.Transparent
    val baseBgColor = if (bgColorInt != 0) Color(bgColorInt) else surfaceColor
    return if (isHazeEnabled) baseBgColor.copy(alpha = hazeOpacity) else baseBgColor
}

/** Glass of the clock; returns this modifier untouched when the glass is off or there is nothing to blur. */
internal fun Modifier.clockHazeEffect(
    isHazeEnabled: Boolean,
    hazeStateRef: HazeState?,
    hazeOpacity: Float,
    blurRadius: Dp,
    tintColor: Color
): Modifier {
    if (!isHazeEnabled || hazeStateRef == null) return this
    return this.hazeEffect(
        state = hazeStateRef,
        style = HazeStyle(
            blurRadius = blurRadius,
            tint = HazeTint(tintColor.copy(alpha = hazeOpacity))
        )
    ) {
        blurEnabled = true
    }
}
