package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.runtime.Composable
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
internal fun HomeScreenQuickButtonOverlays(
    uiState: MainUiState,
    actions: HomeScreenActions,
    hazeState: HazeState? = null
) {
    if (uiState.showQuickButtonSettingsBottomSheet) {
        QuickButtonSettingsBottomSheet(
            uiState = uiState,
            actions = actions,
            onDismissRequest = actions.onCloseQuickButtonSettings,
            hazeState = hazeState
        )
    }

    if (uiState.showQuickButtonAppearanceBottomSheet) {
        QuickButtonAppearanceBottomSheet(
            uiState = uiState,
            actions = actions,
            onDismissRequest = actions.onCloseQuickButtonAppearance,
            hazeState = hazeState
        )
    }

    if (uiState.showShortcutSelectionBottomSheet) {
        ShortcutSelectionBottomSheet(
            uiState = uiState,
            actions = actions,
            onDismissRequest = actions.onCloseShortcutSelection,
            hazeState = hazeState
        )
    }
}

@AureolePreview
@Composable
private fun HomeScreenQuickButtonOverlaysPreview() {
    val dummyActions = HomeScreenActions(
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
    AureoleLauncherTheme {
        HomeScreenQuickButtonOverlays(
            uiState = MainUiState(),
            actions = dummyActions,
            hazeState = null
        )
    }
}
