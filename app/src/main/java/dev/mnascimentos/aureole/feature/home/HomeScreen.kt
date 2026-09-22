package dev.mnascimentos.aureole.feature.home

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.theme.fadingEdges
import dev.mnascimentos.aureole.feature.home.components.AppsListDrawer
import dev.mnascimentos.aureole.feature.home.components.ClockHeader
import dev.mnascimentos.aureole.feature.home.components.CurvedAlphabetScrubber
import dev.mnascimentos.aureole.feature.home.components.FavoritesList
import dev.mnascimentos.aureole.feature.home.components.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.SidePanel
import dev.mnascimentos.aureole.feature.home.components.WallpaperBackground
import dev.mnascimentos.aureole.feature.home.components.model.ScrubberCallbacks
import dev.mnascimentos.aureole.feature.home.components.model.ScrubberOptions
import dev.mnascimentos.aureole.feature.home.components.model.SidePanelConfig
import dev.mnascimentos.aureole.feature.home.folder.HomeScreenFolderOverlays
import dev.mnascimentos.aureole.feature.home.grid.AddContainerDialog
import dev.mnascimentos.aureole.feature.home.grid.DynamicGridContainer
import dev.mnascimentos.aureole.feature.home.grid.EditContainerDialog
import dev.mnascimentos.aureole.feature.home.grid.GridEditConfig
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.GridLimits
import dev.mnascimentos.aureole.feature.home.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.HomeDragParams
import dev.mnascimentos.aureole.feature.home.model.HomeOverlaysConfig
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.HomeScreenBodyConfig
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.ScrubberOverlayConfig
import dev.mnascimentos.aureole.feature.home.widget.HomeScreenWidgetOverlays
import dev.mnascimentos.aureole.feature.home.widget.StackedWidgetSection
import dev.mnascimentos.aureole.feature.home.widget.model.StackedWidgetConfig
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val DRAG_THRESHOLD_PX = 15
private const val EDGE_EXCLUSION_WIDTH_DP = 60
private const val TOP_PADDING_DP = 64
private const val PREVIEW_APPWIDGET_HOST_ID = 1024

val LocalHomeUiState = staticCompositionLocalOf<MainUiState> { error("No MainUiState provided") }
val LocalHomeActions =
    staticCompositionLocalOf<HomeScreenActions> { error("No HomeScreenActions provided") }

@Composable
fun HomeScreen(
    appWidgetHost: AppWidgetHost,
    modifier: Modifier = Modifier,
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val listState = rememberLazyListState()
    val favListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val currentHeightPx by remember(uiState.widgetRowHeight) {
        mutableFloatStateOf(with(density) { uiState.widgetRowHeight.toPx() })
    }

    var externalTouchY by remember { mutableFloatStateOf(-1f) }
    var screenHeightPx by remember { mutableFloatStateOf(0f) }
    var screenWidthPx by remember { mutableFloatStateOf(0f) }

    val dragModifier = Modifier.homeDragGestures(
        HomeDragParams(
            isLeftHandedMode = uiState.isLeftHandedMode,
            screenHeightPx = screenHeightPx,
            screenWidthPx = screenWidthPx,
            isAllAppsDrawerOpen = uiState.isAllAppsDrawerOpen,
            density = density,
            onAllAppsDrawerOpen = actions.onAllAppsDrawerOpen,
            onExpandNotificationShade = actions.onExpandNotificationShade,
            onExternalTouchYChange = { externalTouchY = it }
        )
    )

    val hazeState = rememberHazeState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                screenHeightPx = it.size.height.toFloat()
                screenWidthPx = it.size.width.toFloat()
            }
            .then(HomeScreenExclusionModifier(uiState.isAllAppsDrawerOpen))
            .then(if (uiState.isGridEditMode) Modifier else dragModifier)
    ) {
        HomeScreenBody(
            HomeScreenBodyConfig(
                uiState = uiState,
                actions = actions,
                appWidgetHost = appWidgetHost,
                currentHeightPx = currentHeightPx,
                favListState = favListState,
                listState = listState,
                hazeState = hazeState,
                externalTouchY = externalTouchY,
                coroutineScope = coroutineScope,
                screenHeightPx = screenHeightPx,
                onExternalTouchYReset = { externalTouchY = -1f }
            )
        )
    }
}

