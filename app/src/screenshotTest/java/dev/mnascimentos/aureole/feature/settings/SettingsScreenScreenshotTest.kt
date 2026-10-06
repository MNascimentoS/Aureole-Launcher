package dev.mnascimentos.aureole.feature.settings

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.screens.SettingsAdvancedScreen
import dev.mnascimentos.aureole.feature.settings.screens.SettingsAppearanceScreen
import dev.mnascimentos.aureole.feature.settings.screens.SettingsBehaviorScreen
import dev.mnascimentos.aureole.feature.settings.screens.SettingsContributorsScreen
import dev.mnascimentos.aureole.feature.settings.screens.SettingsRootScreen

@PreviewTest
@Preview(name = "Root - Light Mode", showBackground = true)
@Preview(name = "Root - Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun SettingsRootScreenScreenshotTest() {
    AureoleLauncherTheme {
        SettingsRootScreen(
            uiState = SettingsUiState(),
            actions = SettingsScreenActions(),
            onNavigateToBehavior = {},
            onNavigateToAppearance = {},
            onNavigateToAdvanced = {}
        )
    }
}

@PreviewTest
@Preview(name = "Appearance - Light Mode", showBackground = true)
@Preview(name = "Appearance - Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun SettingsAppearanceScreenScreenshotTest() {
    AureoleLauncherTheme {
        SettingsAppearanceScreen(
            uiState = SettingsUiState(),
            actions = SettingsScreenActions(),
            onNavigateBack = {}
        )
    }
}

@PreviewTest
@Preview(name = "Behavior - Light Mode", showBackground = true)
@Preview(name = "Behavior - Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun SettingsBehaviorScreenScreenshotTest() {
    AureoleLauncherTheme {
        SettingsBehaviorScreen(
            uiState = SettingsUiState(),
            actions = SettingsScreenActions(),
            onNavigateBack = {}
        )
    }
}

@PreviewTest
@Preview(name = "Advanced - Light Mode", showBackground = true)
@Preview(name = "Advanced - Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun SettingsAdvancedScreenScreenshotTest() {
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
@Preview(name = "Contributors - Light Mode", showBackground = true)
@Preview(name = "Contributors - Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun SettingsContributorsScreenScreenshotTest() {
    AureoleLauncherTheme {
        SettingsContributorsScreen(
            onNavigateBack = {}
        )
    }
}
