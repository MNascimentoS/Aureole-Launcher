package dev.mnascimentos.aureole.feature.home.components.widgets.model

import android.appwidget.AppWidgetHost
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.LauncherItemState

object WidgetPickerMetrics {
    val SearchTopPaddingExpanded = 80.dp
    val ListBottomPadding = 120.dp

    /** Small enough that about three cards and the "+" card fit across the live preview. */
    val PreviewCardWidth = 110.dp
    val PreviewCardHeight = 90.dp
    val AddCardSize = 164.dp
}

private const val DEFAULT_HAZE_OPACITY = 0.5f

/**
 * Parameters for the reorderable widget preview in [EditWidgetGroupScreen]: [onMoveWidget] runs at every swap
 * while a card is dragged and [onMoveEnd] once when the drag that moved it ends.
 */
data class EditWidgetCarouselParams(
    val currentWidgets: List<Int>,
    val appWidgetHost: AppWidgetHost?,
    val scrollState: ScrollState,
    val isIndicatorEnabled: Boolean,
    val onRemoveWidget: (Int) -> Unit,
    val onAddWidgetClick: () -> Unit,
    val onMoveWidget: (Int, Int) -> Unit = { _, _ -> },
    val onMoveEnd: () -> Unit = {}
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
    /** The new order, once per drag (or accessibility move) that changed it. */
    val onReorderWidgets: (List<Int>) -> Unit = {},
    /** Opens the Aureole settings from the sheet's header logo; null hides the logo. */
    val onSettingsClick: (() -> Unit)? = null
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

data class StackedWidgetPagerParams(
    val config: StackedWidgetConfig,
    val widgetIds: List<Int>,
    val appWidgetHost: AppWidgetHost? = null,
    val stackId: String,
    val params: PagerContentParams
)
