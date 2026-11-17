package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
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
private fun QuickButtonSheetCase(
    isLeftHanded: Boolean,
    model: QuickButtonModel = QuickButtonModel(),
    content: @Composable (MainUiState) -> Unit
) {
    val uiState = MainUiState(activeQuickButtonModel = model, isLeftHandedMode = isLeftHanded)
    ProvideHomeUiState(uiState) {
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

/** Button menu: the section with the shortcut and appearance rows, then Remove last. */
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

/** Icon and background in the same theme color: the preview warns and outlines the icon (botao-rapido RF-06). */
@PreviewTest
@AureolePreview
@Composable
private fun QuickButtonAppearanceLowContrastScreenshotTest() = QuickButtonSheetCase(
    isLeftHanded = false,
    model = QuickButtonModel(
        appearance = QuickButtonAppearance(
            iconColor = ThemeColorRef.Role(ThemeColorRole.VARIANT),
            backgroundColor = ThemeColorRef.Role(ThemeColorRole.VARIANT)
        )
    )
) {
    QuickButtonAppearanceBottomSheet(uiState = it, actions = dummyActions(), onDismissRequest = {})
}
