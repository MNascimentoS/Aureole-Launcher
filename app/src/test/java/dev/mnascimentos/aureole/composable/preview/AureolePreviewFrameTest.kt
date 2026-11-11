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
    fun `when rendering, should show only the content and no label`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureolePreviewFrame { AureoleText(text = "Content") }
            }
        }

        composeTestRule.onNodeWithText("Content").assertIsDisplayed()
        composeTestRule.onNodeWithText("LIVE PREVIEW").assertDoesNotExist()
    }
}
