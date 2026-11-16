package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.screens.SettingsAdvancedScreen
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

@PreviewTest
@AureoleLandscapePreview
@Composable
private fun SettingsAdvancedScreenLandscapeScreenshotTest() {
    AureoleLauncherTheme {
        SettingsAdvancedScreen(
            uiState = SettingsUiState(),
            actions = SettingsScreenActions(),
            onNavigateToContributors = {},
            onNavigateBack = {}
        )
    }
}
