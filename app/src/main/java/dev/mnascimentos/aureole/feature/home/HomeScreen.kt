@file:Suppress("TooManyFunctions") // HomeScreen coordinates main launcher overlays, layouts and grid containers

package dev.mnascimentos.aureole.feature.home

import android.appwidget.AppWidgetHost
import android.widget.Toast
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.core.designsystem.utils.LocalSharedTransitionScope
import dev.mnascimentos.aureole.feature.home.components.clock.EditClockBottomSheet
import dev.mnascimentos.aureole.feature.home.components.common.HomeScreenFolderOverlays
import dev.mnascimentos.aureole.feature.home.components.common.WallpaperBackground
import dev.mnascimentos.aureole.feature.home.components.containers.EditContainerScreen
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams
import dev.mnascimentos.aureole.feature.home.components.overlays.CustomizeBottomSheet
import dev.mnascimentos.aureole.feature.home.components.overlays.HomeOverlaysContent
import dev.mnascimentos.aureole.feature.home.components.overlays.homeDragGestures
import dev.mnascimentos.aureole.feature.home.components.widgets.EditWidgetGroupScreen
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupActions
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupParams
import dev.mnascimentos.aureole.feature.home.grid.AddContainerDialog
import dev.mnascimentos.aureole.feature.home.grid.EditGridItemDialog
import dev.mnascimentos.aureole.feature.home.model.HomeDragParams
import dev.mnascimentos.aureole.feature.home.model.HomeOverlaysConfig
import dev.mnascimentos.aureole.feature.home.model.HomeScreenBodyConfig
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.getWidgetIdsForStack
import dev.mnascimentos.aureole.feature.home.model.isOverlayActive

private const val DEFAULT_APP_WIDGET_HOST_ID = 1024

@Suppress("LongMethod") // Root composable of home feature managing scaffolding and gesture states
@Composable
fun HomeScreen(
    appWidgetHost: AppWidgetHost?,
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
            isAlphabetScrubberDisabled = uiState.isAlphabetScrubberDisabled,
            screenHeightPx = screenHeightPx,
            screenWidthPx = screenWidthPx,
            isAllAppsDrawerOpen = uiState.isAllAppsDrawerOpen,
            density = density,
            onAllAppsDrawerOpen = actions.onAllAppsDrawerOpen,
            onExpandNotificationShade = actions.onExpandNotificationShade,
            onExternalTouchYChange = { externalTouchY = it }
        )
    )

    val hazeState = LocalHazeState.current ?: rememberHazeState()

    val isSystemBackActive = uiState.isOverlayActive

    @OptIn(ExperimentalSharedTransitionApi::class)
    SharedTransitionLayout {
        CompositionLocalProvider(LocalSharedTransitionScope provides this) {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        screenHeightPx = it.size.height.toFloat()
                        screenWidthPx = it.size.width.toFloat()
                    }
                    .homeScreenExclusion(isSystemBackActive)
                    .then(if (uiState.isOverlayActive) Modifier else dragModifier)
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
    }
}

private fun Modifier.homeScreenExclusion(isSystemBackActive: Boolean): Modifier {
    return if (!isSystemBackActive) {
        this.systemGestureExclusion {
            val heightPx = it.size.height.toFloat()
            val widthPx = it.size.width.toFloat()
            Rect(0f, 0f, widthPx, heightPx)
        }
    } else {
        this
    }
}

@Suppress("LongMethod") // Root composable of home feature managing scaffolding and gesture states
@Composable
private fun BoxScope.HomeScreenBody(
    config: HomeScreenBodyConfig
) {
    val homeContentHazeModifier = Modifier.hazeSource(state = config.hazeState)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(homeContentHazeModifier)
    ) {
        WallpaperBackground(
            isCustomWallpaperSet = config.uiState.isCustomWallpaperSet,
            customWallpaperPath = config.uiState.customWallpaperPath,
            wallpaperScaleType = config.uiState.wallpaperScaleType,
            hazeState = config.hazeState
        )

        if (!config.uiState.isLoading) {
            MainHomeLayout(
                appWidgetHost = config.appWidgetHost ?: AppWidgetHost(LocalContext.current, DEFAULT_APP_WIDGET_HOST_ID),
                currentHeightPx = config.currentHeightPx,
                favListState = config.favListState,
                hazeState = config.hazeState,
            )
        }
    }

    if (config.uiState.isLoading) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    } else {
        val overlaysConfig = HomeOverlaysConfig(
            uiState = config.uiState,
            actions = config.actions,
            listState = config.listState,
            hazeState = config.hazeState,
            externalTouchY = config.externalTouchY,
            coroutineScope = config.coroutineScope,
            screenHeightPx = config.screenHeightPx,
            appWidgetHost = config.appWidgetHost,
            onExternalTouchYReset = config.onExternalTouchYReset
        )

        HomeOverlaysContent(overlaysConfig)

        HomeScreenFolderOverlays(
            screenHeightPx = config.screenHeightPx,
            hazeState = config.hazeState,
            appWidgetHost = config.appWidgetHost
        )

        HomeOverlaysDialogsAndErrors(config = overlaysConfig)
    }
}

