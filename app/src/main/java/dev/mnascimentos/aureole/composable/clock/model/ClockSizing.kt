package dev.mnascimentos.aureole.composable.clock.model

/** Share of each text's height trimmed from the font's empty bands (see `trimVerticalBands`). */
const val TIME_TRIM_TOP = 0.15f
const val TIME_TRIM_BOTTOM = 0.10f
const val DATE_TRIM_TOP = 0.15f
const val DATE_TRIM_BOTTOM = 0.10f
const val GREETING_TRIM_TOP = 0.15f
const val GREETING_TRIM_BOTTOM = 0.10f

private const val TIME_HEIGHT_FACTOR = 0.95f * (1f - TIME_TRIM_TOP - TIME_TRIM_BOTTOM)
private const val DATE_RESERVE_DP = 21f * (1f - DATE_TRIM_TOP - DATE_TRIM_BOTTOM)
private const val GREETING_RESERVE_DP = 23f * (1f - GREETING_TRIM_TOP - GREETING_TRIM_BOTTOM)
private const val LARGE_TIME_SIZE_PX = 68f
private const val MEDIUM_TIME_SIZE_PX = 56f
private val TIME_SIZES_PX = listOf(LARGE_TIME_SIZE_PX, MEDIUM_TIME_SIZE_PX)
private const val SMALLEST_TIME_SIZE_PX = 42f
private const val MIN_TIME_SIZE_PX = 12f

/**
 * What the clock header draws for a given height: the size of the time, whether the greeting fits, and the height
 * the lines take ([blockHeightDp], in dp with the font scale), which never goes over the height given when the time
 * can still shrink, so the time never covers the date or the message.
 */
data class ClockSizing(val textSizePx: Float, val showGreeting: Boolean, val blockHeightDp: Float = 0f)

/**
 * Time and date come first: picks the biggest time size whose block (time plus date, scaled by [fontScale]) fits in
 * [maxHeightDp]. When not even the smallest fixed size fits (a short landscape cell), the time takes the height
 * left by the date, down to a floor. The greeting only shows when the layout has one ([greetingAllowed]) and it
 * still fits at that size. [hasDate] is false for the layouts without a date.
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

    val fittedSize = (maxHeightDp / fontScale - dateReserve) / TIME_HEIGHT_FACTOR
    val size = TIME_SIZES_PX.firstOrNull { blockHeight(it, withGreeting = false) <= maxHeightDp }
        ?: fittedSize.coerceIn(MIN_TIME_SIZE_PX, SMALLEST_TIME_SIZE_PX)
    val showGreeting = greetingAllowed && blockHeight(size, withGreeting = true) <= maxHeightDp
    return ClockSizing(size, showGreeting, blockHeight(size, showGreeting))
}
