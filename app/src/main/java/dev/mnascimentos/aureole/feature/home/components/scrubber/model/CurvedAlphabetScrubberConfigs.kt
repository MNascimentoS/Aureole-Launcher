package dev.mnascimentos.aureole.feature.home.components.scrubber.model

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp

object ScrubberDimensions {
    val BadgeElevation = 6.dp
    val LabelOffset = 14.dp
    val BadgeMargin = 28.dp
    val BulgeMax = 56.dp
    val Spread = 80.dp
    val PaddingBottom = 112.dp
}

data class ScrubberCallbacks(
    val onLetterSelected: (Char) -> Unit,
    val onInteractionStarted: () -> Unit = {},
    val onInteractionEnded: () -> Unit = {}
)

data class ScrubberOptions(
    val isAlwaysVisible: Boolean = false,
    val isGestureEnabled: Boolean = true,
    val externalTouchY: Float = -1f
)

data class ScrubberRenderParams(
    val touchYPx: Float,
    val isDragging: Boolean,
    val selectedIndex: Int,
    val scrubberAlpha: Float,
    val alignment: Alignment
)
