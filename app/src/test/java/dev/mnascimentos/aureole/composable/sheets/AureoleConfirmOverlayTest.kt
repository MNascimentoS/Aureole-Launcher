package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmOverlayParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val DISMISS_ANIMATION_MS = 500L

@RunWith(RobolectricTestRunner::class)
class AureoleConfirmOverlayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val events = mutableListOf<String>()

    private fun setOverlay(secondaryLabel: String? = null, withExtra: Boolean = false) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleConfirmOverlay(
                    params = ConfirmOverlayParams(
                        title = "Title",
                        primaryLabel = "Go",
                        onPrimary = { events += "primary" },
                        onDismissRequest = { events += "dismiss" },
                        secondaryLabel = secondaryLabel
                    ),
                    preview = { AureoleText(text = "Preview body") },
                    bottomExtra = if (withExtra) ({ AureoleText(text = "Extra field") }) else null
                ) {
                    AureoleText(text = "Sheet body")
                }
            }
        }
    }

    @Test
    fun `shows the title, the preview, the content and both buttons`() {
        setOverlay()

        composeTestRule.onNodeWithText("Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Preview body").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sheet body").assertIsDisplayed()
        composeTestRule.onNodeWithText("Go").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }

    @Test
    fun `the primary button runs the action and then closes`() {
        setOverlay()

        composeTestRule.onNodeWithText("Go").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)

        assertEquals(listOf("primary", "dismiss"), events)
    }

    @Test
    fun `cancel only closes`() {
        setOverlay()

        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)

        assertEquals(listOf("dismiss"), events)
    }

    @Test
    fun `a custom secondary label replaces Cancel and the extra slot sits in the bottom bar`() {
        setOverlay(secondaryLabel = "Back", withExtra = true)

        composeTestRule.onNodeWithText("Back").assertIsDisplayed()
        composeTestRule.onNodeWithText("Extra field").assertIsDisplayed()
    }
}