@Composable
private fun HomeScreenExclusionModifier(isAllAppsDrawerOpen: Boolean): Modifier {
    return if (!isAllAppsDrawerOpen) {
        Modifier.systemGestureExclusion {
            val heightPx = it.size.height.toFloat()
            val widthPx = it.size.width.toFloat()
            Rect(0f, 0f, widthPx, heightPx)
        }
    } else {
        Modifier
    }
}

@Composable
private fun BoxScope.HomeScreenBody(
    config: HomeScreenBodyConfig
) {
    WallpaperBackground(
        isCustomWallpaperSet = config.uiState.isCustomWallpaperSet,
        customWallpaperPath = config.uiState.customWallpaperPath,
        hazeState = config.hazeState,
        isHazeEnabled = config.uiState.isHazeEnabled
    )

    if (config.uiState.isLoading) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    } else {
        MainHomeLayout(
            appWidgetHost = config.appWidgetHost,
            currentHeightPx = config.currentHeightPx,
            favListState = config.favListState,
            hazeState = config.hazeState,
        )

        HomeOverlaysContent(
            HomeOverlaysConfig(
                uiState = config.uiState,
                actions = config.actions,
                listState = config.listState,
                hazeState = config.hazeState,
                externalTouchY = config.externalTouchY,
                coroutineScope = config.coroutineScope,
                screenHeightPx = config.screenHeightPx,
                onExternalTouchYReset = config.onExternalTouchYReset
            )
        )
    }
}

private fun Modifier.homeDragGestures(
    params: HomeDragParams
): Modifier = pointerInput(params.isLeftHandedMode, params.screenHeightPx, params.screenWidthPx) {
    var dragStartedOnEdge = false
    detectDragGestures(
        onDragStart = { offset ->
            val edgeThreshold = with(params.density) { EDGE_EXCLUSION_WIDTH_DP.dp.toPx() }
            dragStartedOnEdge = if (params.isLeftHandedMode) {
                offset.x < edgeThreshold
            } else {
                offset.x > params.screenWidthPx - edgeThreshold
            }

            if (dragStartedOnEdge) {
                val topPaddingPx = with(params.density) { TOP_PADDING_DP.dp.toPx() }
                params.onExternalTouchYChange(offset.y - topPaddingPx)
                if (!params.isAllAppsDrawerOpen) {
                    params.onAllAppsDrawerOpen()
                }
            }
        },
        onDragEnd = {
            params.onExternalTouchYChange(-1f)
            dragStartedOnEdge = false
        },
        onDragCancel = {
            params.onExternalTouchYChange(-1f)
            dragStartedOnEdge = false
        },
        onDrag = { change, dragAmount ->
            if (dragStartedOnEdge) {
                change.consume()
                val topPaddingPx = with(params.density) { TOP_PADDING_DP.dp.toPx() }
                params.onExternalTouchYChange(change.position.y - topPaddingPx)
            } else if (!params.isAllAppsDrawerOpen && !change.isConsumed) {
                val absX = abs(dragAmount.x)
                val absY = abs(dragAmount.y)
                val isVertical = absY > DRAG_THRESHOLD_PX && absY > absX

                if (isVertical && dragAmount.y > 0) {
                    change.consume()
                    params.onExpandNotificationShade()
                } else if (isVertical && dragAmount.y < 0) {
                    change.consume()
                    params.onAllAppsDrawerOpen()
                }
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BoxScope.HomeOverlaysContent(
    config: HomeOverlaysConfig
) {
    AppsDrawerOverlay(
        config = AppsDrawerOverlayConfig(
            isAllAppsDrawerOpen = config.uiState.isAllAppsDrawerOpen,
            isOpenedFromBottom = config.uiState.isAllAppsOpenedFromBottom,
            isLeftHandedMode = config.uiState.isLeftHandedMode,
            listState = config.listState,
            hazeState = config.hazeState,
            onClose = config.actions.onAllAppsDrawerClose,
        ),
    )

    if (!config.uiState.isAlphabetScrubberDisabled && !WindowInsets.isImeVisible) {
        val scrubberAlign = if (config.uiState.isLeftHandedMode) {
            Alignment.CenterStart
        } else {
            Alignment.CenterEnd
        }

        ScrubberOverlay(
            config = ScrubberOverlayConfig(
                uiState = config.uiState,
                externalTouchY = config.externalTouchY,
                listState = config.listState,
                coroutineScope = config.coroutineScope,
                actions = config.actions,
                onExternalTouchYReset = config.onExternalTouchYReset
            ),
            modifier = Modifier
                .align(scrubberAlign)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
        )
    }

    HomeScreenFolderOverlays(
        screenHeightPx = config.screenHeightPx,
        hazeState = config.hazeState,
    )

    HomeScreenWidgetOverlays(
        hazeState = config.hazeState,
    )

    HomeOverlaysDialogsAndErrors(config = config)
}

@Composable
private fun HomeOverlaysDialogsAndErrors(config: HomeOverlaysConfig) {
    if (config.uiState.showAddContainerDialog) {
        AddContainerDialog(
            onDismissRequest = config.actions.onCloseAddContainerDialog,
            onSelectType = { type ->
                if (type == LauncherItemType.SINGLE_APP_WIDGET) {
                    config.actions.onSetIsAddingSingleWidget(true)
                    config.actions.onAddWidgetClick()
                    config.actions.onCloseAddContainerDialog()
                } else {
                    config.actions.onAddGridItem(type, null)
                }
            },
            isNested = (config.uiState.targetParentContainerId != null)
        )
    }

    config.uiState.editingGridItem?.let { editingItem ->
        EditContainerDialog(
            item = editingItem,
            onDismissRequest = config.actions.onCloseEditContainerDialog,
            onDeleteConfirm = { id -> config.actions.onDeleteGridItem(id) }
        )
    }

    val context = LocalContext.current
    config.uiState.gridErrorMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            config.actions.onDismissGridError()
        }
    }
}

private data class AppsDrawerOverlayConfig(
    val isAllAppsDrawerOpen: Boolean,
    val isOpenedFromBottom: Boolean,
    val isLeftHandedMode: Boolean,
    val listState: LazyListState,
    val hazeState: HazeState,
    val onClose: () -> Unit,
)

@Composable
private fun AppsDrawerOverlay(
    config: AppsDrawerOverlayConfig,
) {
    val enterTransition = if (config.isOpenedFromBottom) {
        fadeIn() + slideInVertically { it }
    } else {
        fadeIn() + slideInHorizontally { if (config.isLeftHandedMode) -it / 2 else it / 2 }
    }

    val exitTransition = if (config.isOpenedFromBottom) {
        fadeOut() + slideOutVertically { it }
    } else {
        fadeOut() + slideOutHorizontally { if (config.isLeftHandedMode) -it / 2 else it / 2 }
    }

    AnimatedVisibility(
        visible = config.isAllAppsDrawerOpen,
        enter = enterTransition,
        exit = exitTransition,
        modifier = Modifier.fillMaxSize(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { config.onClose() })
                },
        ) {
            val drawerAlign =
                if (config.isLeftHandedMode) Alignment.CenterStart else Alignment.CenterEnd

            AppsListDrawer(
                listState = config.listState,
                hazeState = config.hazeState,
                modifier = Modifier.align(drawerAlign),
            )
        }
    }
}

