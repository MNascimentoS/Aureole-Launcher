package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.runtime.Composable
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@AureolePreview
@Composable
private fun EditClockBottomSheetPreview() {
    val uiState = MainUiState()
    val actions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAddWidgetClick = {},
        onRemoveWidgetClick = { _, _ -> },
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {},
        onEnterGridEditMode = {},
        onCancelGridEditMode = {},
        onSaveGridEditMode = {}
    )
    AureoleLauncherTheme {
        ProvideHomeUiState(uiState) {
            EditClockBottomSheet(
                uiState = uiState,
                actions = actions,
                onDismissRequest = {}
            )
        }
    }
}
