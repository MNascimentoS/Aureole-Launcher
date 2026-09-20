package dev.mnascimentos.aureole.ui.screens

import android.appwidget.AppWidgetHost
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.ui.geometry.Rect
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import dev.mnascimentos.aureole.R
import java.io.File
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState
import dev.chrisbanes.haze.hazeSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.data.model.AppInfo
import dev.mnascimentos.aureole.ui.FolderViewIntent
import dev.mnascimentos.aureole.ui.MainUiState
import dev.mnascimentos.aureole.ui.components.AppItemRow
import dev.mnascimentos.aureole.ui.components.AppsListDrawer
import dev.mnascimentos.aureole.ui.components.ClockHeader
import dev.mnascimentos.aureole.ui.components.CreateFolderDialog
import dev.mnascimentos.aureole.ui.components.CurvedAlphabetScrubber
import dev.mnascimentos.aureole.ui.components.EditFolderDialog
import dev.mnascimentos.aureole.ui.components.FolderAppPickerDialog
import dev.mnascimentos.aureole.ui.components.OpenedFolderPopup
import dev.mnascimentos.aureole.ui.components.OpenedWidgetPopup
import dev.mnascimentos.aureole.ui.components.SidePanel
import dev.mnascimentos.aureole.ui.components.WidgetResizeDialog
import dev.mnascimentos.aureole.ui.theme.fadingEdges
import dev.mnascimentos.aureole.ui.components.SidePanelConfig
import dev.mnascimentos.aureole.ui.components.StackedWidgetSection
import kotlinx.coroutines.launch
import kotlin.math.abs

private const val DRAG_THRESHOLD_PX = 15
private const val EDGE_EXCLUSION_WIDTH_DP = 60

private const val POPUP_MAX_OFFSET_SUBTRAHEND = 300
private const val POPUP_MIN_OFFSET_DP = 16f

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

data class FavoritesListConfig(
    val uiState: MainUiState,
    val currentHeightDp: Dp,
    val currentHeightPx: Float,
    val state: LazyListState,
    val hazeState: HazeState? = null
)