@Suppress("LongMethod") // Root composable of home feature managing scaffolding and gesture states
@Composable
private fun HomeOverlaysDialogsAndErrors(config: HomeOverlaysConfig) {
    if (config.uiState.showCustomizeBottomSheet) {
        CustomizeBottomSheet(
            actions = config.actions,
            onDismissRequest = config.actions.onCloseCustomizeBottomSheet
        )
    }

    if (config.uiState.showEditClockBottomSheet) {
        EditClockBottomSheet(
            uiState = config.uiState,
            actions = config.actions,
            hazeState = if (config.uiState.isHazeEnabled) config.hazeState else null,
            onDismissRequest = config.actions.onCloseEditClockBottomSheet
        )
    }

    if (config.uiState.showAddContainerDialog) {
        AddContainerDialog(
            onDismissRequest = config.actions.onCloseAddContainerDialog,
            onSelectType = { type ->
                if (type == LauncherItemType.SINGLE_APP_WIDGET) {
                    config.actions.onSetIsAddingSingleWidget(true)
                    config.actions.onAddWidgetClick(null)
                    config.actions.onCloseAddContainerDialog()
                } else {
                    config.actions.onAddGridItem(type, null, null)
                }
            },
            isNested = (config.uiState.targetParentContainerId != null)
        )
    }

    HomeEditGridItemOverlay(config = config)
    HomeEditContainerOverlay(config = config)

    val context = LocalContext.current
    config.uiState.gridErrorMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            config.actions.onDismissGridError()
        }
    }
}

@Suppress("LongMethod") // Root composable of home feature managing scaffolding and gesture states
@Composable
private fun HomeEditGridItemOverlay(config: HomeOverlaysConfig) {
    config.uiState.editingGridItem?.let { editingItem ->
        val isWidgetType = editingItem.type == LauncherItemType.WIDGET_LIST ||
            editingItem.type == LauncherItemType.SINGLE_APP_WIDGET
        if (isWidgetType) {
            EditWidgetGroupScreen(
                params = EditWidgetGroupParams(
                    item = editingItem,
                    widgets = config.uiState.getWidgetIdsForStack(editingItem.id),
                    appWidgetHost = config.appWidgetHost,
                    isIndicatorEnabled = config.uiState.widgetStackDots[editingItem.id] ?: true,
                    hazeState = if (config.uiState.isHazeEnabled) config.hazeState else null
                ),
                actions = EditWidgetGroupActions(
                    onToggleIndicator = { config.actions.onToggleWidgetStackDots(editingItem.id) },
                    onRemoveWidget = { config.actions.onRemoveWidgetClick(it, editingItem.id) },
                    onAddWidgetClick = { config.actions.onAddWidgetClick(editingItem.id) },
                    onDismissRequest = config.actions.onCloseEditGridItemDialog,
                    onReorderWidgets = { config.actions.onReorderWidgets(it, editingItem.id) }
                )
            )
        } else {
            EditGridItemDialog(
                item = editingItem,
                onDismissRequest = config.actions.onCloseEditGridItemDialog,
                onDeleteConfirm = { id -> config.actions.onDeleteGridItem(id) }
            )
        }
    }
}

@Suppress("LongMethod") // Root composable of home feature managing scaffolding and gesture states
@Composable
private fun HomeEditContainerOverlay(config: HomeOverlaysConfig) {
    if (config.uiState.isEditContainerDialogVisible && config.uiState.editingContainerId != null) {
        val editingPanel = config.uiState.containers[config.uiState.editingContainerId]
            ?: ContainerModel(id = config.uiState.editingContainerId)

        EditContainerScreen(
            params = EditContainerScreenParams(
                panel = editingPanel,
                allApps = config.uiState.apps,
                hazeState = if (config.uiState.isHazeEnabled) config.hazeState else null,
                onDismiss = config.actions.onCloseEditContainerDialog,
                onSave = config.actions.onSaveContainerModel
            )
        )
    }
}

@AureolePreview
@Composable
private fun HomeScreenPreview() {
    val context = LocalContext.current
    AureoleLauncherTheme {
        HomeScreen(appWidgetHost = AppWidgetHost(context, DEFAULT_APP_WIDGET_HOST_ID))
    }
}
