package dev.mnascimentos.aureole.feature.home.components.model

import androidx.compose.ui.Alignment

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
