package dev.mnascimentos.aureole.feature.home.components.favorites

import android.appwidget.AppWidgetHost
import android.content.ComponentName
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.appitem.AppListSkeletonComponent
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowActions
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.composable.clock.model.ClockHazeConfig
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.fadingEdges
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.HomeAppItemRow
import dev.mnascimentos.aureole.feature.home.components.clock.HomeClockHeader
import dev.mnascimentos.aureole.feature.home.components.favorites.model.APPS_LIST_MAX_OTHERS_IN_SCROLL_VIEW
import dev.mnascimentos.aureole.feature.home.components.favorites.model.AppsListParams
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListOptions
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesPreviewMetrics
import dev.mnascimentos.aureole.feature.home.components.favorites.model.ScrollableAppsListParams
import dev.mnascimentos.aureole.feature.home.components.favorites.model.appsListSections
import dev.mnascimentos.aureole.feature.home.components.widgets.StackedWidgetSection
import dev.mnascimentos.aureole.feature.home.components.widgets.model.StackedWidgetConfig
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.favoritesOf
import dev.mnascimentos.aureole.feature.home.model.isAppsLoading

private const val PREVIEW_LIST_ID = "preview_list"

/**
 * The apps list of the home: the list's favorites on top and, when it shows all apps, every other app below
 * them, under the "Favorites" and "All apps" labels. Showing only favorites, it has no labels, and with none it
 * shows the hint that opens the favorites picker. Every row favorites or unfavorites the app in this list.
 */
@Composable
fun AppsList(
    config: FavoritesListConfig,
    modifier: Modifier = Modifier,
    appWidgetHost: AppWidgetHost? = null,
    options: FavoritesListOptions = FavoritesListOptions()
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val containerId = options.containerId
    val favorites = remember(containerId, uiState.containerFavorites, uiState.favoriteAppPackages) {
        uiState.favoritesOf(containerId)
    }
    val maxOthers = if (options.isInsideScrollView) APPS_LIST_MAX_OTHERS_IN_SCROLL_VIEW else Int.MAX_VALUE
    val sections = remember(favorites, uiState.apps, options.showAllApps, maxOthers) {
        appsListSections(uiState.apps, favorites, options.showAllApps, maxOthers)
    }
    val list = AppsListParams(
        sections = sections,
        actions = actions,
        containerId = containerId,
        isLeftHandedMode = uiState.isLeftHandedMode
    )
    when {
        options.showAllApps && uiState.isAppsLoading ->
            AppListSkeletonComponent(modifier = modifier.padding(AureoleDS.dimens.small))
        options.isInsideScrollView -> NonScrollableAppsList(list = list, modifier = modifier)
        else -> ScrollableAppsList(
            ScrollableAppsListParams(
                config = config,
                uiState = uiState,
                list = list,
                appWidgetHost = appWidgetHost,
                showHeadersAndWidgets = options.showHeadersAndWidgets,
                modifier = modifier
            )
        )
    }
}

@Composable
private fun ScrollableAppsList(params: ScrollableAppsListParams) {
    val config = params.config
    val uiState = params.uiState
    val actions = params.list.actions

    LazyColumn(
        state = config.state,
        modifier = params.modifier
            .fillMaxHeight()
            .fadingEdges(config.state),
        contentPadding = PaddingValues(vertical = AureoleDS.dimens.small)
    ) {
        if (params.showHeadersAndWidgets) {
            item(key = "clock_header") {
                HomeClockHeader(
                    hazeConfig = ClockHazeConfig(
                        hazeState = config.hazeState,
                        isHazeEnabled = uiState.isHazeEnabled,
                        hazeOpacity = uiState.hazeOpacity,
                        isBackgroundEnabled = uiState.isClockBackgroundEnabled
                    ),
                    onLongClick = { actions.onOpenEditClockBottomSheet(null) }
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
                        appWidgetHost = params.appWidgetHost
                    )
                }
            }
        }

        appsListItems(params.list)
    }
}

