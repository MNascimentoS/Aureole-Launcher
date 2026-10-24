package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsAdvancedScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering SettingsAdvancedScreen, should display header, items, and links`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsAdvancedScreen(
                    uiState = SettingsUiState(),
                    actions = SettingsScreenActions(),
                    onNavigateToContributors = {},
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Advanced").assertExists()
        composeTestRule.onNodeWithText("Set as default launcher").assertExists()
        composeTestRule.onNodeWithText("App info").assertExists()
        composeTestRule.onNodeWithText("What's new (Release notes)").assertExists()
        composeTestRule.onNodeWithText("Reset launcher").assertExists()
        composeTestRule.onNodeWithText("Uninstall launcher").assertExists()
        composeTestRule.onNodeWithText("Contributors").assertExists()
        composeTestRule.onNodeWithText("Privacy policy").assertExists()
        composeTestRule.onNodeWithText("Terms of service").assertExists()
        composeTestRule.onNodeWithText("Privacy settings").assertExists()
        composeTestRule.onNodeWithText("Open source licenses").assertExists()
    }

    @Test
    fun `when clicking action items, should trigger callbacks and dialogs`() {
        var defaultLauncherClicked = false
        var resetLauncherClicked = false
        var contributorsNavigated = false

        val actions = SettingsScreenActions(
            onSetDefaultLauncherClick = { defaultLauncherClicked = true },
            onOpenFactoryResetDialog = { resetLauncherClicked = true }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsAdvancedScreen(
                    uiState = SettingsUiState(),
                    actions = actions,
                    onNavigateToContributors = { contributorsNavigated = true },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Set as default launcher").performScrollTo().performClick()
        assertTrue(defaultLauncherClicked)

        composeTestRule.onNodeWithText("App info").performScrollTo().performClick()

        composeTestRule.onNodeWithText("Reset launcher").performScrollTo().performClick()
        assertTrue(resetLauncherClicked)

        composeTestRule.onNodeWithText("Contributors").performScrollTo().performClick()
        assertTrue(contributorsNavigated)
    }

    @Test
    fun `when clicking Release notes, should open release notes sheet`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsAdvancedScreen(
                    uiState = SettingsUiState(),
                    actions = SettingsScreenActions(),
                    onNavigateToContributors = {},
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("What's new (Release notes)").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Got it").assertExists()
    }

    @Test
    fun `when interacting with UninstallBottomSheet, should execute contact us and uninstall actions`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsAdvancedScreen(
                    uiState = SettingsUiState(),
                    actions = SettingsScreenActions(),
                    onNavigateToContributors = {},
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Uninstall launcher").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Uninstall launcher?").assertExists()
        composeTestRule.onNodeWithText("Contact us").performClick()

        composeTestRule.onNodeWithText("Uninstall launcher").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Uninstall").performClick()
    }
}
