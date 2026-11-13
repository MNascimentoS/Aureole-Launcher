package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsLicensesScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering, should display title, first section and first entry`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsLicensesScreen(uiState = SettingsUiState(), onNavigateBack = {})
            }
        }

        composeTestRule.onNodeWithText("Open source licenses").assertIsDisplayed()
        composeTestRule.onNodeWithText("LIBRARIES").assertIsDisplayed()
        composeTestRule.onNodeWithText("AndroidX and Jetpack Compose").assertIsDisplayed()
    }

    @Test
    fun `when tapping an entry, should open the detail sheet with the project button`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsLicensesScreen(uiState = SettingsUiState(), onNavigateBack = {})
            }
        }

        composeTestRule.onNodeWithText("AndroidX and Jetpack Compose").performClick()

        composeTestRule.onNodeWithText("View project").assertIsDisplayed()
        composeTestRule.onNodeWithText("Copyright © The Android Open Source Project · Apache 2.0")
            .assertIsDisplayed()
    }
}
