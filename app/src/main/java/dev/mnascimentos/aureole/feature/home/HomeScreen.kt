package dev.mnascimentos.aureole.feature.home

import android.appwidget.AppWidgetHost
import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.rememberHazeState
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.SidePanelModel
import dev.mnascimentos.aureole.feature.home.components.EditSidePanelDialog
import dev.mnascimentos.aureole.feature.home.components.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.GridItemContent
import dev.mnascimentos.aureole.feature.home.components.HomeOverlaysContent
import dev.mnascimentos.aureole.feature.home.components.WallpaperBackground
import dev.mnascimentos.aureole.feature.home.components.homeDragGestures
import dev.mnascimentos.aureole.feature.home.components.model.SidePanelConfig
import dev.mnascimentos.aureole.feature.home.folder.HomeScreenFolderOverlays
import dev.mnascimentos.aureole.feature.home.grid.AddContainerDialog
import dev.mnascimentos.aureole.feature.home.grid.DynamicGridContainer
import dev.mnascimentos.aureole.feature.home.grid.EditContainerDialog
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.GridLimits
import dev.mnascimentos.aureole.feature.home.grid.model.GridEditConfig
import dev.mnascimentos.aureole.feature.home.model.GridItemContentParams
import dev.mnascimentos.aureole.feature.home.model.HomeDragParams
import dev.mnascimentos.aureole.feature.home.model.HomeOverlaysConfig
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.HomeScreenBodyConfig
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.widget.HomeScreenWidgetOverlays
import dev.mnascimentos.aureole.feature.home.widget.model.StackedWidgetConfig

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

        val overlaysConfig = HomeOverlaysConfig(
            uiState = config.uiState,
            actions = config.actions,
            listState = config.listState,
            hazeState = config.hazeState,
            externalTouchY = config.externalTouchY,
            coroutineScope = config.coroutineScope,
            screenHeightPx = config.screenHeightPx,
            onExternalTouchYReset = config.onExternalTouchYReset
        )

        HomeOverlaysContent(overlaysConfig)

        HomeScreenFolderOverlays(
            screenHeightPx = config.screenHeightPx,
            hazeState = config.hazeState,
        )

        HomeScreenWidgetOverlays(
            hazeState = config.hazeState,
        )

        HomeOverlaysDialogsAndErrors(config = overlaysConfig)
    }
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

    if (config.uiState.isEditSidePanelDialogVisible && config.uiState.editingSidePanelId != null) {
        val editingPanel = config.uiState.sidePanels[config.uiState.editingSidePanelId]
            ?: SidePanelModel(id = config.uiState.editingSidePanelId)
        EditSidePanelDialog(
            panel = editingPanel,
            onDismiss = config.actions.onCloseEditSidePanelDialog,
            onSave = config.actions.onSaveSidePanelModel,
            onDeletePanel = config.actions.onDeleteSidePanelInstance
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

private fun getGridItemsForOrientation(isLandscape: Boolean, uiState: MainUiState): List<LauncherItemState> {
    return if (isLandscape) {
        uiState.landscapeGridItems.ifEmpty { uiState.gridItems }
    } else {
        uiState.portraitGridItems.ifEmpty { uiState.gridItems }
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
    val currentLimits = getGridLimits(configuration.orientation)
    val currentItems = getGridItemsForOrientation(isLandscape, uiState)

    DynamicGridContainer(
        items = currentItems,
        config = GridEditConfig(isEditMode = uiState.isGridEditMode, limits = currentLimits),
        actions = actions,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) { item ->
        val currentSidePanelConfig = buildSidePanelConfig(item, uiState, hazeState)

        GridItemContent(
            GridItemContentParams(
                item = item,
                favConfig = favConfig,
                appWidgetHost = appWidgetHost,
                stackedWidgetConfig = stackedWidgetConfig,
                sidePanelConfig = currentSidePanelConfig
            )
        )
    }
}

private fun buildSidePanelConfig(
    item: LauncherItemState,
    uiState: MainUiState,
    hazeState: HazeState
): SidePanelConfig {
    val panelModel = uiState.sidePanels[item.id]
    return if (item.safeType == LauncherItemType.SHORTCUTS_SIDE_PANEL && panelModel != null) {
        SidePanelConfig(
            panelId = panelModel.id,
            title = panelModel.title,
            folders = panelModel.folders,
            appPackageNames = panelModel.appPackageNames,
            openedFolderId = uiState.openedFolderId,
            position = panelModel.position,
            showFolderLabels = panelModel.showFolderLabels,
            showAddFolderButton = panelModel.showAddFolderButton,
            isBackgroundEnabled = panelModel.isBackgroundEnabled,
            isExpandCell = panelModel.isExpandCell,
            isGridFolderEnabled = panelModel.isGridFolderEnabled,
            hazeState = hazeState
        )
    } else {
        SidePanelConfig(
            panelId = item.id,
            folders = emptyList(),
            openedFolderId = uiState.openedFolderId,
            position = uiState.sidePanelPosition,
            showFolderLabels = uiState.showFolderLabels,
            showAddFolderButton = uiState.showSidePanelAddFolderButton,
            isBackgroundEnabled = uiState.isSidePanelBackgroundEnabled,
            isExpandCell = uiState.isSidePanelExpandCell,
            hazeState = hazeState
        )
    }
}
