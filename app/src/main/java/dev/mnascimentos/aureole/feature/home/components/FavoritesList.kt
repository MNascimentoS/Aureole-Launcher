package dev.mnascimentos.aureole.feature.home.components

import android.appwidget.AppWidgetHost
import android.content.ComponentName
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.theme.fadingEdges
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.model.AppItemRowActions
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.widget.StackedWidgetSection
import dev.mnascimentos.aureole.feature.home.widget.model.StackedWidgetConfig

private const val PREVIEW_APPWIDGET_HOST_ID = 1024

@Composable
fun FavoritesList(
    config: FavoritesListConfig,
    appWidgetHost: AppWidgetHost,
    modifier: Modifier = Modifier
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val favoritePackages = remember(uiState.favoriteAppPackages) {
        uiState.favoriteAppPackages.toSet()
    }

    LazyColumn(
        state = config.state,
        modifier = modifier
            .fillMaxHeight()
            .fadingEdges(config.state),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item(key = "clock_header") {
            ClockHeader(hazeState = config.hazeState)
        }

        if (uiState.isWidgetRowEnabled && !isLandscape) {
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

        favoriteAppsSection(
            favoriteApps = uiState.favoriteApps,
            actions = actions,
            onEmptyClick = actions.onOpenFavoritePicker
        )

        allAppsSection(
            showAllApps = uiState.showAllAppsOnHome,
            apps = uiState.apps,
            favoritePackages = favoritePackages,
            actions = actions
        )
    }
}

private fun LazyListScope.favoriteAppsSection(
    favoriteApps: List<AppInfo>,
    actions: HomeScreenActions,
    onEmptyClick: () -> Unit
) {
    if (favoriteApps.isNotEmpty()) {
        items(favoriteApps, key = { "fav_${it.packageName}" }) { app ->
            AppItemRow(
                app = app,
                onClick = { actions.onAppClick(app) },
                isFavorite = true,
                actions = AppItemRowActions(
                    onToggleFavorite = { actions.onToggleFavorite(it) },
                    onAppInfoClick = { actions.onAppInfoClick(it) }
                )
            )
        }
    } else {
        item(key = "favorites_empty_hint") {
            FavoritesEmptyHint(onClick = onEmptyClick)
        }
    }
}

private fun LazyListScope.allAppsSection(
    showAllApps: Boolean,
    apps: List<AppInfo>,
    favoritePackages: Set<String>,
    actions: HomeScreenActions
) {
    if (showAllApps) {
        item(key = "all_apps_divider") {
            AllAppsDivider()
        }

        items(apps, key = { "all_${it.packageName}" }) { app ->
            val isFav = favoritePackages.contains(app.packageName)
            AppItemRow(
                app = app,
                onClick = { actions.onAppClick(app) },
                isFavorite = isFav,
                actions = AppItemRowActions(
                    onToggleFavorite = { actions.onToggleFavorite(it) },
                    onAppInfoClick = { actions.onAppInfoClick(it) }
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

@Composable
private fun FavoritesEmptyHint(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tap to select Favorites",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AllAppsDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
        Text(
            text = " ALL APPS ",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
        )
    }
}

@AureolePreview
@Composable
fun FavoritesListPreview() {
    val context = LocalContext.current
    val mockApp = AppInfo(
        label = "Phone",
        packageName = "com.example.phone",
        componentName = ComponentName("com.example.phone", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )
    val mockUiState = MainUiState(
        favoriteApps = listOf(mockApp),
        favoriteAppPackages = listOf(mockApp.packageName),
        isWidgetRowEnabled = false
    )
    val mockActions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAddWidgetClick = {},
        onRemoveWidgetClick = {},
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {}
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides mockUiState,
            LocalHomeActions provides mockActions
        ) {
            FavoritesList(
                config = FavoritesListConfig(
                    currentHeightDp = 160.dp,
                    currentHeightPx = 400f,
                    state = rememberLazyListState()
                ),
                appWidgetHost = remember { AppWidgetHost(context, PREVIEW_APPWIDGET_HOST_ID) }
            )
        }
    }
}
