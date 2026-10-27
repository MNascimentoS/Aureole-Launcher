package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.screens.SettingsAdvancedScreen
import dev.mnascimentos.aureole.feature.settings.screens.SettingsAppearanceScreen
import dev.mnascimentos.aureole.feature.settings.screens.SettingsBehaviorScreen
import dev.mnascimentos.aureole.feature.settings.screens.SettingsContributorsScreen
import dev.mnascimentos.aureole.feature.settings.screens.SettingsRootScreen

@PreviewTest
@AureolePreview
@Composable
private fun SettingsRootScreenScreenshotTest() {
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
@AureolePreview
@Composable
private fun SettingsAppearanceScreenScreenshotTest() {
    AureoleLauncherTheme {
        SettingsAppearanceScreen(
            uiState = SettingsUiState(),
            actions = SettingsScreenActions(),
            onNavigateBack = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun SettingsBehaviorScreenScreenshotTest() {
    AureoleLauncherTheme {
        SettingsBehaviorScreen(
            uiState = SettingsUiState(),
            actions = SettingsScreenActions(),
            onNavigateBack = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun SettingsAdvancedScreenScreenshotTest() {
    AureoleLauncherTheme {
        SettingsAdvancedScreen(
            uiState = SettingsUiState(),
            actions = SettingsScreenActions(),
            onNavigateToContributors = {},
            onNavigateBack = {}
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun SettingsContributorsScreenScreenshotTest() {
    AureoleLauncherTheme {
        SettingsContributorsScreen(
            onNavigateBack = {}
        )
    }
}
