package dev.mnascimentos.aureole.feature.home.components

import android.appwidget.AppWidgetHost
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.utils.fadingEdges
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.model.AppItemRowActions
import dev.mnascimentos.aureole.feature.home.components.model.AppItemRowConfig
import dev.mnascimentos.aureole.feature.home.components.model.FavoritesListOptions
import dev.mnascimentos.aureole.feature.home.components.model.NonScrollableFavoritesParams
import dev.mnascimentos.aureole.feature.home.components.model.ScrollableFavoritesParams
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.widget.StackedWidgetSection
import dev.mnascimentos.aureole.feature.home.widget.model.StackedWidgetConfig

private const val MAX_NON_SCROLLABLE_APPS = 20

@Composable
fun FavoritesList(
    config: FavoritesListConfig,
    appWidgetHost: AppWidgetHost,
    modifier: Modifier = Modifier,
    options: FavoritesListOptions = FavoritesListOptions()
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current

    val containerId = options.containerId
    val favoritePackagesList = remember(containerId, uiState.containerFavorites, uiState.favoriteAppPackages) {
        if (containerId != null && uiState.containerFavorites.containsKey(containerId)) {
            uiState.containerFavorites[containerId] ?: uiState.favoriteAppPackages
        } else {
            uiState.favoriteAppPackages
        }
    }
    val favoritePackages = remember(favoritePackagesList) {
        favoritePackagesList.toSet()
    }

    val appMap = remember(uiState.apps) { uiState.apps.associateBy { it.packageName } }
    val favoriteApps = remember(favoritePackagesList, appMap) {
        favoritePackagesList.mapNotNull { pkg -> appMap[pkg] }
    }

    if (options.isInsideScrollView) {
        NonScrollableFavoritesList(
            params = NonScrollableFavoritesParams(
                uiState = uiState,
                favoriteApps = favoriteApps,
                actions = actions,
                favoritePackages = favoritePackages,
                containerId = containerId
            ),
            modifier = modifier
        )
    } else {
        ScrollableFavoritesList(
            params = ScrollableFavoritesParams(
                config = config,
                uiState = uiState,
                actions = actions,
                favoritePackages = favoritePackages,
                appWidgetHost = appWidgetHost,
                showHeadersAndWidgets = options.showHeadersAndWidgets,
                modifier = modifier
            ),
            favoriteApps = favoriteApps,
            containerId = containerId
        )
    }
}

