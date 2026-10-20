package dev.mnascimentos.aureole.feature.home

import android.appwidget.AppWidgetHost
import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.components.content.GridItemContent
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.widgets.model.StackedWidgetConfig
import dev.mnascimentos.aureole.feature.home.grid.DynamicGridContainer
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.model.GridEditConfig
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import dev.mnascimentos.aureole.feature.home.model.GridItemContentParams
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

internal fun getGridLimits(orientation: Int): GridLimits {
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

internal fun getGridItemsForOrientation(isLandscape: Boolean, uiState: MainUiState): List<LauncherItemState> {
    return if (isLandscape) {
        uiState.landscapeGridItems.ifEmpty { uiState.gridItems }
    } else {
        uiState.portraitGridItems.ifEmpty { uiState.gridItems }
    }
}

@Composable
internal fun MainHomeLayout(
    appWidgetHost: AppWidgetHost?,
    currentHeightPx: Float,
    favListState: LazyListState,
    hazeState: HazeState
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val currentHeightDp = with(density) { currentHeightPx.toDp() }

    LaunchedEffect(configuration.orientation) {
        val limits = getGridLimits(configuration.orientation)
        actions.onUpdateGridOrientation(limits.maxCols, limits.maxRows)
    }

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
            .padding(horizontal = AureoleDS.dimens.xSmall)
            .padding(top = AureoleDS.dimens.large, bottom = AureoleDS.dimens.xSmall)
    ) { item ->
        val currentContainerConfig = buildContainerConfig(item, uiState, hazeState)

        GridItemContent(
            GridItemContentParams(
                item = item,
                favConfig = favConfig,
                appWidgetHost = appWidgetHost,
                stackedWidgetConfig = stackedWidgetConfig,
                containerConfig = currentContainerConfig
            )
        )
    }
}

internal fun buildContainerConfig(
    item: LauncherItemState,
    uiState: MainUiState,
    hazeState: HazeState
): ContainerConfig {
    val panelModel = uiState.containers[item.id]
    return if (item.safeType == LauncherItemType.SHORTCUTS_CONTAINER && panelModel != null) {
        ContainerConfig(
            panelId = panelModel.id,
            title = panelModel.title,
            items = panelModel.items,
            folders = panelModel.folders,
            appPackageNames = panelModel.appPackageNames,
            openedFolderId = uiState.openedFolderId,
            position = panelModel.position,
            orientation = panelModel.orientation,
            showFolderLabels = panelModel.showFolderLabels,
            showAddFolderButton = panelModel.showAddFolderButton,
            isBackgroundEnabled = panelModel.isBackgroundEnabled,
            isExpandCell = panelModel.isExpandCell,
            isGridFolderEnabled = panelModel.isGridFolderEnabled,
            hideText = panelModel.hideText,
            textColor = panelModel.textColor,
            backgroundColor = panelModel.backgroundColor,
            strokeEnabled = panelModel.strokeEnabled,
            strokeColor = panelModel.strokeColor,
            folderColor = panelModel.folderColor,
            folderStrokeEnabled = panelModel.folderStrokeEnabled,
            scrollUpShortcut = panelModel.scrollUpShortcut,
            scrollDownShortcut = panelModel.scrollDownShortcut,
            hazeState = hazeState
        )
    } else {
        ContainerConfig(
            panelId = item.id,
            folders = emptyList<AppFolder>(),
            openedFolderId = uiState.openedFolderId,
            position = uiState.containerPosition,
            orientation = "Vertical",
            showFolderLabels = uiState.showFolderLabels,
            showAddFolderButton = uiState.showContainerAddFolderButton,
            isBackgroundEnabled = uiState.isContainerBackgroundEnabled,
            isExpandCell = uiState.isContainerExpandCell,
            hazeState = hazeState
        )
    }
}
