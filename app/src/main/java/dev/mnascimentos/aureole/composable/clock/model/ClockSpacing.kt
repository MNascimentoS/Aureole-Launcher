package dev.mnascimentos.aureole.composable.clock.model

import dev.mnascimentos.aureole.core.data.model.ClockLine
import kotlin.math.max
import kotlin.math.min

/*
 * Everything in the clock card is a share of the time size (relogio-home-sheet D-08), so the card keeps the same
 * organization at any size the user gives it on the grid. The shares are the tokens of `seletores-relogio` D-10 at
 * the large time size (68): `medium` 16 dp of edge, `xxSmall` 4 dp more above the time and 4 dp less below it,
 * `radiusLarge` 24 dp of corner, message 15 sp and date 14 sp.
 */
private const val REFERENCE_TIME_SP = 68f
private const val EDGE_SHARE = 16f / REFERENCE_TIME_SP
private const val WIDER_SHARE = 4f / REFERENCE_TIME_SP
private const val TIGHTER_SHARE = 4f / REFERENCE_TIME_SP
private const val RADIUS_SHARE = 24f / REFERENCE_TIME_SP
private const val GREETING_SHARE = 15f / REFERENCE_TIME_SP
private const val DATE_SHARE = 14f / REFERENCE_TIME_SP

/** Legibility floors: secondary text never under 11 sp, edge never under `xSmall` (8 dp), corner `radiusSmall`. */
const val CLOCK_MIN_TEXT_SP = 11f
const val CLOCK_MIN_EDGE_DP = 8f
const val CLOCK_MIN_RADIUS_DP = 12f

/**
 * Empty room the trimmed lines still keep around the glyphs, as a share of each text size: below the digits of
 * the time (descent minus the trimmed band) and above the date. The date is pulled up into that room, never past
 * [CLOCK_MIN_VISIBLE_GAP_DP], so it never touches the time (RF-B4).
 */
private const val TIME_BOTTOM_ROOM_SHARE = 0.12f
private const val DATE_TOP_ROOM_SHARE = 0.045f
const val CLOCK_MIN_VISIBLE_GAP_DP = 2f

/**
 * A space of the clock card (`seletores-relogio` D-10), measured from the visible text: the lines are trimmed of
 * the font's empty bands (`trimVerticalBands`), so only these spaces sit between them. Its size is a share of the
 * time size ([ClockMetrics.gapDp]).
 */
enum class ClockSpace {
    /** Room between the card's edge and the first or last line (`medium` at the large size). */
    EDGE,

    /** No space of its own between two lines. */
    NONE,

    /** The line above the time: a bit more room (`xxSmall` at the large size). */
    WIDER,

    /** The line below the time: a bit less room (the lines move closer, `xxSmall` at the large size). */
    TIGHTER
}

/**
 * Sizes of a clock card drawn with the time at [timeSp]: message and date sizes (sp), the edge room, the corner
 * radius and the two spaces around the time (dp; [tighterGapDp] is negative, it pulls the date up).
 */
data class ClockMetrics(
    val timeSp: Float,
    val greetingSp: Float,
    val dateSp: Float,
    val edgeDp: Float,
    val radiusDp: Float,
    val widerGapDp: Float,
    val tighterGapDp: Float
)

/** The [ClockMetrics] of a card whose time is [timeSp]: every size a share of it, with the legibility floors. */
fun clockMetrics(timeSp: Float): ClockMetrics {
    val dateSp = max(timeSp * DATE_SHARE, CLOCK_MIN_TEXT_SP)
    val room = timeSp * TIME_BOTTOM_ROOM_SHARE + dateSp * DATE_TOP_ROOM_SHARE - CLOCK_MIN_VISIBLE_GAP_DP
    val pull = min(timeSp * TIGHTER_SHARE, room).coerceAtLeast(0f)
    return ClockMetrics(
        timeSp = timeSp,
        greetingSp = max(timeSp * GREETING_SHARE, CLOCK_MIN_TEXT_SP),
        dateSp = dateSp,
        edgeDp = max(timeSp * EDGE_SHARE, CLOCK_MIN_EDGE_DP),
        radiusDp = max(timeSp * RADIUS_SHARE, CLOCK_MIN_RADIUS_DP),
        widerGapDp = timeSp * WIDER_SHARE,
        tighterGapDp = -pull
    )
}

/** Size of [space] in this card, in dp. */
fun ClockMetrics.gapDp(space: ClockSpace): Float = when (space) {
    ClockSpace.EDGE -> edgeDp
    ClockSpace.NONE -> 0f
    ClockSpace.WIDER -> widerGapDp
    ClockSpace.TIGHTER -> tighterGapDp
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
