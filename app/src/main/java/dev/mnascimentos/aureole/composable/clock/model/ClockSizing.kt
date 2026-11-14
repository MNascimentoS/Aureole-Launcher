package dev.mnascimentos.aureole.composable.clock.model

private const val MIN_GREETING_HEIGHT_DP = 100f
private const val TIME_HEIGHT_FACTOR = 0.95f
private const val DATE_RESERVE_DP = 20f
private const val GREETING_RESERVE_DP = 22f
private val TIME_SIZES_PX = listOf(68f, 56f, 42f)

/** What the clock header draws for a given height: the size of the time and whether the greeting fits. */
data class ClockSizing(val timeSizePx: Float, val showGreeting: Boolean)

/**
 * Picks the biggest time size whose block (time, date and, when shown, greeting) fits in [maxHeightDp],
 * or the smallest one. The greeting only shows from 100dp, in any style but `TIME_ONLY` and `DATE_ON_TOP`.
 */
fun chooseClockSizing(maxHeightDp: Float, clockStyle: String): ClockSizing {
    val hasGreetingStyle = clockStyle != "TIME_ONLY" && clockStyle != "DATE_ON_TOP"
    val showGreeting = hasGreetingStyle && maxHeightDp >= MIN_GREETING_HEIGHT_DP
    val reserve = (if (clockStyle == "TIME_ONLY") 0f else DATE_RESERVE_DP) +
        (if (showGreeting) GREETING_RESERVE_DP else 0f)
    val size = TIME_SIZES_PX.firstOrNull { it * TIME_HEIGHT_FACTOR + reserve <= maxHeightDp } ?: TIME_SIZES_PX.last()
    return ClockSizing(size, showGreeting)
}
