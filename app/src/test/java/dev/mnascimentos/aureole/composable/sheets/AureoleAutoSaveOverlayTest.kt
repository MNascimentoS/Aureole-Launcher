package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.composable.sheets.model.AutoSaveOverlayParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val DISMISS_ANIMATION_MS = 500L

@RunWith(RobolectricTestRunner::class)
class AureoleAutoSaveOverlayTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private val events = mutableListOf<String>()

    private fun setOverlay(changed: Boolean) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleAutoSaveOverlay(
                    params = AutoSaveOverlayParams(
                        title = "Title",
                        isChanged = { changed },
                        onCommit = { events += "commit" },
                        onDismissRequest = { events += "dismiss" }
                    ),
                    preview = { AureoleText(text = "Preview body") }
                ) {
                    AureoleText(text = "Sheet body")
                }
            }
        }
    }

    private fun pressBackAndSettle() {
        composeTestRule.waitForIdle()
        composeTestRule.runOnUiThread { composeTestRule.activity.onBackPressedDispatcher.onBackPressed() }
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)
        composeTestRule.waitForIdle()
    }

    @Test
    fun `shows the title, the preview and the content, with no buttons`() {
        setOverlay(changed = true)

        composeTestRule.onNodeWithText("Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Preview body").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sheet body").assertIsDisplayed()
    }

    @Test
    fun `closing after a change saves once and then dismisses`() {
        setOverlay(changed = true)

        pressBackAndSettle()

        assertEquals(listOf("commit", "dismiss"), events)
    }

    @Test
    fun `closing without a change only dismisses`() {
        setOverlay(changed = false)

        pressBackAndSettle()

        assertEquals(listOf("dismiss"), events)
    }
}