@Suppress("LongMethod", "CyclomaticComplexMethod")
@Composable
fun HomeScreen(
    uiState: MainUiState,
    appWidgetHost: AppWidgetHost,
    actions: HomeScreenActions,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val favListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    var currentHeightPx by remember(uiState.widgetRowHeight) {
        mutableFloatStateOf(with(density) { uiState.widgetRowHeight.toPx() })
    }

    var externalTouchY by remember { mutableFloatStateOf(-1f) }
    var screenHeightPx by remember { mutableFloatStateOf(0f) }
    var screenWidthPx by remember { mutableFloatStateOf(0f) }
    var dragStartedOnEdge by remember { mutableStateOf(false) }

    val dragModifier = Modifier.pointerInput(uiState.isLeftHandedMode, screenHeightPx, screenWidthPx) {
        detectDragGestures(
            onDragStart = { offset ->
                val edgeThreshold = with(density) { EDGE_EXCLUSION_WIDTH_DP.dp.toPx() }
                dragStartedOnEdge = if (uiState.isLeftHandedMode) {
                    offset.x < edgeThreshold
                } else {
                    offset.x > screenWidthPx - edgeThreshold
                }

                if (dragStartedOnEdge) {
                    val topPaddingPx = with(density) { 64.dp.toPx() }
                    externalTouchY = offset.y - topPaddingPx
                    if (!uiState.isAllAppsDrawerOpen) {
                        actions.onAllAppsDrawerOpen()
                    }
                }
            },
            onDragEnd = {
                externalTouchY = -1f
                dragStartedOnEdge = false
            },
            onDragCancel = {
                externalTouchY = -1f
                dragStartedOnEdge = false
            },
            onDrag = { change, dragAmount ->
                change.consume()
                if (dragStartedOnEdge) {
                    val topPaddingPx = with(density) { 64.dp.toPx() }
                    externalTouchY = change.position.y - topPaddingPx
                } else if (!uiState.isAllAppsDrawerOpen) {
                    val absX = abs(dragAmount.x)
                    val absY = abs(dragAmount.y)
                    val isVertical = absY > DRAG_THRESHOLD_PX && absY > absX

                    if (isVertical && dragAmount.y > 0) {
                        actions.onExpandNotificationShade()
                    } else if (isVertical && dragAmount.y < 0) {
                        actions.onAllAppsDrawerOpen()
                    }
                }
            }
        )
    }

    val exclusionModifier = if (!uiState.isAllAppsDrawerOpen) {
        Modifier.systemGestureExclusion {
            val heightPx = it.size.height.toFloat()
            val widthPx = it.size.width.toFloat()
            Rect(0f, 0f, widthPx, heightPx)
        }
    } else {
        Modifier
    }

    val hazeState = rememberHazeState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                screenHeightPx = it.size.height.toFloat()
                screenWidthPx = it.size.width.toFloat()
            }
            .then(exclusionModifier)
            .then(dragModifier)
    ) {
        WallpaperBackground(
            isCustomWallpaperSet = uiState.isCustomWallpaperSet,
            customWallpaperPath = uiState.customWallpaperPath,
            hazeState = hazeState,
            isHazeEnabled = uiState.isHazeEnabled
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            MainHomeLayout(
                uiState = uiState,
                actions = actions,
                appWidgetHost = appWidgetHost,
                currentHeightPx = currentHeightPx,
                favListState = favListState,
                hazeState = hazeState,
                isHazeEnabled = uiState.isHazeEnabled,
                hazeOpacity = uiState.hazeOpacity
            )

            AnimatedVisibility(
                visible = uiState.isAllAppsDrawerOpen,
                enter = fadeIn() + slideInHorizontally { if (uiState.isLeftHandedMode) -it / 2 else it / 2 },
                exit = fadeOut() + slideOutHorizontally { if (uiState.isLeftHandedMode) -it / 2 else it / 2 },
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { actions.onAllAppsDrawerClose() })
                        }
                ) {
                    val drawerAlign = if (uiState.isLeftHandedMode) Alignment.CenterStart else Alignment.CenterEnd

                    AppsListDrawer(
                        uiState = uiState,
                        actions = actions,
                        listState = listState,
                        hazeState = hazeState,
                        isHazeEnabled = uiState.isHazeEnabled,
                        hazeOpacity = uiState.hazeOpacity,
                        modifier = Modifier.align(drawerAlign)
                    )
                }
            }

            val scrubberAlign = if (uiState.isLeftHandedMode) Alignment.CenterStart else Alignment.CenterEnd
            CurvedAlphabetScrubber(
                alphabet = uiState.alphabet,
                isAlwaysVisible = uiState.isAllAppsDrawerOpen,
                isGestureEnabled = uiState.isAllAppsDrawerOpen,
                externalTouchY = externalTouchY,
                onLetterSelected = { letter ->
                    if (!uiState.isAllAppsDrawerOpen) {
                        actions.onAllAppsDrawerOpen()
                    }
                    uiState.letterIndexMap[letter]?.let { targetIndex ->
                        coroutineScope.launch {
                            listState.scrollToItem(targetIndex)
                        }
                    }
                },
                onInteractionStarted = {
                    if (!uiState.isAllAppsDrawerOpen) {
                        actions.onAllAppsDrawerOpen()
                    }
                },
                onInteractionEnded = {
                    externalTouchY = -1f
                },
                isLeftHandedMode = uiState.isLeftHandedMode,
                modifier = Modifier.align(scrubberAlign)
            )

            HomeScreenFolderOverlays(
                uiState = uiState,
                actions = actions,
                screenHeightPx = screenHeightPx,
                hazeState = hazeState,
                isHazeEnabled = uiState.isHazeEnabled,
                hazeOpacity = uiState.hazeOpacity
            )

            HomeScreenWidgetOverlays(
                uiState = uiState,
                actions = actions,
                appWidgetHost = appWidgetHost,
                hazeState = hazeState,
                isHazeEnabled = uiState.isHazeEnabled,
                hazeOpacity = uiState.hazeOpacity
            )
        }
    }
}

