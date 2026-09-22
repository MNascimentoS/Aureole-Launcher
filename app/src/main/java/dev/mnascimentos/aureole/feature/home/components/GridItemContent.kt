package dev.mnascimentos.aureole.feature.home.components

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.viewinterop.AndroidView
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.model.SidePanelConfig
import dev.mnascimentos.aureole.feature.home.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.GridItemContentParams
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.widget.StackedWidgetSection

private const val DEFAULT_WIDGET_MIN_WIDTH = 100
private const val DEFAULT_WIDGET_MAX_HEIGHT = 360

@Composable
fun GridClockContent(sidePanelConfig: SidePanelConfig, uiState: MainUiState) {
    Box(modifier = Modifier.fillMaxSize()) {
        ClockHeader(
            hazeState = sidePanelConfig.hazeState,
            isHazeEnabled = uiState.isHazeEnabled,
            hazeOpacity = uiState.hazeOpacity,
            isBackgroundEnabled = uiState.isClockBackgroundEnabled
        )
    }
}

@Composable
fun GridAppsListContent(params: GridItemContentParams) {
    val isInScrollView = params.isInScrollView
    Box(modifier = if (isInScrollView) Modifier.fillMaxWidth() else Modifier.fillMaxSize()) {
        FavoritesList(
            config = if (isInScrollView) params.favConfig.copy(hazeState = null) else params.favConfig,
            appWidgetHost = params.appWidgetHost,
            options = FavoritesListOptions(
                containerId = params.item.id,
                showHeadersAndWidgets = false,
                isInsideScrollView = isInScrollView
            ),
            modifier = if (isInScrollView) Modifier.fillMaxWidth() else Modifier.fillMaxSize()
        )
    }
}

@Composable
fun GridSidePanelContent(
    sidePanelConfig: SidePanelConfig,
    uiState: MainUiState,
    actions: HomeScreenActions,
    isInScrollView: Boolean = false
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        SidePanel(
            config = if (isInScrollView) {
                sidePanelConfig.copy(hazeState = null)
            } else {
                sidePanelConfig
            },
            onFolderClick = { folder, topYPx ->
                if (uiState.openedFolderId == folder.id) {
                    actions.onFolderIntent(FolderViewIntent.CloseFolder)
                } else {
                    actions.onFolderIntent(FolderViewIntent.OpenFolder(folder.id, topYPx))
                }
            }
        )
    }
}

@Composable
fun GridWidgetListContent(params: GridItemContentParams) {
    Box(modifier = Modifier.fillMaxSize()) {
        StackedWidgetSection(
            config = if (params.isInScrollView) {
                params.stackedWidgetConfig.copy(hazeState = null)
            } else {
                params.stackedWidgetConfig
            },
            appWidgetHost = params.appWidgetHost
        )
    }
}

@Composable
fun GridItemContent(params: GridItemContentParams) {
    val item = params.item
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    when (item.safeType) {
        LauncherItemType.CLOCK -> GridClockContent(params.sidePanelConfig, uiState)
        LauncherItemType.APPS_LIST -> GridAppsListContent(params)
        LauncherItemType.SHORTCUTS_SIDE_PANEL -> GridSidePanelContent(
            params.sidePanelConfig,
            uiState,
            actions,
            params.isInScrollView
        )

        LauncherItemType.SINGLE_APP_WIDGET -> SingleAppWidgetContent(
            item = item,
            appWidgetHost = params.appWidgetHost
        )

        LauncherItemType.WIDGET_LIST -> GridWidgetListContent(params)
        LauncherItemType.SCROLL_VIEW -> ScrollViewContainerContent(
            item = item,
            favConfig = params.favConfig,
            appWidgetHost = params.appWidgetHost,
            stackedWidgetConfig = params.stackedWidgetConfig,
            sidePanelConfig = params.sidePanelConfig
        )
    }
}

@Composable
fun SingleAppWidgetContent(
    item: LauncherItemState,
    appWidgetHost: AppWidgetHost
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (item.widgetId != null && item.widgetId != -1) {
            AndroidView(
                factory = { context -> createSingleWidgetView(context, item.widgetId, appWidgetHost) },
                update = { view -> updateSingleWidgetView(view, item.widgetId) },
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(rememberNestedScrollInteropConnection())
            )
        } else {
            UnconfiguredWidgetPlaceholder()
        }
    }
}

private fun createSingleWidgetView(
    context: Context,
    widgetId: Int,
    appWidgetHost: AppWidgetHost
): View {
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val appWidgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)
    return if (appWidgetInfo != null) {
        val widthDp = (context.resources.configuration.screenWidthDp)
            .coerceAtLeast(DEFAULT_WIDGET_MIN_WIDTH)
        val options = Bundle().apply {
            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, appWidgetInfo.minWidth)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, appWidgetInfo.minHeight)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, widthDp)
            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, DEFAULT_WIDGET_MAX_HEIGHT)
        }
        appWidgetManager.updateAppWidgetOptions(widgetId, options)
        appWidgetHost.createView(context, widgetId, appWidgetInfo)
    } else {
        TextView(context).apply { text = "Widget" }
    }
}

private fun updateSingleWidgetView(view: View, widgetId: Int) {
    val widthDp = view.context.resources.configuration.screenWidthDp
    if (view is AppWidgetHostView && view.tag != widthDp) {
        view.tag = widthDp
        val appWidgetManager = AppWidgetManager.getInstance(view.context)
        val appWidgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)
        if (appWidgetInfo != null) {
            val options = Bundle().apply {
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, appWidgetInfo.minWidth)
                putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, appWidgetInfo.minHeight)
                putInt(
                    AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                    widthDp.coerceAtLeast(DEFAULT_WIDGET_MIN_WIDTH)
                )
                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, DEFAULT_WIDGET_MAX_HEIGHT)
            }
            appWidgetManager.updateAppWidgetOptions(widgetId, options)
        }
    }
}

@Composable
private fun UnconfiguredWidgetPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = "Widget não configurado",
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
