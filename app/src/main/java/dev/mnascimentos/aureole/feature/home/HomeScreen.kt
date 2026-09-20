package dev.mnascimentos.aureole.feature.home

import android.appwidget.AppWidgetHost
import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.AppsListDrawer
import dev.mnascimentos.aureole.feature.home.components.CurvedAlphabetScrubber
import dev.mnascimentos.aureole.feature.home.components.FavoritesList
import dev.mnascimentos.aureole.feature.home.components.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.ScrubberCallbacks
import dev.mnascimentos.aureole.feature.home.components.ScrubberOptions
import dev.mnascimentos.aureole.feature.home.components.SidePanel
import dev.mnascimentos.aureole.feature.home.components.SidePanelConfig
import dev.mnascimentos.aureole.feature.home.components.WallpaperBackground
import dev.mnascimentos.aureole.feature.home.folder.HomeScreenFolderOverlays
import dev.mnascimentos.aureole.feature.home.widget.HomeScreenWidgetOverlays
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val DRAG_THRESHOLD_PX = 15
private const val EDGE_EXCLUSION_WIDTH_DP = 60
private const val TOP_PADDING_DP = 64
private const val PREVIEW_APPWIDGET_HOST_ID = 1024

data class HomeScreenActions(
    val onWidgetRowHeightChanged: (Dp) -> Unit,
    val onAddWidgetClick: () -> Unit,
    val onRemoveWidgetClick: (Int) -> Unit,
    val onAppClick: (AppInfo) -> Unit,
    val onExpandNotificationShade: () -> Unit,
    val onFolderIntent: (FolderViewIntent) -> Unit,
    val onSetAddAppToFolderDialogVisible: (Boolean) -> Unit,
    val onSetRenameFolderDialogVisible: (Boolean) -> Unit,
    val onSearchQueryChanged: (String) -> Unit,
    val onSettingsClick: () -> Unit,
    val onAllAppsDrawerClose: () -> Unit,
    val onAllAppsDrawerOpen: () -> Unit,
    val onToggleFavorite: (String) -> Unit = {},
    val onAppInfoClick: (AppInfo) -> Unit = {},
    val onOpenFavoritePicker: () -> Unit = {},
    val onOpenWidgetPopup: (Int, Float) -> Unit = { _, _ -> },
    val onCloseWidgetPopup: () -> Unit = {},
    val onOpenWidgetResizeDialog: () -> Unit = {},
    val onCloseWidgetResizeDialog: () -> Unit = {},
    val onResizeWidgetHeight: (Dp) -> Unit = {},
)

data class HomeDragParams(
    val isLeftHandedMode: Boolean,
    val screenHeightPx: Float,
    val screenWidthPx: Float,
    val isAllAppsDrawerOpen: Boolean,
    val density: Density,
    val onAllAppsDrawerOpen: () -> Unit,
    val onExpandNotificationShade: () -> Unit,
    val onExternalTouchYChange: (Float) -> Unit,
)

data class ScrubberOverlayConfig(
    val uiState: MainUiState,
    val externalTouchY: Float,
    val listState: LazyListState,
    val coroutineScope: CoroutineScope,
    val actions: HomeScreenActions,
    val onExternalTouchYReset: () -> Unit,
)

data class HomeOverlaysConfig(
    val uiState: MainUiState,
    val actions: HomeScreenActions,
    val listState: LazyListState,
    val hazeState: HazeState,
    val externalTouchY: Float,
    val coroutineScope: CoroutineScope,
    val screenHeightPx: Float,
    val onExternalTouchYReset: () -> Unit
)

data class HomeScreenBodyConfig(
    val uiState: MainUiState,
    val actions: HomeScreenActions,
    val appWidgetHost: AppWidgetHost,
    val currentHeightPx: Float,
    val favListState: LazyListState,
    val listState: LazyListState,
    val hazeState: HazeState,
    val externalTouchY: Float,
    val coroutineScope: CoroutineScope,
    val screenHeightPx: Float,
    val onExternalTouchYReset: () -> Unit
)

