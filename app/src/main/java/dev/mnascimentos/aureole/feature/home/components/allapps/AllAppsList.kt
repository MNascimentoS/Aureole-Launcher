package dev.mnascimentos.aureole.feature.home.components.allapps

import android.appwidget.AppWidgetHost
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.appitem.AppListSkeletonComponent
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowActions
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.composable.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.utils.fadingEdges
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.HomeAppItemRow
import dev.mnascimentos.aureole.feature.home.components.clock.HomeClockHeader
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListOptions
import dev.mnascimentos.aureole.feature.home.components.widgets.StackedWidgetSection
import dev.mnascimentos.aureole.feature.home.components.widgets.model.StackedWidgetConfig
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.isAppsLoading

private const val MAX_NON_SCROLLABLE_APPS = 20

@Composable
fun AllAppsList(
    config: FavoritesListConfig,
    modifier: Modifier = Modifier,
    appWidgetHost: AppWidgetHost? = null,
    options: FavoritesListOptions = FavoritesListOptions()
) {
    if (LocalHomeUiState.current.isAppsLoading) {
        AppListSkeletonComponent(modifier = modifier.padding(AureoleDS.dimens.small))
    } else if (options.isInsideScrollView) {
        NonScrollableAllAppsList(modifier = modifier)
    } else {
        ScrollableAllAppsList(
            config = config,
            modifier = modifier,
            appWidgetHost = appWidgetHost,
            options = options
        )
    }
}

@Composable
private fun NonScrollableAllAppsList(modifier: Modifier = Modifier) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        val appsToDisplay = remember(uiState.apps) { uiState.apps.take(MAX_NON_SCROLLABLE_APPS) }
        appsToDisplay.forEach { app ->
            HomeAppItemRow(
                app = app,
                onClick = { actions.onAppClick(app) },
                config = AppItemRowConfig(useActionSheet = true, isLeftHandedMode = uiState.isLeftHandedMode),
                actions = AppItemRowActions(
                    onAppInfoClick = { actions.onAppInfoClick(it) },
                    onUninstallClick = { actions.onUninstallAppClick(it) }
                )
            )
        }
    }
}

@Composable
private fun ScrollableAllAppsList(
    config: FavoritesListConfig,
    modifier: Modifier = Modifier,
    appWidgetHost: AppWidgetHost? = null,
    options: FavoritesListOptions = FavoritesListOptions()
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current

    LazyColumn(
        state = config.state,
        modifier = modifier
            .fillMaxHeight()
            .fadingEdges(config.state),
        contentPadding = PaddingValues(vertical = AureoleDS.dimens.small)
    ) {
        if (options.showHeadersAndWidgets) {
            item(key = "clock_header") {
                HomeClockHeader(
                    hazeConfig = ClockHazeConfig(
                        hazeState = config.hazeState,
                        isHazeEnabled = uiState.isHazeEnabled,
                        hazeOpacity = uiState.hazeOpacity,
                        isBackgroundEnabled = uiState.isClockBackgroundEnabled
                    ),
                    onLongClick = { actions.onOpenEditClockBottomSheet() }
                )
            }

            if (uiState.isWidgetRowEnabled) {
                item(key = "stacked_widget_section") {
                    StackedWidgetSection(
                        config = StackedWidgetConfig(
                            topWidgetIds = uiState.topWidgetIds,
                            currentHeightDp = config.currentHeightDp,
                            currentHeightPx = config.currentHeightPx,
                            showWidgetDots = uiState.showWidgetDots,
                            hazeState = config.hazeState,
                        ),
                        appWidgetHost = appWidgetHost
                    )
                }
            }
        }

        items(
            items = uiState.apps,
            key = { "all_${it.packageName}" },
            contentType = { "app_row" }
        ) { app ->
            HomeAppItemRow(
                app = app,
                onClick = { actions.onAppClick(app) },
                config = AppItemRowConfig(useActionSheet = true, isLeftHandedMode = uiState.isLeftHandedMode),
                actions = AppItemRowActions(
                    onAppInfoClick = { actions.onAppInfoClick(it) },
                    onUninstallClick = { actions.onUninstallAppClick(it) }
                )
            )
        }
    }
}
