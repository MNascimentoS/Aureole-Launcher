package dev.mnascimentos.aureole.feature.home.components.scrubber.model

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ScrubberDimensions {
    val BadgeElevation = 6.dp
    val LabelOffset = 14.dp
    val BadgeMargin = 28.dp
    val BulgeMax = 56.dp
    val Spread = 80.dp
    val PaddingBottom = 112.dp

    /** Share of the screen height the scrubber column takes, anchored at the bottom. */
    const val SCRUBBER_HEIGHT_FRACTION = 0.55f

    /** Width of the screen edge that starts a scrub; the coach's finger enters by it too. */
    val EdgeGestureStartWidth = 32.dp

    /** Large letter badge of the drawer scrub (RF-21). */
    val ProminentBadgeSize = 76.dp

    /** Gap from the alphabet edge of the column to the near side of the large badge, clear of the bulged letters. */
    val ProminentBadgeInset = 96.dp

    /** How far above the finger the center of the large badge sits, so the thumb never covers it. */
    val ProminentBadgeLift = 56.dp

    /** Spread of the letter growth around the finger: tighter than [Spread], so only the neighbours grow. */
    val GrowthSpread = 24.dp
}

/** Motion of the drawer scrub highlight; layout tokens are never used for motion. */
object ScrubberMotion {
    const val BADGE_ENTER_MS = 150
    const val BADGE_EXIT_MS = 180
    const val BADGE_START_SCALE = 0.6f
    const val LETTER_SWAP_MS = 90
    const val LETTER_SWAP_START_SCALE = 0.8f

    /** Scale of the letter under the finger in the strip. */
    const val SELECTED_LETTER_SCALE = 1.8f

    /** Extra scale the neighbours get at most; it fades with [ScrubberDimensions.GrowthSpread]. */
    const val NEIGHBOUR_GROWTH = 0.4f
}

data class ScrubberCallbacks(
    val onLetterSelected: (Char) -> Unit,
    val onInteractionStarted: () -> Unit = {},
    val onInteractionEnded: () -> Unit = {}
)

data class ScrubberOptions(
    val isAlwaysVisible: Boolean = false,
    val isGestureEnabled: Boolean = true,
    val externalTouchY: Float = -1f,
    /** The onboarding teaches the scrub: show the letters faintly without any touch. */
    val isHintVisible: Boolean = false,
    /** Share of the parent height the letters take; the home keeps [ScrubberDimensions.SCRUBBER_HEIGHT_FRACTION]. */
    val heightFraction: Float = ScrubberDimensions.SCRUBBER_HEIGHT_FRACTION,
    /** Space kept under the letters; the home leaves room for its dock. */
    val bottomPadding: Dp = ScrubberDimensions.PaddingBottom,
    /** Draws a narrow pill behind the letters (the folder picker); the touch area keeps its full width. */
    val hasPillBackground: Boolean = false,
    /**
     * The drawer highlight (RF-21): large badge inward of and above the finger, letters growing around it and a
     * light tick on each letter. Off keeps the small badge on the letter (pickers, which tick on their own).
     */
    val isProminentSelection: Boolean = false
)

data class ScrubberRenderParams(
    val touchYPx: Float,
    val isDragging: Boolean,
    val selectedIndex: Int,
    val scrubberAlpha: Float,
    val alignment: Alignment,
    val isProminentSelection: Boolean = false
)

/**
 * Alpha of one letter of the strip. The small badge is drawn on the selected letter while a drag runs, so the strip
 * hides its own copy and the letter shows once, in the right place, instead of twice on top of each other. The large
 * badge sits away from the strip ([isBadgeOnLetter] false), so the strip keeps the letter, grown.
 */
fun scrubberLetterAlpha(
    isSelected: Boolean,
    isDragging: Boolean,
    scrubberAlpha: Float,
    isBadgeOnLetter: Boolean = true
): Float = if (isSelected && isDragging && isBadgeOnLetter) 0f else scrubberAlpha

/** Scale of one letter of the strip [distancePx] from the finger: the selected one biggest, the neighbours fading. */
fun scrubberLetterScale(distancePx: Float, growthSpreadPx: Float, isSelected: Boolean): Float =
    if (isSelected) {
        ScrubberMotion.SELECTED_LETTER_SCALE
    } else {
        1f + ScrubberMotion.NEIGHBOUR_GROWTH * scrubberBulgeWeight(distancePx, growthSpreadPx)
    }

/** Top-left offset of the large badge from the column it is aligned in, as (x, y) in px. */
data class ProminentBadgeOffset(val xPx: Float, val yPx: Float)

/**
 * Places the large badge [liftPx] above the finger at [touchYPx] and [insetPx] inward of the alphabet edge of the
 * column: to the left for the right-handed layout, to the right when [isLeftHanded].
 */
fun prominentBadgeOffset(
    touchYPx: Float,
    badgeSizePx: Float,
    insetPx: Float,
    liftPx: Float,
    isLeftHanded: Boolean
): ProminentBadgeOffset = ProminentBadgeOffset(
    xPx = if (isLeftHanded) insetPx else -insetPx,
    yPx = touchYPx - liftPx - badgeSizePx / 2f
)

/** Hands each new letter to the scrubber callbacks once ([report]) and forgets the last one when a scrub ends. */
data class LetterReporter(
    val report: (Char?) -> Unit,
    val reset: () -> Unit
)
