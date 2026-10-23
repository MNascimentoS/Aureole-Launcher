package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsContributorsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering SettingsContributorsScreen, should display title and items`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsContributorsScreen(
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Contributors").assertIsDisplayed()
    }
}
