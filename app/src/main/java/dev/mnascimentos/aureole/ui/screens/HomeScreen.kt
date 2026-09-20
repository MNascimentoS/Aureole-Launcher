package dev.mnascimentos.aureole.ui.screens

import android.appwidget.AppWidgetHost
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.systemGestureExclusion
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import dev.mnascimentos.aureole.R
import java.io.File
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
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
import dev.mnascimentos.aureole.ui.components.SidePanel
import dev.mnascimentos.aureole.ui.components.SidePanelConfig
import dev.mnascimentos.aureole.ui.components.StackedWidgetSection
import kotlinx.coroutines.launch

private const val DRAG_THRESHOLD_PX = 15
private const val EDGE_EXCLUSION_HEIGHT_DP = 200
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
)

data class FavoritesListConfig(
    val uiState: MainUiState,
    val currentHeightDp: Dp,
    val currentHeightPx: Float,
    val state: LazyListState
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
                }

                val absX = Math.abs(dragAmount.x)
                val absY = Math.abs(dragAmount.y)
                val isHorizontal = absX > DRAG_THRESHOLD_PX && absX > absY
                val isVertical = absY > DRAG_THRESHOLD_PX && absY > absX

                if (isVertical && dragAmount.y > 0) {
                    actions.onExpandNotificationShade()
                } else if ((isVertical && dragAmount.y < 0) || isHorizontal) {
                    if (!uiState.isAllAppsDrawerOpen) {
                        actions.onAllAppsDrawerOpen()
                    }
                }
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned {
                screenHeightPx = it.size.height.toFloat()
                screenWidthPx = it.size.width.toFloat()
            }
            .then(dragModifier)
    ) {
        WallpaperBackground(
            isCustomWallpaperSet = uiState.isCustomWallpaperSet,
            customWallpaperPath = uiState.customWallpaperPath
        )

        var edgeTouchYPx by remember { mutableFloatStateOf(-1f) }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(EDGE_EXCLUSION_WIDTH_DP.dp)
                .align(if (uiState.isLeftHandedMode) Alignment.CenterStart else Alignment.CenterEnd)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent(PointerEventPass.Initial)
                            val position = event.changes.firstOrNull()?.position
                            if (position != null) {
                                edgeTouchYPx = position.y
                            }
                        }
                    }
                }
                .systemGestureExclusion { layoutCoordinates ->
                    val heightPx = layoutCoordinates.size.height.toFloat()
                    val widthPx = layoutCoordinates.size.width.toFloat()
                    val maxExclusionHeightPx = with(density) { EDGE_EXCLUSION_HEIGHT_DP.dp.toPx() }

                    val centerY = if (edgeTouchYPx >= 0f) edgeTouchYPx else heightPx / 2f
                    val top = (centerY - maxExclusionHeightPx / 2f).coerceIn(
                        0f,
                        (heightPx - maxExclusionHeightPx).coerceAtLeast(0f)
                    )
                    val bottom = (top + maxExclusionHeightPx).coerceAtMost(heightPx)

                    Rect(left = 0f, top = top, right = widthPx, bottom = bottom)
                }
        )

        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            MainHomeLayout(
                uiState = uiState,
                actions = actions,
                appWidgetHost = appWidgetHost,
                currentHeightPx = currentHeightPx,
                favListState = favListState
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
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { actions.onAllAppsDrawerClose() })
                        }
                ) {
                    val drawerAlign = if (uiState.isLeftHandedMode) Alignment.CenterStart else Alignment.CenterEnd

                    AppsListDrawer(
                        uiState = uiState,
                        actions = actions,
                        listState = listState,
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
                screenHeightPx = screenHeightPx
            )
        }
    }
}

@Suppress("LongMethod")
@Composable
private fun MainHomeLayout(
    uiState: MainUiState,
    actions: HomeScreenActions,
    appWidgetHost: AppWidgetHost,
    currentHeightPx: Float,
    favListState: LazyListState
) {
    val density = LocalDensity.current
    val currentHeightDp = with(density) { currentHeightPx.toDp() }

    val sidePanelConfig = SidePanelConfig(
        folders = uiState.folders,
        openedFolderId = uiState.openedFolderId,
        isLeftHandedMode = uiState.isLeftHandedMode,
        position = uiState.sidePanelPosition
    )

    val favConfig = FavoritesListConfig(
        uiState = uiState,
        currentHeightDp = currentHeightDp,
        currentHeightPx = currentHeightPx,
        state = favListState
    )

    Row(modifier = Modifier.fillMaxSize()) {
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

@Suppress("LongMethod")
@Composable
private fun HomeScreenFolderOverlays(
    uiState: MainUiState,
    actions: HomeScreenActions,
    screenHeightPx: Float
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
            onRename = { newName ->
                actions.onFolderIntent(
                    FolderViewIntent.RenameFolder(
                        folderId = uiState.activeFolder.id,
                        newName = newName
                    )
                )
            },
            onDelete = { actions.onFolderIntent(FolderViewIntent.DeleteFolder(uiState.activeFolder.id)) }
        )
    }
}

@Composable
fun FavoritesList(
    config: FavoritesListConfig,
    actions: HomeScreenActions,
    appWidgetHost: AppWidgetHost,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val favoritePackages = remember(config.uiState.favoriteAppPackages) {
        config.uiState.favoriteAppPackages.toSet()
    }

    LazyColumn(
        state = config.state,
        modifier = modifier.fillMaxHeight(),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        item(key = "clock_header") {
            ClockHeader()
        }

        item(key = "stacked_widget_section") {
            StackedWidgetSection(
                topWidgetIds = config.uiState.topWidgetIds,
                appWidgetHost = appWidgetHost,
                currentHeightDp = config.currentHeightDp,
                onHeightChange = { newHeightPx ->
                    actions.onWidgetRowHeightChanged(with(density) { newHeightPx.toDp() })
                },
                actions = actions,
                currentHeightPx = config.currentHeightPx
            )
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
    customWallpaperPath: String?
) {
    val model: Any = if (isCustomWallpaperSet && !customWallpaperPath.isNullOrEmpty()) {
        File(customWallpaperPath)
    } else {
        R.drawable.default_wallpaper
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
