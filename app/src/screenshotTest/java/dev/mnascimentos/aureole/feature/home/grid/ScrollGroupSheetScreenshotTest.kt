package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions

private fun group(orientation: ScrollOrientation) = LauncherItemState(
    id = "group",
    type = LauncherItemType.SCROLL_VIEW,
    col = 0,
    row = 0,
    colSpan = 16,
    rowSpan = 12,
    scrollOrientation = orientation,
    children = listOf(
        LauncherItemState(id = "clock", type = LauncherItemType.CLOCK, col = 0, row = 0, colSpan = 2, rowSpan = 2),
        LauncherItemState(id = "apps", type = LauncherItemType.APPS_LIST, col = 0, row = 0, colSpan = 2, rowSpan = 6)
    )
)

private fun actions() = HomeScreenActions(
    onWidgetRowHeightChanged = {},
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

/** Clock with "Height" and "- Nx +", apps list with "Full list" and no size control, vertical group (RF-C4, C6). */
@PreviewTest
@AureolePreview
@Composable
private fun ScrollGroupVerticalScreenshotTest() {
    AureoleLauncherTheme {
        EditDialogScrollViewSection(
            item = group(ScrollOrientation.VERTICAL),
            actions = actions(),
            onDismissRequest = {}
        )
    }
}

/** Same group sideways: the clock size reads "Width" (RF-C4, C7). */
@PreviewTest
@AureolePreview
@Composable
private fun ScrollGroupHorizontalScreenshotTest() {
    AureoleLauncherTheme {
        EditDialogScrollViewSection(
            item = group(ScrollOrientation.HORIZONTAL),
            actions = actions(),
            onDismissRequest = {}
        )
    }
}