private fun LazyListScope.appsListItems(list: AppsListParams) {
    val sections = list.sections
    if (!sections.showsAll && sections.isEmpty) {
        item(key = "favorites_empty_hint", contentType = "empty_hint") {
            FavoritesEmptyHint(onClick = { list.actions.onOpenFavoritePicker(list.containerId) })
        }
        return
    }
    if (sections.showsAll && sections.favorites.isNotEmpty()) {
        item(key = "section_favorites", contentType = "section_label") {
            AppsListSectionLabel(stringResource(R.string.home_apps_list_section_favorites))
        }
    }
    items(items = sections.favorites, key = { "fav_${it.packageName}" }, contentType = { "app_row" }) { app ->
        AppsListRow(app = app, isFavorite = true, list = list)
    }
    if (sections.showsAll) {
        item(key = "section_all", contentType = "section_label") {
            AppsListSectionLabel(stringResource(R.string.home_apps_list_section_all))
        }
    }
    items(items = sections.others, key = { "all_${it.packageName}" }, contentType = { "app_row" }) { app ->
        AppsListRow(app = app, isFavorite = false, list = list)
    }
}

@Composable
private fun NonScrollableAppsList(list: AppsListParams, modifier: Modifier = Modifier) {
    val sections = list.sections
    Column(modifier = modifier.fillMaxWidth()) {
        if (!sections.showsAll && sections.isEmpty) {
            FavoritesEmptyHint(onClick = { list.actions.onOpenFavoritePicker(list.containerId) })
            return@Column
        }
        if (sections.showsAll && sections.favorites.isNotEmpty()) {
            AppsListSectionLabel(stringResource(R.string.home_apps_list_section_favorites))
        }
        sections.favorites.forEach { app -> AppsListRow(app = app, isFavorite = true, list = list) }
        if (sections.showsAll) AppsListSectionLabel(stringResource(R.string.home_apps_list_section_all))
        sections.others.forEach { app -> AppsListRow(app = app, isFavorite = false, list = list) }
    }
}

/** A row of the list, the same as the favorites rows were: icon and name, no star. */
@Composable
private fun AppsListRow(app: AppInfo, isFavorite: Boolean, list: AppsListParams) {
    val actions = list.actions
    HomeAppItemRow(
        app = app,
        onClick = { actions.onAppClick(app) },
        config = AppItemRowConfig(
            isFavorite = isFavorite,
            useActionSheet = true,
            isLeftHandedMode = list.isLeftHandedMode
        ),
        actions = AppItemRowActions(
            onToggleFavorite = { actions.onToggleFavoriteWithContainer(it, list.containerId) },
            onEditFavoritesClick = { actions.onOpenFavoritePicker(list.containerId) },
            onAppInfoClick = { actions.onAppInfoClick(it) },
            onUninstallClick = { actions.onUninstallAppClick(it) }
        )
    )
}

@Composable
private fun AppsListSectionLabel(text: String) {
    AureoleMenuSectionLabel(text = text, modifier = Modifier.padding(horizontal = AureoleDS.dimens.medium))
}

@AureolePreview
@Composable
private fun AppsListPreview() {
    val apps = listOf("Camera", "Phone", "Settings").map { label ->
        val packageName = "com.example.${label.lowercase()}"
        AppInfo(
            label = label,
            packageName = packageName,
            componentName = ComponentName(packageName, "MainActivity"),
            icon = 0xFF1B65C0.toInt().toDrawable()
        )
    }
    val uiState = MainUiState(
        isLoading = false,
        apps = apps,
        containerFavorites = mapOf(PREVIEW_LIST_ID to listOf(apps[1].packageName))
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides uiState,
            LocalLeftHanded provides uiState.isLeftHandedMode
        ) {
            AppsList(
                config = FavoritesListConfig(
                    currentHeightDp = FavoritesPreviewMetrics.DefaultHeight,
                    currentHeightPx = 400f,
                    state = rememberLazyListState()
                ),
                options = FavoritesListOptions(
                    containerId = PREVIEW_LIST_ID,
                    showHeadersAndWidgets = false,
                    showAllApps = true
                )
            )
        }
    }
}