@Composable
private fun ScrubberOverlay(
    config: ScrubberOverlayConfig,
    modifier: Modifier = Modifier
) {
    CurvedAlphabetScrubber(
        alphabet = config.uiState.alphabet,
        options = ScrubberOptions(
            isAlwaysVisible = config.uiState.isAllAppsDrawerOpen,
            isGestureEnabled = config.uiState.isAllAppsDrawerOpen,
            externalTouchY = config.externalTouchY
        ),
        callbacks = ScrubberCallbacks(
            onLetterSelected = { letter ->
                if (!config.uiState.isAllAppsDrawerOpen) {
                    config.actions.onAllAppsDrawerOpen()
                }
                config.uiState.letterIndexMap[letter]?.let { targetIndex ->
                    config.coroutineScope.launch {
                        config.listState.scrollToItem(targetIndex)
                    }
                }
            },
            onInteractionStarted = {
                if (!config.uiState.isAllAppsDrawerOpen) {
                    config.actions.onAllAppsDrawerOpen()
                }
            },
            onInteractionEnded = {
                config.onExternalTouchYReset()
            }
        ),
        modifier = modifier
    )
}

private fun getGridLimits(orientation: Int): GridLimits {
    val maxCols = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        GridEngineUtils.LANDSCAPE_MAX_COLS
    } else {
        GridEngineUtils.PORTRAIT_MAX_COLS
    }
    val maxRows = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
        GridEngineUtils.LANDSCAPE_MAX_ROWS
    } else {
        GridEngineUtils.PORTRAIT_MAX_ROWS
    }
    return GridLimits(maxCols, maxRows)
}