@Suppress("LongMethod", "LongParameterList")
@Composable
private fun MainHomeLayout(
    uiState: MainUiState,
    actions: HomeScreenActions,
    appWidgetHost: AppWidgetHost,
    currentHeightPx: Float,
    favListState: LazyListState,
    hazeState: HazeState,
    isHazeEnabled: Boolean,
    hazeOpacity: Float
) {
    val density = LocalDensity.current
    val currentHeightDp = with(density) { currentHeightPx.toDp() }

    val sidePanelConfig = SidePanelConfig(
        folders = uiState.folders,
        openedFolderId = uiState.openedFolderId,
        isLeftHandedMode = uiState.isLeftHandedMode,
        position = uiState.sidePanelPosition,
        showFolderLabels = uiState.showFolderLabels,
        hazeState = hazeState,
        isHazeEnabled = isHazeEnabled,
        hazeOpacity = hazeOpacity
    )

    val favConfig = FavoritesListConfig(
        uiState = uiState,
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
            if (uiState.isSidePanelEnabled) {
                SidePanel(
                    config = sidePanelConfig,
                    onFolderClick = { folder, topYPx ->
                        if (uiState.openedFolderId == folder.id) {
                            actions.onFolderIntent(FolderViewIntent.CloseFolder)
                        } else {
                            actions.onFolderIntent(FolderViewIntent.OpenFolder(folder.id, topYPx))
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
            FavoritesList(
                config = favConfig,
                actions = actions,
                appWidgetHost = appWidgetHost,
                modifier = Modifier.weight(1f)
            )
        } else {
            FavoritesList(
                config = favConfig,
                actions = actions,
                appWidgetHost = appWidgetHost,
                modifier = Modifier.weight(1f)
            )
            if (uiState.isSidePanelEnabled) {
                SidePanel(
                    config = sidePanelConfig,
                    onFolderClick = { folder, topYPx ->
                        if (uiState.openedFolderId == folder.id) {
                            actions.onFolderIntent(FolderViewIntent.CloseFolder)
                        } else {
                            actions.onFolderIntent(FolderViewIntent.OpenFolder(folder.id, topYPx))
                        }
                    },
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Suppress("LongMethod", "LongParameterList")
@Composable
private fun HomeScreenFolderOverlays(
    uiState: MainUiState,
    actions: HomeScreenActions,
    screenHeightPx: Float,
    hazeState: HazeState,
    isHazeEnabled: Boolean,
    hazeOpacity: Float
) {
    val density = LocalDensity.current

    if (uiState.isCreateFolderDialogVisible) {
        CreateFolderDialog(
            onDismiss = { actions.onFolderIntent(FolderViewIntent.CloseFolder) },
            onSubmit = { folderName -> actions.onFolderIntent(FolderViewIntent.SubmitFolderName(folderName)) }
        )
    }

    if (uiState.activeFolder != null && !uiState.isCreateFolderDialogVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { actions.onFolderIntent(FolderViewIntent.CloseFolder) })
                }
        ) {
            val popupAlign = if (uiState.isLeftHandedMode) Alignment.TopStart else Alignment.TopEnd
            val sidePadding = 76.dp

            val screenDensity = density.density
            val topInsetDp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

            val rawTopDp = if (uiState.activeFolderTopYPx > 0f) {
                (uiState.activeFolderTopYPx / screenDensity).dp - topInsetDp
            } else {
                40.dp
            }
            val maxTopDp = if (screenHeightPx > 0f) {
                ((screenHeightPx / screenDensity) - POPUP_MAX_OFFSET_SUBTRAHEND).coerceAtLeast(POPUP_MIN_OFFSET_DP).dp
            } else {
                280.dp
            }
            val clampedTopDp = rawTopDp.coerceIn(8.dp, maxTopDp)

            OpenedFolderPopup(
                folder = uiState.activeFolder,
                allApps = uiState.apps,
                onDismiss = { actions.onFolderIntent(FolderViewIntent.CloseFolder) },
                onAppClick = { app -> actions.onFolderIntent(FolderViewIntent.LaunchApp(app.packageName)) },
                onAddAppsClick = { actions.onFolderIntent(FolderViewIntent.AddAppToFolder(uiState.activeFolder.id)) },
                onEditFolderClick = { actions.onSetRenameFolderDialogVisible(true) },
                isLeftHandedMode = uiState.isLeftHandedMode,
                hazeState = hazeState,
                isHazeEnabled = isHazeEnabled,
                hazeOpacity = hazeOpacity,
                modifier = Modifier
                    .align(popupAlign)
                    .padding(
                        start = if (uiState.isLeftHandedMode) sidePadding else 0.dp,
                        end = if (!uiState.isLeftHandedMode) sidePadding else 0.dp,
                        top = clampedTopDp
                    )
            )
        }
    }

    if (uiState.isAddAppToFolderDialogVisible && uiState.activeFolder != null) {
        FolderAppPickerDialog(
            folder = uiState.activeFolder,
            allApps = uiState.apps,
            onDismiss = { actions.onSetAddAppToFolderDialogVisible(false) },
            onSave = { selectedPackages ->
                actions.onFolderIntent(
                    FolderViewIntent.SaveFolderApps(
                        folderId = uiState.activeFolder.id,
                        selectedPackageNames = selectedPackages
                    )
                )
            }
        )
    }

    if (uiState.isRenameFolderDialogVisible && uiState.activeFolder != null) {
        EditFolderDialog(
            folder = uiState.activeFolder,
            onDismiss = { actions.onSetRenameFolderDialogVisible(false) },
            onSave = { newName, icon ->
                actions.onFolderIntent(
                    FolderViewIntent.RenameFolder(
                        folderId = uiState.activeFolder.id,
                        newName = newName,
                        icon = icon
                    )
                )
            },
            onDelete = { actions.onFolderIntent(FolderViewIntent.DeleteFolder(uiState.activeFolder.id)) }
        )
    }
}

@Suppress("LongMethod", "LongParameterList")
@Composable
private fun HomeScreenWidgetOverlays(
    uiState: MainUiState,
    actions: HomeScreenActions,
    appWidgetHost: AppWidgetHost,
    hazeState: HazeState,
    isHazeEnabled: Boolean,
    hazeOpacity: Float
) {
    val density = LocalDensity.current

    if (uiState.showWidgetPopup && uiState.activeWidgetId != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { actions.onCloseWidgetPopup() })
                }
        ) {
            val popupAlign = if (uiState.isLeftHandedMode) Alignment.TopStart else Alignment.TopEnd
            val sidePadding = 76.dp
            val screenDensity = density.density
            val topInsetDp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

            val rawTopDp = if (uiState.activeWidgetTopYPx > 0f) {
                (uiState.activeWidgetTopYPx / screenDensity).dp - topInsetDp
            } else {
                40.dp
            }
            val clampedTopDp = rawTopDp.coerceIn(8.dp, 500.dp)

            OpenedWidgetPopup(
                widgetId = uiState.activeWidgetId,
                appWidgetHost = appWidgetHost,
                onDismiss = actions.onCloseWidgetPopup,
                onResizeClick = actions.onOpenWidgetResizeDialog,
                onRemoveClick = {
                    actions.onRemoveWidgetClick(uiState.activeWidgetId)
                    actions.onCloseWidgetPopup()
                },
                hazeState = hazeState,
                isHazeEnabled = isHazeEnabled,
                hazeOpacity = hazeOpacity,
                modifier = Modifier
                    .align(popupAlign)
                    .padding(
                        start = if (uiState.isLeftHandedMode) sidePadding else 0.dp,
                        end = if (!uiState.isLeftHandedMode) sidePadding else 0.dp,
                        top = clampedTopDp
                    )
            )
        }
    }

    if (uiState.showWidgetResizeDialog) {
        WidgetResizeDialog(
            currentHeightDp = uiState.widgetRowHeight,
            onHeightSelected = { newHeight ->
                actions.onResizeWidgetHeight(newHeight)
                actions.onCloseWidgetPopup()
            },
            onDismiss = actions.onCloseWidgetPopup
        )
    }
}

@Suppress("LongMethod")
@Composable
fun FavoritesList(
    config: FavoritesListConfig,
    actions: HomeScreenActions,
    appWidgetHost: AppWidgetHost,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val favoritePackages = remember(config.uiState.favoriteAppPackages) {
        config.uiState.favoriteAppPackages.toSet()
    }

    LazyColumn(
        state = config.state,
        modifier = modifier
            .fillMaxHeight()
            .fadingEdges(config.state),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item(key = "clock_header") {
            ClockHeader(
                hazeState = config.hazeState,
                isHazeEnabled = config.uiState.isHazeEnabled,
                hazeOpacity = config.uiState.hazeOpacity
            )
        }

        if (config.uiState.isWidgetRowEnabled && !isLandscape) {
            item(key = "stacked_widget_section") {
                StackedWidgetSection(
                    topWidgetIds = config.uiState.topWidgetIds,
                    appWidgetHost = appWidgetHost,
                    currentHeightDp = config.currentHeightDp,
                    onHeightChange = { newHeightPx ->
                        actions.onWidgetRowHeightChanged(with(density) { newHeightPx.toDp() })
                    },
                    actions = actions,
                    currentHeightPx = config.currentHeightPx,
                    showWidgetDots = config.uiState.showWidgetDots,
                    isLeftHandedMode = config.uiState.isLeftHandedMode,
                    isSidePanelEnabled = config.uiState.isSidePanelEnabled,
                    hazeState = config.hazeState,
                    isHazeEnabled = config.uiState.isHazeEnabled,
                    hazeOpacity = config.uiState.hazeOpacity
                )
            }
        }

        if (config.uiState.favoriteApps.isNotEmpty()) {
            items(config.uiState.favoriteApps, key = { "fav_${it.packageName}" }) { app ->
                AppItemRow(
                    app = app,
                    onClick = { actions.onAppClick(app) },
                    isLeftHandedMode = config.uiState.isLeftHandedMode,
                    isFavorite = true,
                    onToggleFavorite = { actions.onToggleFavorite(it) },
                    onAppInfoClick = { actions.onAppInfoClick(it) }
                )
            }
        } else {
            item(key = "favorites_empty_hint") {
                FavoritesEmptyHint(onClick = actions.onOpenFavoritePicker)
            }
        }

        if (config.uiState.showAllAppsOnHome) {
            item(key = "all_apps_divider") {
                AllAppsDivider()
            }

            items(config.uiState.apps, key = { "all_${it.packageName}" }) { app ->
                val isFav = favoritePackages.contains(app.packageName)
                AppItemRow(
                    app = app,
                    onClick = { actions.onAppClick(app) },
                    isLeftHandedMode = config.uiState.isLeftHandedMode,
                    isFavorite = isFav,
                    onToggleFavorite = { actions.onToggleFavorite(it) },
                    onAppInfoClick = { actions.onAppInfoClick(it) }
                )
            }
        }
    }
}

// Backward compatibility overload
@Suppress("LongParameterList")
@Composable
fun FavoritesList(
    uiState: MainUiState,
    actions: HomeScreenActions,
    appWidgetHost: AppWidgetHost,
    currentHeightDp: Dp,
    currentHeightPx: Float,
    state: LazyListState,
    modifier: Modifier = Modifier
) {
    FavoritesList(
        config = FavoritesListConfig(
            uiState = uiState,
            currentHeightDp = currentHeightDp,
            currentHeightPx = currentHeightPx,
            state = state
        ),
        actions = actions,
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

@Composable
private fun WallpaperBackground(
    isCustomWallpaperSet: Boolean,
    customWallpaperPath: String?,
    hazeState: HazeState,
    isHazeEnabled: Boolean
) {
    val model: Any = if (isCustomWallpaperSet && !customWallpaperPath.isNullOrEmpty()) {
        File(customWallpaperPath)
    } else {
        R.drawable.default_wallpaper
    }

    val hazeModifier = if (isHazeEnabled) {
        Modifier.hazeSource(state = hazeState)
    } else {
        Modifier
    }

    Box(modifier = Modifier.fillMaxSize().then(hazeModifier)) {
        AsyncImage(
            model = model,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
    }
}
