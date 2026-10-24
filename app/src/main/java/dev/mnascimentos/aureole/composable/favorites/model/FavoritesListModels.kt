package dev.mnascimentos.aureole.composable.favorites.model

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

object FavoritesPreviewMetrics {
    val DefaultHeight: Dp = 160.dp
}

data class FavoritesListConfig(
    val currentHeightDp: Dp,
    val currentHeightPx: Float,
    val state: LazyListState,
    val hazeState: HazeState? = null
)

data class FavoritesListOptions(
    val containerId: String? = null,
    val showHeadersAndWidgets: Boolean = true,
    val isInsideScrollView: Boolean = false
)

data class NonScrollableFavoritesParams(
    val uiState: MainUiState,
    val favoriteApps: List<AppInfo>,
    val actions: HomeScreenActions,
    val favoritePackages: Set<String>,
    val containerId: String? = null
)
