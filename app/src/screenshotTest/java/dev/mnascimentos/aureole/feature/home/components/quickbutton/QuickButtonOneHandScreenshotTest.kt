package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private fun dummyActions() = HomeScreenActions(
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

@Composable
private fun QuickButtonSheetCase(isLeftHanded: Boolean, content: @Composable (MainUiState) -> Unit) {
    val uiState = MainUiState(activeQuickButtonModel = QuickButtonModel(), isLeftHandedMode = isLeftHanded)
    CompositionLocalProvider(LocalHomeUiState provides uiState) {
        AureoleLauncherTheme { content(uiState) }
    }
}

/** Appearance editor: live preview anchored, Cancel / Save with Save under the thumb. */
@PreviewTest
@AureolePreview
@Composable
private fun QuickButtonAppearanceRightHandedScreenshotTest() = QuickButtonSheetCase(isLeftHanded = false) {
    QuickButtonAppearanceBottomSheet(uiState = it, actions = dummyActions(), onDismissRequest = {})
}

@PreviewTest
@AureolePreview
@Composable
private fun QuickButtonAppearanceLeftHandedScreenshotTest() = QuickButtonSheetCase(isLeftHanded = true) {
    QuickButtonAppearanceBottomSheet(uiState = it, actions = dummyActions(), onDismissRequest = {})
}

/** Button menu: Remove on top, then the section with the shortcut and appearance rows. */
@PreviewTest
@AureolePreview
@Composable
private fun QuickButtonSettingsRightHandedScreenshotTest() = QuickButtonSheetCase(isLeftHanded = false) {
    QuickButtonSettingsBottomSheet(uiState = it, actions = dummyActions(), onDismissRequest = {})
}

@PreviewTest
@AureolePreview
@Composable
private fun QuickButtonSettingsLeftHandedScreenshotTest() = QuickButtonSheetCase(isLeftHanded = true) {
    QuickButtonSettingsBottomSheet(uiState = it, actions = dummyActions(), onDismissRequest = {})
}
