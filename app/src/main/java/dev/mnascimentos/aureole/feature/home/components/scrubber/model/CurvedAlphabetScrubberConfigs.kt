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
    val hasPillBackground: Boolean = false
)

data class ScrubberRenderParams(
    val touchYPx: Float,
    val isDragging: Boolean,
    val selectedIndex: Int,
    val scrubberAlpha: Float,
    val alignment: Alignment
)

/**
 * Alpha of one letter of the strip. The selected letter is drawn by the badge while a drag runs, so the strip
 * hides its own copy and the letter shows once, in the right place, instead of twice on top of each other.
 */
fun scrubberLetterAlpha(isSelected: Boolean, isDragging: Boolean, scrubberAlpha: Float): Float =
    if (isSelected && isDragging) 0f else scrubberAlpha
