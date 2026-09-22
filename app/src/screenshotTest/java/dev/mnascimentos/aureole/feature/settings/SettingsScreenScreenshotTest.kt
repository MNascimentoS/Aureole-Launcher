package dev.mnascimentos.aureole.feature.settings

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState

@PreviewTest
@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun SettingsScreenScreenshotTest() {
    val mockUiState = SettingsUiState(
        favoriteAppPackages = listOf("com.example.camera", "com.example.gallery"),
        isSidePanelEnabled = true,
        sidePanelPosition = "Center",
        isLeftHandedMode = false,
        isHazeSupported = true,
        isHazeEnabled = true,
        hazeOpacity = 0.5f
    )

    val mockActions = SettingsScreenActions()

    AureoleLauncherTheme {
        SettingsScreen(
            uiState = mockUiState,
            actions = mockActions
        )
    }
}
