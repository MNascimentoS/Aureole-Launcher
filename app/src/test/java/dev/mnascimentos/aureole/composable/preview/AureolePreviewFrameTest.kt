package dev.mnascimentos.aureole.composable.preview

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AureolePreviewFrameTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering, should upper-case the label and show the content`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureolePreviewFrame(label = "Live preview") { AureoleText(text = "Content") }
            }
        }

        composeTestRule.onNodeWithText("LIVE PREVIEW").assertIsDisplayed()
        composeTestRule.onNodeWithText("Content").assertIsDisplayed()
    }
}
