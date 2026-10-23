package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@PreviewTest
@AureolePreview
@Composable
private fun QuickButtonGridItemScreenshotTest() {
    AureoleLauncherTheme {
        Box(modifier = Modifier.size(100.dp)) {
            QuickButtonGridItem(
                model = QuickButtonModel(),
                onAction = {},
                onLongPress = {}
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun QuickButtonSettingsBottomSheetScreenshotTest() {
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
        QuickButtonSettingsBottomSheet(
            uiState = MainUiState(
                activeQuickButtonModel = QuickButtonModel()
            ),
            actions = dummyActions,
            onDismissRequest = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun QuickButtonAppearanceBottomSheetScreenshotTest() {
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
        QuickButtonAppearanceBottomSheet(
            uiState = MainUiState(
                activeQuickButtonModel = QuickButtonModel()
            ),
            actions = dummyActions,
            onDismissRequest = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ShortcutSelectionBottomSheetScreenshotTest() {
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
        ShortcutSelectionBottomSheet(
            uiState = MainUiState(
                activeQuickButtonModel = QuickButtonModel()
            ),
            actions = dummyActions,
            onDismissRequest = {}
        )
    }
}
