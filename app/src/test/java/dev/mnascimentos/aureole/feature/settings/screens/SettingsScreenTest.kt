package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import dev.mnascimentos.aureole.composable.actions.model.HOLD_CONFIRM_MS
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val DISMISS_ANIMATION_MS = 500L
private const val HOLD_MARGIN_MS = 100L

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
    fun `when showColorPickerDialog is true, should render color picker dialog`() {
        var dismissCalled = false
        val state = SettingsUiState(showColorPickerDialog = true)
        val actions = SettingsScreenActions(
            onDismissColorPickerDialog = { dismissCalled = true }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsScreen(uiState = state, actions = actions)
            }
        }

        composeTestRule.onNodeWithText("Choose Primary Color").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)
        assertTrue(dismissCalled)
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
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)
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

        composeTestRule.onNodeWithText("Reset Layout?").assertIsDisplayed()
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

    @Test
    fun `when secondary dialog flags are true, should handle position selections and factory reset`() {
        var selectedPos: String? = null
        var selectedBtnPos: String? = null
        var selectedSearchPos: String? = null
        var factoryResetConfirmed = false
        var factoryResetDismissed = false

        val state = SettingsUiState(
            showContainerPositionDialog = true,
            showSettingsButtonPositionDialog = true,
            showSearchIconPositionDialog = true,
            showFactoryResetDialog = true
        )
        val actions = SettingsScreenActions(
            onContainerPositionSelected = { selectedPos = it },
            onSettingsButtonPositionSelected = { selectedBtnPos = it },
            onSearchIconPositionSelected = { selectedSearchPos = it },
            onConfirmFactoryReset = { factoryResetConfirmed = true },
            onDismissFactoryResetDialog = { factoryResetDismissed = true }
        )

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsScreen(uiState = state, actions = actions)
            }
        }

        composeTestRule.onNodeWithText("Reset launcher?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hold to reset").performClick()
        assertFalse(factoryResetConfirmed)

        composeTestRule.onNodeWithText("Hold to reset").performTouchInput { down(center) }
        composeTestRule.mainClock.advanceTimeBy(HOLD_CONFIRM_MS + HOLD_MARGIN_MS)
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)
        assertTrue(factoryResetConfirmed)
        assertTrue(factoryResetDismissed)
    }
}
