package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.testing.closeOverlayWithBack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val DISMISS_ANIMATION_MS = 500L

@RunWith(RobolectricTestRunner::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when errorMessage is present, should handle composition without throwing exception`() {
        var clearErrorCalled = false
        val state = SettingsUiState(errorMessage = TextResource.fromString("Test Error Message"))
        val actions = SettingsScreenActions(onClearErrorMessage = { clearErrorCalled = true })

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsScreen(uiState = state, actions = actions)
            }
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun `when showCreateFolderDialog is true, should render create folder dialog`() {
        var dismissCalled = false
        val state = SettingsUiState(showCreateFolderDialog = true)
        val actions = SettingsScreenActions(
            onDismissCreateFolderDialog = { dismissCalled = true }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsScreen(uiState = state, actions = actions)
            }
        }

        composeTestRule.onNodeWithText("Create").assertExists()
        composeTestRule.closeOverlayWithBack()
        assertTrue(dismissCalled)
    }

    @Test
    fun `when showResetGridDialog is true, should handle restore and cancel actions`() {
        var confirmCalled = false
        var dismissCount = 0
        val state = SettingsUiState(showResetGridDialog = true)
        val actions = SettingsScreenActions(
            onConfirmResetGrid = { confirmCalled = true },
            onDismissResetGridDialog = { dismissCount++ }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsScreen(uiState = state, actions = actions)
            }
        }

        composeTestRule.onNodeWithText("Reset layout?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Restore").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)
        assertTrue(confirmCalled)
        assertEquals(1, dismissCount)
    }

    @Test
    fun `when showResetGridDialog is true, cancel only dismisses`() {
        var confirmCalled = false
        var dismissCount = 0
        val state = SettingsUiState(showResetGridDialog = true)
        val actions = SettingsScreenActions(
            onConfirmResetGrid = { confirmCalled = true },
            onDismissResetGridDialog = { dismissCount++ }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsScreen(uiState = state, actions = actions)
            }
        }

        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)
        assertFalse(confirmCalled)
        assertEquals(1, dismissCount)
    }
}
