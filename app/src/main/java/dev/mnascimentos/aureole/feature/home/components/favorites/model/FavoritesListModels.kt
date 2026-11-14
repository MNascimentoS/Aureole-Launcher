package dev.mnascimentos.aureole.feature.home.components.favorites.model

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions

object FavoritesPreviewMetrics {
    val DefaultHeight: Dp = 160.dp
}

data class FavoritesListConfig(
    val currentHeightDp: Dp,
    val currentHeightPx: Float,
    val state: LazyListState,
    val hazeState: HazeState? = null
)

/**
 * @property containerId The id of the apps list on the home, whose favorites it shows and toggles.
 * @property showAllApps Whether it lists every other app below its favorites.
 * @property showLabels Whether the "Favorites" and "All apps" labels show (only with [showAllApps]).
 */
data class FavoritesListOptions(
    val containerId: String? = null,
    val showHeadersAndWidgets: Boolean = true,
    val isInsideScrollView: Boolean = false,
    val showAllApps: Boolean = false,
    val showLabels: Boolean = true
)

/** What every row of an apps list needs: the sections to draw and the list whose favorites the rows toggle. */
data class AppsListParams(
    val sections: AppsListSections,
    val actions: HomeScreenActions,
    val containerId: String? = null,
    val isLeftHandedMode: Boolean = false,
    val hasLabels: Boolean = true
)