@Composable
private fun GridOrientationEffect(orientation: Int, actions: HomeScreenActions) {
    LaunchedEffect(orientation) {
        val limits = getGridLimits(orientation)
        actions.onUpdateGridOrientation(limits.maxCols, limits.maxRows)
    }
}

@Composable
private fun MainHomeLayout(
    appWidgetHost: AppWidgetHost,
    currentHeightPx: Float,
    favListState: LazyListState,
    hazeState: HazeState
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val currentHeightDp = with(density) { currentHeightPx.toDp() }

    GridOrientationEffect(configuration.orientation, actions)

    val sidePanelConfig = SidePanelConfig(
        folders = uiState.folders,
        openedFolderId = uiState.openedFolderId,
        position = uiState.sidePanelPosition,
        showFolderLabels = uiState.showFolderLabels,
        showAddFolderButton = uiState.showSidePanelAddFolderButton,
        isBackgroundEnabled = uiState.isSidePanelBackgroundEnabled,
        isExpandCell = uiState.isSidePanelExpandCell,
        hazeState = hazeState
    )

    val favConfig = FavoritesListConfig(
        currentHeightDp = currentHeightDp,
        currentHeightPx = currentHeightPx,
        state = favListState,
        hazeState = hazeState
    )

    val stackedWidgetConfig = StackedWidgetConfig(
        topWidgetIds = uiState.topWidgetIds,
        currentHeightDp = currentHeightDp,
        currentHeightPx = currentHeightPx,
        showWidgetDots = uiState.showWidgetDots,
        hazeState = hazeState
    )

    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val currentLimits = if (isLandscape) {
        GridLimits(GridEngineUtils.LANDSCAPE_MAX_COLS, GridEngineUtils.LANDSCAPE_MAX_ROWS)
    } else {
        GridLimits(GridEngineUtils.PORTRAIT_MAX_COLS, GridEngineUtils.PORTRAIT_MAX_ROWS)
    }

    val currentItems = if (isLandscape) {
        uiState.landscapeGridItems.ifEmpty {
            uiState.gridItems
        }
    } else {
        uiState.portraitGridItems.ifEmpty {
            uiState.gridItems
        }
    }

    DynamicGridContainer(
        items = currentItems,
        config = GridEditConfig(isEditMode = uiState.isGridEditMode, limits = currentLimits),
        actions = actions,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) { item ->
        GridItemContent(
            GridItemContentParams(
                item = item,
                favConfig = favConfig,
                appWidgetHost = appWidgetHost,
                stackedWidgetConfig = stackedWidgetConfig,
                sidePanelConfig = sidePanelConfig
            )
        )
    }
}

data class GridItemContentParams(
    val item: LauncherItemState,
    val favConfig: FavoritesListConfig,
    val appWidgetHost: AppWidgetHost,
    val stackedWidgetConfig: StackedWidgetConfig,
    val sidePanelConfig: SidePanelConfig,
    val isInScrollView: Boolean = false
)

