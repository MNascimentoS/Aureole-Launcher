package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShortcutSelectionBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when shortcut selection sheet is rendered, should display title and allow selecting option`() {
        // Arrange
        var updatedShortcuts: QuickButtonShortcuts? = null
        val actions = createDefaultHomeScreenActions().copy(
            onUpdateQuickButtonShortcuts = { updatedShortcuts = it }
        )
        val uiState = MainUiState(
            activeQuickButtonModel = QuickButtonModel(),
            editingShortcutType = "click"
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ShortcutSelectionBottomSheet(
                    uiState = uiState,
                    actions = actions,
                    onDismissRequest = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Select a shortcut").assertIsDisplayed()
        composeTestRule.onNodeWithText("Aureole Settings").performClick()

        assertNotNull(updatedShortcuts)
        assertEquals(ShortcutAction.Settings, updatedShortcuts!!.click)
    }

    @Test
    fun `when editing type is press, should update press shortcut action`() {
        // Arrange
        var updatedShortcuts: QuickButtonShortcuts? = null
        val actions = createDefaultHomeScreenActions().copy(
            onUpdateQuickButtonShortcuts = { updatedShortcuts = it }
        )
        val uiState = MainUiState(
            activeQuickButtonModel = QuickButtonModel(),
            editingShortcutType = "press"
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ShortcutSelectionBottomSheet(
                    uiState = uiState,
                    actions = actions,
                    onDismissRequest = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Aureole Settings").performClick()

        assertNotNull(updatedShortcuts)
        assertEquals(ShortcutAction.Settings, updatedShortcuts!!.press)
    }

    @Test
    fun `when editing type is swipeUp, should update swipeUp shortcut action`() {
        // Arrange
        var updatedShortcuts: QuickButtonShortcuts? = null
        val actions = createDefaultHomeScreenActions().copy(
            onUpdateQuickButtonShortcuts = { updatedShortcuts = it }
        )
        val uiState = MainUiState(
            activeQuickButtonModel = QuickButtonModel(),
            editingShortcutType = "swipeUp"
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ShortcutSelectionBottomSheet(
                    uiState = uiState,
                    actions = actions,
                    onDismissRequest = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Aureole Settings").performClick()

        assertNotNull(updatedShortcuts)
        assertEquals(ShortcutAction.Settings, updatedShortcuts!!.swipeUp)
    }
}
