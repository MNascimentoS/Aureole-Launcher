package dev.mnascimentos.aureole.composable.clock.model

private const val TIME_HEIGHT_FACTOR = 0.95f
private const val DATE_RESERVE_DP = 20f
private const val GREETING_RESERVE_DP = 22f
private val TIME_SIZES_PX = listOf(68f, 56f)
private const val SMALLEST_TIME_SIZE_PX = 42f

/** What the clock header draws for a given height: the size of the time and whether the greeting fits. */
data class ClockSizing(val textSizePx: Float, val showGreeting: Boolean)

/**
 * Time and date come first: picks the biggest time size whose block (time plus date, scaled by [fontScale]) fits in
 * [maxHeightDp], or the smallest one. The greeting only shows when the style allows it ([greetingAllowed]) and it
 * still fits at that size. [hasDate] is false for the time-only style.
 */
fun resolveClockSizing(
    maxHeightDp: Float,
    fontScale: Float,
    greetingAllowed: Boolean,
    hasDate: Boolean = true
): ClockSizing {
    val dateReserve = if (hasDate) DATE_RESERVE_DP else 0f
    fun blockHeight(sizePx: Float, withGreeting: Boolean): Float =
        (sizePx * TIME_HEIGHT_FACTOR + dateReserve + if (withGreeting) GREETING_RESERVE_DP else 0f) * fontScale

    val size = TIME_SIZES_PX.firstOrNull { blockHeight(it, withGreeting = false) <= maxHeightDp }
        ?: SMALLEST_TIME_SIZE_PX
    val showGreeting = greetingAllowed && blockHeight(size, withGreeting = true) <= maxHeightDp
    return ClockSizing(size, showGreeting)
}
