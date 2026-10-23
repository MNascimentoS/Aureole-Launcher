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
class SettingsBehaviorScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering SettingsBehaviorScreen, should display title and behavior items`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsBehaviorScreen(
                    uiState = SettingsUiState(),
                    actions = SettingsScreenActions(),
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Behavior").assertExists()
        composeTestRule.onNodeWithText("Header offset").assertExists()
        composeTestRule.onNodeWithText("Home opens all apps").assertExists()
        composeTestRule.onNodeWithText("Show all apps on home").assertExists()
        composeTestRule.onNodeWithText("All apps on left side").assertExists()
        composeTestRule.onNodeWithText("Show widget indicator").assertExists()
        composeTestRule.onNodeWithText("Disable alphabet scrubber").assertExists()
        composeTestRule.onNodeWithText("Check for updates").assertExists()
    }

    @Test
    fun `when toggling behavior settings, should invoke corresponding action callbacks`() {
        var homeOpensAllAppsToggled = false
        var showAllAppsOnHomeToggled = false
        var leftHandedToggled = false
        var showWidgetDotsToggled = false
        var disableScrubberToggled = false
        var inAppUpdateToggled = false

        val actions = SettingsScreenActions(
            onToggleHomeButtonOpensAllApps = { homeOpensAllAppsToggled = true },
            onToggleShowAllAppsOnHome = { showAllAppsOnHomeToggled = true },
            onToggleLeftHandedMode = { leftHandedToggled = true },
            onToggleShowWidgetDots = { showWidgetDotsToggled = true },
            onToggleDisableAlphabetScrubber = { disableScrubberToggled = true },
            onToggleInAppUpdate = { inAppUpdateToggled = true }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsBehaviorScreen(
                    uiState = SettingsUiState(),
                    actions = actions,
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Home opens all apps").performScrollTo().performClick()
        assertTrue(homeOpensAllAppsToggled)

        composeTestRule.onNodeWithText("Show all apps on home").performScrollTo().performClick()
        assertTrue(showAllAppsOnHomeToggled)

        composeTestRule.onNodeWithText("All apps on left side").performScrollTo().performClick()
        assertTrue(leftHandedToggled)

        composeTestRule.onNodeWithText("Show widget indicator").performScrollTo().performClick()
        assertTrue(showWidgetDotsToggled)

        composeTestRule.onNodeWithText("Disable alphabet scrubber").performScrollTo().performClick()
        assertTrue(disableScrubberToggled)

        composeTestRule.onNodeWithText("Check for updates").performScrollTo().performClick()
        assertTrue(inAppUpdateToggled)
    }
}
