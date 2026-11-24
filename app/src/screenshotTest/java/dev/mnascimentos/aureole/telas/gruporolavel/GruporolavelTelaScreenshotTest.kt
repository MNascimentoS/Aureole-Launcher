package dev.mnascimentos.aureole.telas.gruporolavel

import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.content.ScrollViewContainerContent
import dev.mnascimentos.aureole.feature.home.components.content.model.ScrollViewContainerParams
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesPreviewMetrics
import dev.mnascimentos.aureole.feature.home.components.widgets.model.StackedWidgetConfig
import dev.mnascimentos.aureole.feature.home.grid.EditGridItemDialog
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData

private const val LIST_HEIGHT_PX = 400f
private const val GROUP_COLS = 16
private const val GROUP_ROWS = 12

private fun group(children: List<LauncherItemState>) = LauncherItemState(
    id = "group",
    type = LauncherItemType.SCROLL_VIEW,
    col = 0,
    row = 0,
    colSpan = GROUP_COLS,
    rowSpan = GROUP_ROWS,
    scrollOrientation = ScrollOrientation.VERTICAL,
    children = children
)

/** Sheet do grupo: relógio com "Altura" e "- Nx +", lista de apps com "Lista inteira". */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = HomeCase(createSampleHomeData().uiState) {
    EditGridItemDialog(
        item = group(
            listOf(
                LauncherItemState(id = "clock", type = LauncherItemType.CLOCK, col = 0, row = 0, colSpan = 2, rowSpan = 2),
                LauncherItemState(id = "apps", type = LauncherItemType.APPS_LIST, col = 0, row = 0, colSpan = 2, rowSpan = 6)
            )
        ),
        onDismissRequest = {},
        onDeleteConfirm = {}
    )
}

/** Grupo na home só com a lista de apps (todos os apps): a lista aparece inteira e o grupo é que rola. */
@PreviewTest
@AureolePreview
@Composable
private fun NaHomeScreenshotTest() = HomeCase(createSampleHomeData().uiState) {
    val apps = LauncherItemState(
        id = "apps",
        type = LauncherItemType.APPS_LIST,
        col = 0,
        row = 0,
        colSpan = 2,
        rowSpan = 2,
        showAllApps = true
    )
    ScrollViewContainerContent(
        ScrollViewContainerParams(
            item = group(listOf(apps)),
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
