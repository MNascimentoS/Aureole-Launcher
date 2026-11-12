package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.actions.model.HOLD_CONFIRM_MS
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmDialogParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val DISMISS_ANIMATION_MS = 500L

@RunWith(RobolectricTestRunner::class)
class AureoleConfirmDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val events = mutableListOf<String>()

    private fun setDialog(holdMs: Long? = null, isLeftHanded: Boolean = false, cancelLabel: String? = null) {
        composeTestRule.setContent {
            Themed(isLeftHanded) {
                AureoleConfirmDialog(
                    params = ConfirmDialogParams(
                        title = "Reset?",
                        message = "Explanation",
                        confirmLabel = "Confirm",
                        onConfirm = { events += "confirm" },
                        onDismiss = { events += "dismiss" },
                        cancelLabel = cancelLabel,
                        holdMs = holdMs
                    )
                )
            }
        }
    }

    @Composable
    private fun Themed(isLeftHanded: Boolean, content: @Composable () -> Unit) {
        ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
            AureoleLauncherTheme { content() }
        }
    }

    private fun left(text: String) = composeTestRule.onNodeWithText(text).getUnclippedBoundsInRoot().left

    @Test
    fun `shows the title, the message and both buttons`() {
        setDialog()

        composeTestRule.onNodeWithText("Reset?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Explanation").assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirm").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun `confirm runs the action and then closes`() {
        setDialog()

        composeTestRule.onNodeWithText("Confirm").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)

        assertEquals(listOf("confirm", "dismiss"), events)
    }

    @Test
    fun `cancel only closes and a custom label replaces Cancel`() {
        setDialog(cancelLabel = "Not now")

        composeTestRule.onNodeWithText("Not now").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)

        assertEquals(listOf("dismiss"), events)
    }

    @Test
    fun `confirm sits on the thumb side for each hand`() {
        setDialog()
        assertTrue(left("Cancel") < left("Confirm"))
    }

    @Test
    fun `confirm sits on the left for the left-handed mode`() {
        setDialog(isLeftHanded = true)
        assertTrue(left("Confirm") < left("Cancel"))
    }

    @Test
    fun `the hold button sits opposite the thumb`() {
        setDialog(holdMs = HOLD_CONFIRM_MS)
        assertTrue(left("Confirm") < left("Cancel"))
    }

    @Test
    fun `the hold button sits opposite the thumb in the left-handed mode`() {
        setDialog(holdMs = HOLD_CONFIRM_MS, isLeftHanded = true)
        assertTrue(left("Cancel") < left("Confirm"))
    }

    @Test
    fun `hold confirm ignores a tap and a short press and fires after the hold time`() {
        composeTestRule.mainClock.autoAdvance = false
        setDialog(holdMs = HOLD_CONFIRM_MS)

        composeTestRule.onNodeWithText("Confirm").performClick()
        composeTestRule.onNodeWithText("Confirm").performTouchInput { down(center) }
        composeTestRule.mainClock.advanceTimeBy(HOLD_CONFIRM_MS / 2)
        composeTestRule.onNodeWithText("Confirm").performTouchInput { up() }
        composeTestRule.mainClock.advanceTimeBy(HOLD_CONFIRM_MS)
        assertTrue(events.isEmpty())

        composeTestRule.onNodeWithText("Confirm").performTouchInput { down(center) }
        composeTestRule.mainClock.advanceTimeBy(HOLD_CONFIRM_MS + 100L)
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)

        assertEquals(listOf("confirm", "dismiss"), events)
    }
}
