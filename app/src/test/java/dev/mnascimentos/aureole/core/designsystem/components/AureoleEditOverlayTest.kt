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
class AureoleEditOverlayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when edit overlay is visible, should display preview slot and sheet content`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleEditOverlay(
                    onDismissRequest = {},
                    title = "Edit Title",
                    animated = false,
                    previewContent = { Text("Overlay Preview Area") }
                ) {
                    Text("Overlay Sheet Body")
                }
            }
        }

        composeTestRule.onNodeWithText("Edit Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Overlay Preview Area").assertIsDisplayed()
        composeTestRule.onNodeWithText("Overlay Sheet Body").assertIsDisplayed()
    }

    @Test
    fun `when rendering preview, should display overlay content`() {
        composeTestRule.setContent {
            AureoleEditOverlayPreview()
        }

        composeTestRule.onNodeWithText("Edit Section").assertIsDisplayed()
        composeTestRule.onNodeWithText("Preview Slot").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sheet Content").assertIsDisplayed()
    }

    @Test
    fun `when edit overlay is visible and animated, should display content`() {
        var backPressed = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleEditOverlay(
                    onDismissRequest = {},
                    animated = true,
                    isVisible = true,
                    onBackPress = { backPressed = true },
                    onSettingsClick = {},
                    bottomBarContent = { Text("Bottom Bar") },
                    previewContent = { Text("Animated Preview Area") }
                ) {
                    Text("Animated Sheet Body")
                }
            }
        }

        composeTestRule.onNodeWithText("Animated Preview Area").assertIsDisplayed()
        composeTestRule.onNodeWithText("Animated Sheet Body").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bottom Bar").assertIsDisplayed()
    }
}
