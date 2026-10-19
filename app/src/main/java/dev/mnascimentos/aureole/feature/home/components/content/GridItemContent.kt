package dev.mnascimentos.aureole.feature.home.components.content

import android.annotation.SuppressLint
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.viewinterop.AndroidView
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalAnimatedVisibilityScope
import dev.mnascimentos.aureole.core.designsystem.utils.LocalSharedTransitionScope
import dev.mnascimentos.aureole.feature.home.components.clock.ClockContainer
import dev.mnascimentos.aureole.feature.home.components.containers.Container
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.components.content.model.ScrollViewContainerParams
import dev.mnascimentos.aureole.feature.home.components.favorites.FavoritesList
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListOptions
import dev.mnascimentos.aureole.feature.home.components.widgets.StackedWidgetSection
import dev.mnascimentos.aureole.feature.home.model.GridItemContentParams
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val LONG_PRESS_MARGIN_MS = 50L
private const val MIN_LONG_PRESS_TIMEOUT_MS = 200L
private const val DEFAULT_WIDGET_MIN_WIDTH = 100
private const val DEFAULT_WIDGET_MAX_HEIGHT = 360

@Composable
fun GridClockContent(
    containerConfig: ContainerConfig,
    uiState: MainUiState,
    modifier: Modifier = Modifier
) {
    ClockContainer(
        modifier = modifier.fillMaxSize(),
        hazeState = containerConfig.hazeState,
        uiState = uiState
    )
}

@Composable
fun GridAppsListContent(
    params: GridItemContentParams,
    modifier: Modifier = Modifier
) {
    val isInScrollView = params.isInScrollView
    Box(modifier = modifier.then(if (isInScrollView) Modifier.fillMaxWidth() else Modifier.fillMaxSize())) {
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
fun GridContainerContent(
    containerConfig: ContainerConfig,
    uiState: MainUiState,
    actions: HomeScreenActions,
    modifier: Modifier = Modifier,
    isInScrollView: Boolean = false
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Container(
            config = if (isInScrollView) {
                containerConfig.copy(hazeState = null)
            } else {
                containerConfig
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
fun GridWidgetListContent(
    params: GridItemContentParams,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        StackedWidgetSection(
            config = if (params.isInScrollView) {
                params.stackedWidgetConfig.copy(hazeState = null)
            } else {
                params.stackedWidgetConfig
            },
            appWidgetHost = params.appWidgetHost,
            stackId = params.item.id
        )
    }
}

@Composable
fun GridItemContent(params: GridItemContentParams) {
    val item = params.item
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    when (item.safeType) {
        LauncherItemType.CLOCK -> GridClockContent(params.containerConfig, uiState)
        LauncherItemType.APPS_LIST -> GridAppsListContent(params)
        LauncherItemType.SHORTCUTS_CONTAINER -> GridContainerContent(
            params.containerConfig,
            uiState,
            actions,
            isInScrollView = params.isInScrollView
        )

        LauncherItemType.SINGLE_APP_WIDGET -> SingleAppWidgetContent(
            item = item,
            appWidgetHost = params.appWidgetHost
        )

        LauncherItemType.WIDGET_LIST -> GridWidgetListContent(params)
        LauncherItemType.SCROLL_VIEW -> ScrollViewContainerContent(
            ScrollViewContainerParams(
                item = item,
                favConfig = params.favConfig,
                appWidgetHost = params.appWidgetHost,
                stackedWidgetConfig = params.stackedWidgetConfig,
                containerConfig = params.containerConfig
            )
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SingleAppWidgetContent(
    item: LauncherItemState,
    appWidgetHost: AppWidgetHost?,
    modifier: Modifier = Modifier
) {
    val actions = LocalHomeActions.current
    val coroutineScope = rememberCoroutineScope()

    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    val isSharedTransitionActive = sharedTransitionScope != null &&
        animatedVisibilityScope != null &&
        item.widgetId != null
    val sharedModifier = if (isSharedTransitionActive) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(key = "widget_${item.widgetId}"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(sharedModifier)
            .pointerInput(item.widgetId, item.id) {
                val longPressTimeout = viewConfiguration.longPressTimeoutMillis
                awaitEachGesture {
                    val down = awaitFirstDown(pass = PointerEventPass.Initial)
                    var isLongPressTriggered = false

                    val job = coroutineScope.launch {
                        delay((longPressTimeout - LONG_PRESS_MARGIN_MS).coerceAtLeast(MIN_LONG_PRESS_TIMEOUT_MS))
                        isLongPressTriggered = true
                        down.consume()
                        actions.onOpenWidgetStackBottomSheet(item.widgetId, item.id)
                    }

                    try {
                        val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        job.cancel()
                        if (isLongPressTriggered) {
                            up?.consume()
                        }
                    } catch (e: IllegalArgumentException) {
                        Log.d("GridItemContent", "Pointer gesture wait interrupted", e)
                        job.cancel()
                    }
                }
            }
    ) {
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

@SuppressLint("SetTextI18n")
private fun createSingleWidgetView(
    context: Context,
    widgetId: Int,
    appWidgetHost: AppWidgetHost?
): View {
    if (appWidgetHost == null) return TextView(context).apply { text = "Widget" }
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
        AureoleText(
            text = "Widget não configurado",
            color = AureoleDS.colors.onSurfaceHigh
        )
    }
}

@AureolePreview
@Composable
private fun UnconfiguredWidgetPlaceholderPreview() {
    AureoleLauncherTheme {
        UnconfiguredWidgetPlaceholder()
    }
}