@Composable
private fun GridClockContent(sidePanelConfig: SidePanelConfig, uiState: MainUiState) {
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
private fun GridAppsListContent(params: GridItemContentParams) {
    val isInScrollView = params.isInScrollView
    Box(modifier = if (isInScrollView) Modifier.fillMaxWidth() else Modifier.fillMaxSize()) {
        FavoritesList(
            config = if (isInScrollView) params.favConfig.copy(hazeState = null) else params.favConfig,
            appWidgetHost = params.appWidgetHost,
            showHeadersAndWidgets = false,
            isInsideScrollView = isInScrollView,
            modifier = if (isInScrollView) Modifier.fillMaxWidth() else Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun GridSidePanelContent(
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
private fun GridWidgetListContent(params: GridItemContentParams) {
    Box(modifier = Modifier.fillMaxSize()) {
        StackedWidgetSection(
            config = if (params.isInScrollView) {
                params.stackedWidgetConfig.copy(
                    hazeState = null
                )
            } else {
                params.stackedWidgetConfig
            },
            appWidgetHost = params.appWidgetHost
        )
    }
}

@Composable
private fun GridItemContent(params: GridItemContentParams) {
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
private fun SingleAppWidgetContent(
    item: LauncherItemState,
    appWidgetHost: AppWidgetHost
) {
    Box(modifier = Modifier.fillMaxSize()) {
        if (item.widgetId != null && item.widgetId != -1) {
            AndroidView(
                factory = { context ->
                    val appWidgetManager = AppWidgetManager.getInstance(context)
                    val appWidgetInfo = appWidgetManager.getAppWidgetInfo(item.widgetId)
                    if (appWidgetInfo != null) {
                        val widthDp =
                            (context.resources.configuration.screenWidthDp)
                                .coerceAtLeast(100)
                        val options = Bundle().apply {
                            putInt(
                                AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,
                                appWidgetInfo.minWidth
                            )
                            putInt(
                                AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
                                appWidgetInfo.minHeight
                            )
                            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, widthDp)
                            putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 360)
                        }
                        appWidgetManager.updateAppWidgetOptions(item.widgetId, options)
                        appWidgetHost.createView(context, item.widgetId, appWidgetInfo)
                    } else {
                        TextView(context).apply { text = "Widget" }
                    }
                },
                update = { view ->
                    val widthDp = view.context.resources.configuration.screenWidthDp
                    if (view is AppWidgetHostView && view.tag != widthDp) {
                        view.tag = widthDp
                        val appWidgetManager = AppWidgetManager.getInstance(view.context)
                        val appWidgetInfo = appWidgetManager.getAppWidgetInfo(item.widgetId)
                        if (appWidgetInfo != null) {
                            val options = Bundle().apply {
                                putInt(
                                    AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH,
                                    appWidgetInfo.minWidth
                                )
                                putInt(
                                    AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT,
                                    appWidgetInfo.minHeight
                                )
                                putInt(
                                    AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH,
                                    widthDp.coerceAtLeast(100)
                                )
                                putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 360)
                            }
                            appWidgetManager
                                .updateAppWidgetOptions(item.widgetId, options)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(rememberNestedScrollInteropConnection())
            )
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Widget não configurado",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@AureolePreview
@Composable
fun HomeScreenPreview() {
    val context = LocalContext.current
    val mockApp = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )
    val mockUiState = MainUiState(
        apps = listOf(mockApp),
        favoriteApps = listOf(mockApp),
        alphabet = listOf('C'),
        isLoading = false
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
        onAllAppsDrawerOpen = {},
        onEnterGridEditMode = {},
        onCancelGridEditMode = {},
        onSaveGridEditMode = {}
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides mockUiState,
            LocalHomeActions provides mockActions
        ) {
            HomeScreen(
                appWidgetHost = remember { AppWidgetHost(context, PREVIEW_APPWIDGET_HOST_ID) }
            )
        }
    }
}

@Composable
private fun ScrollViewContainerContent(
    item: LauncherItemState,
    favConfig: FavoritesListConfig,
    appWidgetHost: AppWidgetHost,
    stackedWidgetConfig: StackedWidgetConfig,
    sidePanelConfig: SidePanelConfig
) {
    val isVertical = item.safeScrollOrientation == ScrollOrientation.VERTICAL
    val scrollState = rememberScrollState()
    val nestedScrollInterop = rememberNestedScrollInteropConnection()
    val parentNestedScrollConnection = rememberScrollViewNestedConnection(scrollState, isVertical)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .fadingEdges(scrollState, isHorizontal = !isVertical)
            .nestedScroll(parentNestedScrollConnection)
            .nestedScroll(nestedScrollInterop)
    ) {
        if (isVertical) {
            VerticalScrollViewContent(
                VerticalScrollViewContentParams(
                    item = item,
                    favConfig = favConfig,
                    appWidgetHost = appWidgetHost,
                    stackedWidgetConfig = stackedWidgetConfig,
                    sidePanelConfig = sidePanelConfig,
                    scrollState = scrollState
                )
            )
        } else {
            HorizontalScrollViewContent(
                ScrollViewContentParams(
                    item = item,
                    favConfig = favConfig,
                    appWidgetHost = appWidgetHost,
                    stackedWidgetConfig = stackedWidgetConfig,
                    sidePanelConfig = sidePanelConfig
                )
            )
        }
    }
}

@Composable
private fun rememberScrollViewNestedConnection(
    scrollState: ScrollState,
    isVertical: Boolean
): NestedScrollConnection {
    return remember(scrollState, isVertical) {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                val delta = if (isVertical) {
                    available.y
                } else {
                    available.x
                }
                if (delta != 0f && (scrollState.canScrollForward || scrollState.canScrollBackward)) {
                    val consumedByParent = scrollState.dispatchRawDelta(-delta)
                    return if (isVertical) {
                        Offset(0f, -consumedByParent)
                    } else {
                        Offset(-consumedByParent, 0f)
                    }
                }
                return Offset.Zero
            }
        }
    }
}

data class VerticalScrollViewContentParams(
    val item: LauncherItemState,
    val favConfig: FavoritesListConfig,
    val appWidgetHost: AppWidgetHost,
    val stackedWidgetConfig: StackedWidgetConfig,
    val sidePanelConfig: SidePanelConfig,
    val scrollState: ScrollState
)

data class ScrollViewContentParams(
    val item: LauncherItemState,
    val favConfig: FavoritesListConfig,
    val appWidgetHost: AppWidgetHost,
    val stackedWidgetConfig: StackedWidgetConfig,
    val sidePanelConfig: SidePanelConfig,
    val scrollState: ScrollState? = null
)

data class ScrollViewChildItemParams(
    val parentId: String,
    val childItem: LauncherItemState,
    val favConfig: FavoritesListConfig,
    val appWidgetHost: AppWidgetHost,
    val stackedWidgetConfig: StackedWidgetConfig,
    val sidePanelConfig: SidePanelConfig,
    val isVertical: Boolean
)

@Composable
private fun VerticalScrollViewContent(params: VerticalScrollViewContentParams) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val item = params.item
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(params.scrollState)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item.safeChildren.forEach { childItem ->
            ScrollViewChildItem(
                ScrollViewChildItemParams(
                    parentId = item.id,
                    childItem = childItem,
                    favConfig = params.favConfig,
                    appWidgetHost = params.appWidgetHost,
                    stackedWidgetConfig = params.stackedWidgetConfig,
                    sidePanelConfig = params.sidePanelConfig,
                    isVertical = true
                )
            )
        }
        if (uiState.isGridEditMode) {
            ScrollViewAddComponentButton(parentId = item.id, actions = actions)
        }
    }
}

@Composable
private fun HorizontalScrollViewContent(params: ScrollViewContentParams) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val item = params.item
    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item.safeChildren.forEach { childItem ->
            ScrollViewChildItem(
                ScrollViewChildItemParams(
                    parentId = item.id,
                    childItem = childItem,
                    favConfig = params.favConfig,
                    appWidgetHost = params.appWidgetHost,
                    stackedWidgetConfig = params.stackedWidgetConfig,
                    sidePanelConfig = params.sidePanelConfig,
                    isVertical = false
                )
            )
        }
        if (uiState.isGridEditMode) {
            ScrollViewAddComponentButton(parentId = item.id, actions = actions)
        }
    }
}

