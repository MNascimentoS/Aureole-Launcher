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
    val selectedWidgetId: Int? = null,
    /** Changes when the selected card must be scrolled into view (a move by button), not while dragging. */
    val revealToken: Int = 0,
    val onSelectWidget: (Int) -> Unit = {},
    val onMoveWidget: (Int, Int) -> Unit = { _, _ -> },
    val onMoveEnd: () -> Unit = {}
)

/**
 * What the "Move <widget>" row of the stack editor shows: the selected widget, its 1-based [position] among [count]
 * and the two steps; a null step means the widget already sits at that end and its button is disabled.
 */
data class WidgetMoveParams(
    val name: String,
    val position: Int,
    val count: Int,
    val onPrevious: (() -> Unit)?,
    val onNext: (() -> Unit)?
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
    val showIndicator: Boolean = true,
    /** Package that owns the widget; looked up from [widgetId] when null. */
    val appPackage: String? = null,
)

data class StackedWidgetPagerParams(
    val config: StackedWidgetConfig,
    val widgetIds: List<Int>,
    val appWidgetHost: AppWidgetHost? = null,
    val stackId: String,
    val params: PagerContentParams
)

/** What the editor sheet shows: the [widgets] in order, the [selected] one and the indicator switch. */
data class EditWidgetSheetParams(
    val widgets: List<Int>,
    val selected: Int?,
    val isIndicatorEnabled: Boolean
)
