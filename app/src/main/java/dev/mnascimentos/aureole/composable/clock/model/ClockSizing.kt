package dev.mnascimentos.aureole.composable.clock.model

import dev.mnascimentos.aureole.core.data.model.ClockLine

/** Share of each text's height trimmed from the font's empty bands (see `trimVerticalBands`). */
const val TIME_TRIM_TOP = 0.15f
const val TIME_TRIM_BOTTOM = 0.10f
const val DATE_TRIM_TOP = 0.15f
const val DATE_TRIM_BOTTOM = 0.10f
const val GREETING_TRIM_TOP = 0.15f
const val GREETING_TRIM_BOTTOM = 0.10f

/** Height of each line before the trim, in ems of its own text size. */
private const val TIME_LINE_EM = 0.95f * (1f - TIME_TRIM_TOP - TIME_TRIM_BOTTOM)
private const val DATE_LINE_EM = 1.5f * (1f - DATE_TRIM_TOP - DATE_TRIM_BOTTOM)
private const val GREETING_LINE_EM = 23f / 15f * (1f - GREETING_TRIM_TOP - GREETING_TRIM_BOTTOM)

/** Width of the time ("00:00") in ems of its size, with room for the wider fonts of the catalog. */
private const val TIME_WIDTH_EM = 2.8f

/** Ceiling (the large size of `seletores-relogio`) and floor of the time size, in sp. */
const val CLOCK_MAX_TIME_SP = 68f
const val CLOCK_MIN_TIME_SP = 12f

/** Rounding room of a measured height (px to dp), so a card given its own height keeps its size. */
private const val FIT_TOLERANCE_DP = 1f
private const val SEARCH_STEPS = 24

/**
 * What the clock card draws in a given room: the size of the time ([textSizePx], in sp), whether the message
 * fits, the height of the whole card ([blockHeightDp], edges and spaces included, with the font scale) and the
 * other sizes, all shares of the time ([metrics]).
 */
data class ClockSizing(
    val textSizePx: Float,
    val showGreeting: Boolean,
    val blockHeightDp: Float = 0f,
    val metrics: ClockMetrics = clockMetrics(textSizePx)
)

/**
 * Sizes a clock card drawing [lines] (top to bottom) in a room [maxTextWidthDp] wide for the text and
 * [maxHeightDp] tall for the whole card (edges included): the time takes the biggest size, from
 * [CLOCK_MIN_TIME_SP] to [CLOCK_MAX_TIME_SP], whose card fits both (RF-B2), and every other size follows it
 * (D-08). Time and date come first: the message shows when the layout has one and it still fits with the time at
 * that size; otherwise it is dropped, so a taller card never makes the time smaller.
 */
fun resolveClockSizing(
    lines: List<ClockLine>,
    maxTextWidthDp: Float,
    maxHeightDp: Float,
    fontScale: Float = 1f
): ClockSizing {
    val ceiling = (maxTextWidthDp / (fontScale * TIME_WIDTH_EM)).coerceIn(CLOCK_MIN_TIME_SP, CLOCK_MAX_TIME_SP)
    val withoutMessage = lines.filter { it != ClockLine.MESSAGE }
    val size = fittedTimeSize(withoutMessage, ceiling, maxHeightDp, fontScale)
    val showGreeting = withoutMessage.size != lines.size &&
        clockCardHeightDp(lines, size, fontScale) <= maxHeightDp + FIT_TOLERANCE_DP
    val shown = if (showGreeting) lines else withoutMessage
    return ClockSizing(size, showGreeting, blockHeightDp = clockCardHeightDp(shown, size, fontScale))
}

/** Height of a clock card drawing [lines] with the time at [timeSp]: edges, trimmed lines and the spaces. */
fun clockCardHeightDp(lines: List<ClockLine>, timeSp: Float, fontScale: Float = 1f): Float {
    val metrics = clockMetrics(timeSp)
    val spacing = clockSpacing(lines)
    val text = lines.sumOf { line ->
        when (line) {
            ClockLine.TIME -> timeSp * TIME_LINE_EM
            ClockLine.DATE -> metrics.dateSp * DATE_LINE_EM
            ClockLine.MESSAGE -> metrics.greetingSp * GREETING_LINE_EM
        }.toDouble()
    }.toFloat() * fontScale
    val spaces = (spacing.between + spacing.above + spacing.below).sumOf { metrics.gapDp(it).toDouble() }.toFloat()
    return text + spaces
}

/** Biggest time size up to [ceiling] whose card fits [maxHeightDp]; the floor when not even it fits. */
private fun fittedTimeSize(lines: List<ClockLine>, ceiling: Float, maxHeightDp: Float, fontScale: Float): Float {
    fun fits(size: Float) = clockCardHeightDp(lines, size, fontScale) <= maxHeightDp + FIT_TOLERANCE_DP
    if (fits(ceiling)) return ceiling
    var low = CLOCK_MIN_TIME_SP
    var high = ceiling
    repeat(SEARCH_STEPS) {
        val middle = (low + high) / 2f
        if (fits(middle)) low = middle else high = middle
    }
    return low
}
