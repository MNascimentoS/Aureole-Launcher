package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.animation.AnimatedVisibilityScope

internal data class OverlaySheetStateData(
    val dragExpansion: Float,
    val onDragExpansionChanged: (Float) -> Unit,
    val effectiveAnimated: Boolean,
    val triggerDismiss: () -> Unit,
    val animScope: AnimatedVisibilityScope
)
