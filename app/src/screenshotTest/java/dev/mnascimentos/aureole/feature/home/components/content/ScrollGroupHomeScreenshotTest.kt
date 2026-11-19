package dev.mnascimentos.aureole.feature.home.components.content

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.content.model.ScrollViewContainerParams
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesPreviewMetrics
import dev.mnascimentos.aureole.feature.home.components.widgets.model.StackedWidgetConfig
import dev.mnascimentos.aureole.feature.home.createSampleHomeData
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions

private const val LIST_HEIGHT_PX = 400f

/** A group with only the apps list (all apps on): the whole list shows, the group is what scrolls (RF-C6, RF-C7). */
@Composable
private fun ScrollGroupWithAppsList(orientation: ScrollOrientation) {
    val sample = createSampleHomeData()
    val apps = LauncherItemState(
        id = "apps",
        type = LauncherItemType.APPS_LIST,
        col = 0,
        row = 0,
        colSpan = 2,
        rowSpan = 2,
        showAllApps = true
    )
    val group = LauncherItemState(
        id = "group",
        type = LauncherItemType.SCROLL_VIEW,
        col = 0,
        row = 0,
        colSpan = 16,
        rowSpan = 12,
        scrollOrientation = orientation,
        children = listOf(apps)
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides sample.uiState,
            LocalLeftHanded provides sample.uiState.isLeftHandedMode,
            LocalHomeActions provides sample.actions
        ) {
            ScrollViewContainerContent(
                ScrollViewContainerParams(
                    item = group,
                    favConfig = FavoritesListConfig(
                        currentHeightDp = FavoritesPreviewMetrics.DefaultHeight,
                        currentHeightPx = LIST_HEIGHT_PX,
                        state = rememberLazyListState()
                    ),
                    appWidgetHost = null,
                    stackedWidgetConfig = StackedWidgetConfig(
                        topWidgetIds = emptyList(),
                        currentHeightDp = FavoritesPreviewMetrics.DefaultHeight,
                        currentHeightPx = LIST_HEIGHT_PX
                    ),
                    hazeState = null
                )
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ScrollGroupHomeVerticalScreenshotTest() = ScrollGroupWithAppsList(ScrollOrientation.VERTICAL)

@PreviewTest
@AureolePreview
@Composable
private fun ScrollGroupHomeHorizontalScreenshotTest() = ScrollGroupWithAppsList(ScrollOrientation.HORIZONTAL)
