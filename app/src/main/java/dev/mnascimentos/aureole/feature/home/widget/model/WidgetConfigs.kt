package dev.mnascimentos.aureole.feature.home.widget.model

import androidx.compose.ui.unit.Dp
import dev.chrisbanes.haze.HazeState

private const val DEFAULT_HAZE_OPACITY = 0.5f

data class StackedWidgetConfig(
    val topWidgetIds: List<Int>,
    val currentHeightDp: Dp,
    val currentHeightPx: Float,
    val showWidgetDots: Boolean = true,
    val hazeState: HazeState? = null
)

data class OpenedWidgetPopupConfig(
    val onDismiss: () -> Unit,
    val onResizeClick: () -> Unit,
    val onRemoveClick: () -> Unit,
    val hazeState: HazeState? = null,
    val isHazeEnabled: Boolean = false,
    val hazeOpacity: Float = DEFAULT_HAZE_OPACITY
)

data class WidgetItemActions(
    val onOpenWidgetPopup: (Int, Float) -> Unit,
    val onRemoveClick: (Int) -> Unit
)
