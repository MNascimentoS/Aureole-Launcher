package dev.mnascimentos.aureole.feature.home.model

import android.appwidget.AppWidgetHost
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Density
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.widgets.model.StackedWidgetConfig
import kotlinx.coroutines.CoroutineScope

data class HomeDragParams(
    val isLeftHandedMode: Boolean,
    val isAlphabetScrubberDisabled: Boolean = false,
    val screenHeightPx: Float,
    val screenWidthPx: Float,
    val isAllAppsDrawerOpen: Boolean,
    val density: Density,
    val onAllAppsDrawerOpen: () -> Unit,
    val onExpandNotificationShade: () -> Unit,
    /** Y of the finger inside the letters column while an edge scrub runs; -1 when it ends. */
    val onExternalTouchYChange: (Float) -> Unit,
    /** An edge scrub started (true) or ended (false): the home keeps this modifier on while it runs. */
    val onEdgeScrubChange: (Boolean) -> Unit = {},
)

data class ScrubberOverlayConfig(
    val uiState: MainUiState,
    val externalTouchY: Float,
    val listState: LazyListState,
    val coroutineScope: CoroutineScope,
    val actions: HomeScreenActions,
    val onExternalTouchYReset: () -> Unit,
)

data class HomeOverlaysConfig(
    val uiState: MainUiState,
    val actions: HomeScreenActions,
    val listState: LazyListState,
    val hazeState: HazeState,
    val externalTouchY: Float,
    val coroutineScope: CoroutineScope,
    val screenHeightPx: Float,
    val appWidgetHost: AppWidgetHost? = null,
    val onExternalTouchYReset: () -> Unit
)

data class HomeScreenBodyConfig(
    val uiState: MainUiState,
    val actions: HomeScreenActions,
    val appWidgetHost: AppWidgetHost? = null,
    val currentHeightPx: Float,
    val favListState: LazyListState,
    val listState: LazyListState,
    val hazeState: HazeState,
    val externalTouchY: Float,
    val coroutineScope: CoroutineScope,
    val screenHeightPx: Float,
    val onExternalTouchYReset: () -> Unit
)

data class AppsDrawerOverlayConfig(
    val isAllAppsDrawerOpen: Boolean,
    val isOpenedFromBottom: Boolean,
    val isLeftHandedMode: Boolean,
    val listState: LazyListState,
    val hazeState: HazeState,
    val onClose: () -> Unit,
)

data class GridItemContentParams(
    val item: LauncherItemState,
    val favConfig: FavoritesListConfig,
    val appWidgetHost: AppWidgetHost?,
    val stackedWidgetConfig: StackedWidgetConfig,
    val hazeState: HazeState?,
    val isInScrollView: Boolean = false
)

data class VerticalScrollViewContentParams(
    val item: LauncherItemState,
    val favConfig: FavoritesListConfig,
    val appWidgetHost: AppWidgetHost?,
    val stackedWidgetConfig: StackedWidgetConfig,
    val hazeState: HazeState?,
    val scrollState: ScrollState
)

data class ScrollViewContentParams(
    val item: LauncherItemState,
    val favConfig: FavoritesListConfig,
    val appWidgetHost: AppWidgetHost?,
    val stackedWidgetConfig: StackedWidgetConfig,
    val hazeState: HazeState?,
    val scrollState: ScrollState? = null
)

data class ScrollViewChildItemParams(
    val parentId: String,
    val childItem: LauncherItemState,
    val favConfig: FavoritesListConfig,
    val appWidgetHost: AppWidgetHost?,
    val stackedWidgetConfig: StackedWidgetConfig,
    val hazeState: HazeState?,
    val isVertical: Boolean
)
