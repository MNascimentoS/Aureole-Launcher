package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsRootScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering SettingsRootScreen, should display app header and navigation options`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsRootScreen(
                    uiState = SettingsUiState(),
                    onNavigateToBehavior = {},
                    onNavigateToAppearance = {},
                    onNavigateToAdvanced = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Aureole").assertExists()
        composeTestRule.onNodeWithText("Appearance").assertExists()
        composeTestRule.onNodeWithText("Behavior").assertExists()
        composeTestRule.onNodeWithText("Advanced").assertExists()
        composeTestRule.onNodeWithText("Thank you!").assertExists()
    }

    @Test
    fun `when clicking menu items, should invoke corresponding navigation callbacks`() {
        var behaviorNavigated = false
        var appearanceNavigated = false
        var advancedNavigated = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsRootScreen(
                    uiState = SettingsUiState(),
                    onNavigateToBehavior = { behaviorNavigated = true },
                    onNavigateToAppearance = { appearanceNavigated = true },
                    onNavigateToAdvanced = { advancedNavigated = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Appearance").performScrollTo().performClick()
        assertTrue(appearanceNavigated)

        composeTestRule.onNodeWithText("Behavior").performScrollTo().performClick()
        assertTrue(behaviorNavigated)

        composeTestRule.onNodeWithText("Advanced").performScrollTo().performClick()
        assertTrue(advancedNavigated)
    }

    @Test
    fun `when clicking header, should open release notes overlay`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsRootScreen(
                    uiState = SettingsUiState(),
                    onNavigateToBehavior = {},
                    onNavigateToAppearance = {},
                    onNavigateToAdvanced = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Aureole").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Got it").assertExists()
    }
}