@Composable
private fun ScrollableFavoritesList(
    params: ScrollableFavoritesParams,
    favoriteApps: List<AppInfo>,
    containerId: String? = null
) {
    val config = params.config
    val uiState = params.uiState
    val actions = params.actions
    val favoritePackages = params.favoritePackages
    val appWidgetHost = params.appWidgetHost
    val showHeadersAndWidgets = params.showHeadersAndWidgets
    val modifier = params.modifier

    LazyColumn(
        state = config.state,
        modifier = modifier
            .fillMaxHeight()
            .fadingEdges(config.state),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        if (showHeadersAndWidgets) {
            item(key = "clock_header") {
                ClockHeader(
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

        favoriteAppsSection(
            favoriteApps = favoriteApps,
            actions = actions,
            containerId = containerId,
            onEmptyClick = { actions.onOpenFavoritePicker(containerId) }
        )

        allAppsSection(
            showAllApps = uiState.showAllAppsOnHome,
            apps = uiState.apps,
            favoritePackages = favoritePackages,
            actions = actions,
            containerId = containerId
        )
    }
}

@Composable
private fun NonScrollableFavoritesList(
    params: NonScrollableFavoritesParams,
    modifier: Modifier = Modifier
) {
    val uiState = params.uiState
    val favoriteApps = params.favoriteApps
    val actions = params.actions
    val favoritePackages = params.favoritePackages
    val containerId = params.containerId

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        if (favoriteApps.isNotEmpty()) {
            favoriteApps.forEach { app ->
                AppItemRow(
                    app = app,
                    onClick = { actions.onAppClick(app) },
                    config = AppItemRowConfig(isFavorite = true, useActionSheet = true),
                    actions = AppItemRowActions(
                        onToggleFavorite = { actions.onToggleFavorite(it) },
                        onEditFavoritesClick = { actions.onOpenFavoritePicker(containerId) },
                        onAppInfoClick = { actions.onAppInfoClick(it) },
                        onUninstallClick = { actions.onUninstallAppClick(it) }
                    )
                )
            }
        } else {
            FavoritesEmptyHint(onClick = { actions.onOpenFavoritePicker(containerId) })
        }

        if (uiState.showAllAppsOnHome) {
            AllAppsDivider()
            val appsToDisplay = remember(uiState.apps) { uiState.apps.take(MAX_NON_SCROLLABLE_APPS) }
            appsToDisplay.forEach { app ->
                val isFav = favoritePackages.contains(app.packageName)
                AppItemRow(
                    app = app,
                    onClick = { actions.onAppClick(app) },
                    config = AppItemRowConfig(isFavorite = isFav, useActionSheet = true),
                    actions = AppItemRowActions(
                        onToggleFavorite = { actions.onToggleFavorite(it) },
                        onEditFavoritesClick = { actions.onOpenFavoritePicker(containerId) },
                        onAppInfoClick = { actions.onAppInfoClick(it) },
                        onUninstallClick = { actions.onUninstallAppClick(it) }
                    )
                )
            }
        }
    }
}

private fun LazyListScope.favoriteAppsSection(
    favoriteApps: List<AppInfo>,
    actions: HomeScreenActions,
    containerId: String? = null,
    onEmptyClick: () -> Unit
) {
    if (favoriteApps.isNotEmpty()) {
        items(
            items = favoriteApps,
            key = { "fav_${it.packageName}" },
            contentType = { "app_row" }
        ) { app ->
            AppItemRow(
                app = app,
                onClick = { actions.onAppClick(app) },
                config = AppItemRowConfig(isFavorite = true, useActionSheet = true),
                actions = AppItemRowActions(
                    onToggleFavorite = { actions.onToggleFavorite(it) },
                    onEditFavoritesClick = { actions.onOpenFavoritePicker(containerId) },
                    onAppInfoClick = { actions.onAppInfoClick(it) },
                    onUninstallClick = { actions.onUninstallAppClick(it) }
                )
            )
        }
    } else {
        item(key = "favorites_empty_hint", contentType = "empty_hint") {
            FavoritesEmptyHint(onClick = onEmptyClick)
        }
    }
}

private fun LazyListScope.allAppsSection(
    showAllApps: Boolean,
    apps: List<AppInfo>,
    favoritePackages: Set<String>,
    actions: HomeScreenActions,
    containerId: String? = null
) {
    if (showAllApps) {
        item(key = "all_apps_divider", contentType = "divider") {
            AllAppsDivider()
        }

        items(
            items = apps,
            key = { "all_${it.packageName}" },
            contentType = { "app_row" }
        ) { app ->
            val isFav = favoritePackages.contains(app.packageName)
            AppItemRow(
                app = app,
                onClick = { actions.onAppClick(app) },
                config = AppItemRowConfig(isFavorite = isFav, useActionSheet = true),
                actions = AppItemRowActions(
                    onToggleFavorite = { actions.onToggleFavorite(it) },
                    onEditFavoritesClick = { actions.onOpenFavoritePicker(containerId) },
                    onAppInfoClick = { actions.onAppInfoClick(it) },
                    onUninstallClick = { actions.onUninstallAppClick(it) }
                )
            )
        }
    }
}

@Composable
fun FavoritesList(
    appWidgetHost: AppWidgetHost,
    currentHeightDp: Dp,
    currentHeightPx: Float,
    state: LazyListState,
    modifier: Modifier = Modifier
) {
    FavoritesList(
        config = FavoritesListConfig(
            currentHeightDp = currentHeightDp,
            currentHeightPx = currentHeightPx,
            state = state
        ),
        appWidgetHost = appWidgetHost,
        modifier = modifier
    )
}
