package dev.mnascimentos.aureole.feature.home.components.widgets.model

import android.appwidget.AppWidgetHost
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.LauncherItemState

object WidgetPickerMetrics {
    val SearchTopPaddingExpanded = 80.dp
    val ListBottomPadding = 120.dp
    val PreviewCardSize = 150.dp
    val AddCardSize = 164.dp
}

private const val DEFAULT_HAZE_OPACITY = 0.5f

/**
 * Parameters for the widget carousel preview in [EditWidgetGroupScreen].
 */
data class EditWidgetCarouselParams(
    val currentWidgets: List<Int>,
    val appWidgetHost: AppWidgetHost?,
    val listState: LazyListState,
    val currentPage: Int,
    val isIndicatorEnabled: Boolean,
    val onRemoveWidget: (Int) -> Unit,
    val onAddWidgetClick: () -> Unit,
    val onMoveWidget: ((Int, Int) -> Unit)? = null
)

data class EditWidgetGroupParams(
    val item: LauncherItemState,
    val widgets: List<Int>,
    val appWidgetHost: AppWidgetHost? = null,
    val isIndicatorEnabled: Boolean = true,
    val hazeState: HazeState? = null
)

data class EditWidgetGroupActions(
    val onToggleIndicator: () -> Unit,
    val onRemoveWidget: (Int) -> Unit,
    val onAddWidgetClick: () -> Unit,
    val onDismissRequest: () -> Unit,
    val onReorderWidgets: (List<Int>) -> Unit = {}
)

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

data class PagerContentParams(
    val showAddButton: Boolean,
    val pageCount: Int,
    val pagerState: PagerState,
    val hasFillMaxSize: Boolean
)

data class WidgetStackBottomSheetParams(
    val widgetId: Int?,
    val stackId: String,
    val onEditStack: () -> Unit,
    val onToggleDots: (String) -> Unit,
    val onRemoveStack: () -> Unit,
)
