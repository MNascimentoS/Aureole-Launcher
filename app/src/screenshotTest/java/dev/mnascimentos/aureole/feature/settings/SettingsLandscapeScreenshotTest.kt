package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsInnerKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.screens.SettingsRootScreen

@PreviewTest
@AureoleLandscapePreview
@Composable
private fun SettingsRootScreenLandscapeScreenshotTest() {
    AureoleLauncherTheme {
        SettingsRootScreen(
            uiState = SettingsUiState(),
            onNavigateToBehavior = {},
            onNavigateToAppearance = {},
            onNavigateToAdvanced = {}
        )
    }
}

/** In landscape the sheet is narrow and centered over the two panes of the root. */
@PreviewTest
@AureoleLandscapePreview
@Composable
private fun SettingsAdvancedSheetLandscapeScreenshotTest() {
    SettingsSheetScreenshot(sheet = SettingsSheet.Area(SettingsArea.ADVANCED))
}

/** Appearance in landscape (RF-16): the preview on the left, centered, and the sheet on the right. */
@PreviewTest
@AureoleLandscapePreview
@Composable
private fun SettingsAppearanceSheetLandscapeScreenshotTest() {
    SettingsSheetScreenshot(sheet = SettingsSheet.Area(SettingsArea.APPEARANCE))
}

/** The palette inside Appearance in landscape: the same preview beside it shows the palette. */
@PreviewTest
@AureoleLandscapePreview
@Composable
private fun SettingsAppearancePaletteLandscapeScreenshotTest() {
    SettingsSheetScreenshot(sheet = SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.PALETTE))
}