val LocalHomeUiState = staticCompositionLocalOf<MainUiState> { error("No MainUiState provided") }
val LocalHomeActions = staticCompositionLocalOf<HomeScreenActions> { error("No HomeScreenActions provided") }

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
            .then(dragModifier)
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
            change.consume()
            if (dragStartedOnEdge) {
                val topPaddingPx = with(params.density) { TOP_PADDING_DP.dp.toPx() }
                params.onExternalTouchYChange(change.position.y - topPaddingPx)
            } else if (!params.isAllAppsDrawerOpen) {
                val absX = abs(dragAmount.x)
                val absY = abs(dragAmount.y)
                val isVertical = absY > DRAG_THRESHOLD_PX && absY > absX

                if (isVertical && dragAmount.y > 0) {
                    params.onExpandNotificationShade()
                } else if (isVertical && dragAmount.y < 0) {
                    params.onAllAppsDrawerOpen()
                }
            }
        }
    )
}

@Composable
private fun BoxScope.HomeOverlaysContent(
    config: HomeOverlaysConfig
) {
    AppsDrawerOverlay(
        isAllAppsDrawerOpen = config.uiState.isAllAppsDrawerOpen,
        isLeftHandedMode = config.uiState.isLeftHandedMode,
        listState = config.listState,
        hazeState = config.hazeState,
        onClose = config.actions.onAllAppsDrawerClose
    )

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
        modifier = Modifier.align(scrubberAlign)
    )

    HomeScreenFolderOverlays(
        screenHeightPx = config.screenHeightPx,
        hazeState = config.hazeState,
    )

    HomeScreenWidgetOverlays(
        hazeState = config.hazeState,
    )
}

@Composable
private fun AppsDrawerOverlay(
    isAllAppsDrawerOpen: Boolean,
    isLeftHandedMode: Boolean,
    listState: LazyListState,
    hazeState: HazeState,
    onClose: () -> Unit
) {
    AnimatedVisibility(
        visible = isAllAppsDrawerOpen,
        enter = fadeIn() + slideInHorizontally { if (isLeftHandedMode) -it / 2 else it / 2 },
        exit = fadeOut() + slideOutHorizontally { if (isLeftHandedMode) -it / 2 else it / 2 },
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { onClose() })
                }
        ) {
            val drawerAlign = if (isLeftHandedMode) Alignment.CenterStart else Alignment.CenterEnd

            AppsListDrawer(
                listState = listState,
                hazeState = hazeState,
                modifier = Modifier.align(drawerAlign)
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
    val currentHeightDp = with(density) { currentHeightPx.toDp() }

    val sidePanelConfig = SidePanelConfig(
        folders = uiState.folders,
        openedFolderId = uiState.openedFolderId,
        position = uiState.sidePanelPosition,
        showFolderLabels = uiState.showFolderLabels,
        hazeState = hazeState
    )

    val favConfig = FavoritesListConfig(
        currentHeightDp = currentHeightDp,
        currentHeightPx = currentHeightPx,
        state = favListState,
        hazeState = hazeState
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (uiState.isLeftHandedMode) {
            SidePanelSection(uiState = uiState, sidePanelConfig = sidePanelConfig, actions = actions)
            FavoritesList(
                config = favConfig,
                appWidgetHost = appWidgetHost,
                modifier = Modifier.weight(1f)
            )
        } else {
            FavoritesList(
                config = favConfig,
                appWidgetHost = appWidgetHost,
                modifier = Modifier.weight(1f)
            )
            SidePanelSection(uiState = uiState, sidePanelConfig = sidePanelConfig, actions = actions)
        }
    }
}

@Composable
private fun SidePanelSection(
    uiState: MainUiState,
    sidePanelConfig: SidePanelConfig,
    actions: HomeScreenActions
) {
    if (uiState.isSidePanelEnabled) {
        SidePanel(
            config = sidePanelConfig,
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
        onAllAppsDrawerOpen = {}
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
