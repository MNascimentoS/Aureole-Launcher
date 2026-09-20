package dev.mnascimentos.aureole.feature.home.model

import android.appwidget.AppWidgetHost
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Density
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.CoroutineScope

data class HomeDragParams(
    val isLeftHandedMode: Boolean,
    val screenHeightPx: Float,
    val screenWidthPx: Float,
    val isAllAppsDrawerOpen: Boolean,
    val density: Density,
    val onAllAppsDrawerOpen: () -> Unit,
    val onExpandNotificationShade: () -> Unit,
    val onExternalTouchYChange: (Float) -> Unit,
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
    val onExternalTouchYReset: () -> Unit
)

data class HomeScreenBodyConfig(
    val uiState: MainUiState,
    val actions: HomeScreenActions,
    val appWidgetHost: AppWidgetHost,
    val currentHeightPx: Float,
    val favListState: LazyListState,
    val listState: LazyListState,
    val hazeState: HazeState,
    val externalTouchY: Float,
    val coroutineScope: CoroutineScope,
    val screenHeightPx: Float,
    val onExternalTouchYReset: () -> Unit
)
