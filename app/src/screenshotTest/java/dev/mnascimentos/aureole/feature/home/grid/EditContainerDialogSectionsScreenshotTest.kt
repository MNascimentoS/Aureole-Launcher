package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private fun mockHomeScreenActions() = HomeScreenActions(
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

@PreviewTest
@AureolePreview
@Composable
private fun EditContainerDialogSectionsScreenshotTest() {
    AureoleLauncherTheme {
        Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium)) {
            EditDialogDeleteRow(
                itemId = "item_preview",
                onDeleteConfirm = {},
                onDismissRequest = {}
            )

            EditDialogTypeActionSection(
                item = LauncherItemState(
                    id = "clock_item",
                    type = LauncherItemType.CLOCK,
                    col = 0,
                    row = 0,
                    colSpan = 2,
                    rowSpan = 2
                ),
                actions = mockHomeScreenActions(),
                onDismissRequest = {}
            )

            EditDialogWidgetListSection(
                item = LauncherItemState(
                    id = "widget_item",
                    type = LauncherItemType.WIDGET_LIST,
                    col = 0,
                    row = 0,
                    colSpan = 2,
                    rowSpan = 2
                ),
                uiState = MainUiState(),
                actions = mockHomeScreenActions()
            )

            EditDialogScrollViewSection(
                item = LauncherItemState(
                    id = "scroll_view_item",
                    type = LauncherItemType.SCROLL_VIEW,
                    col = 0,
                    row = 0,
                    colSpan = 4,
                    rowSpan = 4
                ),
                actions = mockHomeScreenActions(),
                onDismissRequest = {}
            )
        }
    }
}
