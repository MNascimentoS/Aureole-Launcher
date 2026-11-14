package dev.mnascimentos.aureole.feature.home

import android.appwidget.AppWidgetHost
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
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
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.HomeTopGrabZone
import dev.mnascimentos.aureole.feature.home.components.content.GridItemContent
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.widgets.model.StackedWidgetConfig
import dev.mnascimentos.aureole.feature.home.grid.DynamicGridContainer
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.model.GridEditConfig
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import dev.mnascimentos.aureole.feature.home.model.GridItemContentParams
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
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

    Box(modifier = Modifier.fillMaxSize()) {
        DynamicGridContainer(
            items = currentItems,
            config = GridEditConfig(isEditMode = uiState.isGridEditMode, limits = currentLimits),
            actions = actions,
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = AureoleDS.dimens.xSmall)
                .padding(top = AureoleDS.dimens.homeTopGrabZone, bottom = AureoleDS.dimens.xSmall)
        ) { item ->
            GridItemContent(
                GridItemContentParams(
                    item = item,
                    favConfig = favConfig,
                    appWidgetHost = appWidgetHost,
                    stackedWidgetConfig = stackedWidgetConfig,
                    hazeState = hazeState
                )
            )
        }
        HomeTopGrabZone(enabled = !uiState.isGridEditMode, onLongPress = actions.onOpenCustomizeBottomSheet)
    }
}
