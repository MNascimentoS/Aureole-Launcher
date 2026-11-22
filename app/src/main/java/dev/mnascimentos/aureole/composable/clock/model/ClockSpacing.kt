package dev.mnascimentos.aureole.composable.clock.model

import dev.mnascimentos.aureole.core.data.model.ClockLine

/**
 * A space of the clock card, by design token (`seletores-relogio` D-10), measured from the visible text: the lines
 * are trimmed of the font's empty bands (`trimVerticalBands`), so only these spaces sit between them.
 */
enum class ClockSpace {
    /** Room between the card's edge and the first or last line: `medium`. */
    EDGE,

    /** No space of its own between two lines. */
    NONE,

    /** The line above the time: one `xxSmall` more. */
    WIDER,

    /** The line below the time: one `xxSmall` less (the lines move closer). */
    TIGHTER
}

/**
 * The spaces of a clock card: [above] the first line, [between] each pair of lines (top to bottom, one less than
 * the lines) and [below] the last one.
 */
data class ClockSpacing(val above: ClockSpace, val between: List<ClockSpace>, val below: ClockSpace)

/**
 * The spaces of a clock drawing [lines], top to bottom (only the lines it shows). The top and the bottom are the
 * same; the line above the time gets a bit more room and the line below it a bit less, in every layout.
 */
fun clockSpacing(lines: List<ClockLine>): ClockSpacing = ClockSpacing(
    above = ClockSpace.EDGE,
    between = lines.zipWithNext { upper, lower ->
        when {
            lower == ClockLine.TIME -> ClockSpace.WIDER
            upper == ClockLine.TIME -> ClockSpace.TIGHTER
            else -> ClockSpace.NONE
        }
    },
    below = ClockSpace.EDGE
)
