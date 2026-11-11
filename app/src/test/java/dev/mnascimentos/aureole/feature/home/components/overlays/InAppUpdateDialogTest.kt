package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val DISMISS_ANIMATION_MS = 500L

@RunWith(RobolectricTestRunner::class)
class InAppUpdateDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when update available dialog is displayed, should show content and handle confirm`() {
        var confirmed = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                UpdateAvailableDialog(
                    onConfirmUpdate = { confirmed = true },
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("New update available").assertIsDisplayed()
        composeTestRule.onNodeWithText("Update now").performClick()

        assertTrue(confirmed)
    }

    @Test
    fun `when update available dialog is displayed, should handle dismiss`() {
        var dismissed = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                UpdateAvailableDialog(
                    onConfirmUpdate = {},
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Not now").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)

        assertTrue(dismissed)
    }

    @Test
    fun `when update downloaded dialog is displayed, should show content and handle confirm`() {
        var confirmed = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                UpdateDownloadedDialog(
                    onConfirmRestart = { confirmed = true },
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Update ready").assertIsDisplayed()
        composeTestRule.onNodeWithText("Restart now").performClick()

        assertTrue(confirmed)
    }

    @Test
    fun `when update downloaded dialog is displayed, should handle dismiss`() {
        var dismissed = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                UpdateDownloadedDialog(
                    onConfirmRestart = {},
                    onDismiss = { dismissed = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Later").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)

        assertTrue(dismissed)
    }
}
