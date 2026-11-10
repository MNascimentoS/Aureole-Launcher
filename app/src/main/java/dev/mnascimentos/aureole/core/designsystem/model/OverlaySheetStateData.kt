package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.animation.AnimatedVisibilityScope

internal data class OverlaySheetStateData(
    val isExpanded: Boolean,
    val onExpandedChanged: (Boolean) -> Unit,
    val effectiveAnimated: Boolean,
    val triggerDismiss: () -> Unit,
    val animScope: AnimatedVisibilityScope
)