@Composable
private fun ScrollViewChildItem(params: ScrollViewChildItemParams) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val parentId = params.parentId
    val childItem = params.childItem
    val isVertical = params.isVertical
    val isAppsList = childItem.safeType == LauncherItemType.APPS_LIST
    val childModifier = when {
        isAppsList && isVertical -> {
            Modifier.fillMaxWidth().wrapContentHeight()
        }
        isVertical -> {
            val childHeightDp = (childItem.rowSpan * 60).dp.coerceAtLeast(70.dp)
            Modifier.fillMaxWidth().height(childHeightDp)
        }
        else -> {
            val childWidthDp = (childItem.colSpan * 70).dp.coerceAtLeast(100.dp)
            Modifier.width(childWidthDp).fillMaxHeight()
        }
    }

    Box(modifier = childModifier) {
        GridItemContent(
            GridItemContentParams(
                item = childItem,
                favConfig = params.favConfig,
                appWidgetHost = params.appWidgetHost,
                stackedWidgetConfig = params.stackedWidgetConfig,
                sidePanelConfig = params.sidePanelConfig,
                isInScrollView = true
            )
        )
        if (uiState.isGridEditMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput("block_child_" + childItem.id) {
                        detectTapGestures(onTap = {})
                    }
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(28.dp)
                    .zIndex(100f)
                    .background(MaterialTheme.colorScheme.error, CircleShape)
                    .pointerInput("remove_btn_" + childItem.id) {
                        detectTapGestures(
                            onTap = {
                                actions.onRemoveChildFromScrollView(parentId, childItem.id)
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remover",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun ScrollViewAddComponentButton(parentId: String, actions: HomeScreenActions) {
    Box(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .pointerInput("add_btn_" + parentId) {
                detectTapGestures(
                    onTap = {
                        actions.onOpenAddContainerForParent(parentId)
                    }
                )
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar ao Scroll View",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Componente",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
