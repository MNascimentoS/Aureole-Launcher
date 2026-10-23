package dev.mnascimentos.aureole.feature.settings.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when errorMessage is present, should handle composition without throwing exception`() {
        var clearErrorCalled = false
        val state = SettingsUiState(errorMessage = "Test Error Message")
        val actions = SettingsScreenActions(onClearErrorMessage = { clearErrorCalled = true })

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsScreen(uiState = state, actions = actions)
            }
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun `when showHazeOpacityDialog is true, should render dialog and handle opacity selection and dismiss`() {
        var selectedOpacity = 0f
        var dismissCalled = false
        val state = SettingsUiState(showHazeOpacityDialog = true)
        val actions = SettingsScreenActions(
            onHazeOpacitySelected = { selectedOpacity = it },
            onDismissHazeOpacityDialog = { dismissCalled = true }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsScreen(uiState = state, actions = actions)
            }
        }

        composeTestRule.onNodeWithText("Blur Opacity").assertIsDisplayed()
        composeTestRule.onNodeWithText("Solid").performClick()
        assertEquals(0.90f, selectedOpacity)

        composeTestRule.onNodeWithText("Cancel").performClick()
        assertTrue(dismissCalled)
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

        composeTestRule.onNodeWithText("Escolher Cor Primária").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancelar").performClick()
        assertTrue(dismissCalled)
    }

    @Test
    fun `when showRestoreWallpaperDialog is true, should handle confirm and dismiss actions`() {
        var confirmCalled = false
        var dismissCalled = false
        val state = SettingsUiState(showRestoreWallpaperDialog = true)
        val actions = SettingsScreenActions(
            onConfirmRestoreWallpaper = { confirmCalled = true },
            onDismissRestoreWallpaperDialog = { dismissCalled = true }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsScreen(uiState = state, actions = actions)
            }
        }

        composeTestRule.onNodeWithText("Restore default background?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Yes").performClick()
        assertTrue(confirmCalled)

        composeTestRule.onNodeWithText("No").performClick()
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
        assertTrue(confirmCalled)
        assertEquals(1, dismissCount)

        composeTestRule.onNodeWithText("Cancel").performClick()
        assertEquals(2, dismissCount)
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

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsScreen(uiState = state, actions = actions)
            }
        }

        composeTestRule.onNodeWithText("Reset launcher?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Yes").performClick()
        assertTrue(factoryResetConfirmed)

        composeTestRule.onNodeWithText("No").performClick()
        assertTrue(factoryResetDismissed)
    }
}
