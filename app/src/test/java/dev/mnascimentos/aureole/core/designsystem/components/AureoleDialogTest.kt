package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AureoleDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering dialog, should display title and content`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleDialog(
                    onDismissRequest = {},
                    title = "Dialog Title Test"
                ) {
                    Text("Dialog Body Test")
                }
            }
        }

        composeTestRule.onNodeWithText("Dialog Title Test").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dialog Body Test").assertIsDisplayed()
    }

    @Test
    fun `when rendering dialog header, should display title`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleDialogHeader(
                    title = "Header Title Test"
                )
            }
        }

        composeTestRule.onNodeWithText("Header Title Test").assertIsDisplayed()
    }

    @Test
    fun `when rendering dialog with icon, should display icon`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleDialog(
                    onDismissRequest = {},
                    title = "Dialog Title Test",
                    icon = { Text("DialogIcon") }
                ) {
                    Text("Dialog Body Test")
                }
            }
        }
        composeTestRule.onNodeWithText("DialogIcon").assertIsDisplayed()
    }

    @Test
    fun `when rendering preview, should display preview content`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleDialog(
                    onDismissRequest = {},
                    title = "Sample Dialog Title"
                ) {
                    Text("This is sample content inside AureoleDialog.")
                }
            }
        }
        composeTestRule.onNodeWithText("Sample Dialog Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("This is sample content inside AureoleDialog.").assertIsDisplayed()
    }
}
