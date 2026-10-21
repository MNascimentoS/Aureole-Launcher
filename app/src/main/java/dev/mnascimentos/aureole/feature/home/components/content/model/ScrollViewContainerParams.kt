package dev.mnascimentos.aureole.feature.home.components.content.model

import android.appwidget.AppWidgetHost
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.widgets.model.StackedWidgetConfig

object ScrollChildDimensions {
    val RowHeight = 60.dp
    val ColWidth = 70.dp
    val MinHeight = 70.dp
    val MinWidth = 100.dp
}

/**
 * Parameters for rendering scroll view container content on the home screen.
 */
data class ScrollViewContainerParams(
    val item: LauncherItemState,
    val favConfig: FavoritesListConfig,
    val appWidgetHost: AppWidgetHost?,
    val stackedWidgetConfig: StackedWidgetConfig,
    val containerConfig: ContainerConfig
)
