package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.rememberAureoleEditOverlayState
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
                    params = AureoleEditOverlayParams(
                        config = AureoleEditOverlayConfig(
                            title = "Edit Title",
                            animated = false
                        ),
                        previewContent = { Text("Overlay Preview Area") },
                        sheetContent = { Text("Overlay Sheet Body") }
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Edit Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Overlay Preview Area").assertIsDisplayed()
        composeTestRule.onNodeWithText("Overlay Sheet Body").assertIsDisplayed()
    }

    @Test
    fun `when rendering preview, should display overlay content`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleEditOverlay(
                    onDismissRequest = {},
                    params = AureoleEditOverlayParams(
                        config = AureoleEditOverlayConfig(
                            title = "Edit Section",
                            animated = false
                        ),
                        previewContent = { Text("Preview Slot") },
                        sheetContent = { Text("Sheet Content") }
                    )
                )
            }
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
                    params = AureoleEditOverlayParams(
                        config = AureoleEditOverlayConfig(
                            animated = true,
                            isVisible = true,
                            onBackPress = { backPressed = true },
                            onSettingsClick = {}
                        ),
                        bottomBarContent = { Text("Bottom Bar") },
                        previewContent = { Text("Animated Preview Area") },
                        sheetContent = { Text("Animated Sheet Body") }
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Animated Preview Area").assertIsDisplayed()
        composeTestRule.onNodeWithText("Animated Sheet Body").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bottom Bar").assertIsDisplayed()
    }

    @Test
    fun `when using remembered edit overlay state, should display correctly`() {
        composeTestRule.setContent {
            val state = rememberAureoleEditOverlayState()
            AureoleLauncherTheme {
                AureoleEditOverlay(
                    state = state,
                    onDismissRequest = {},
                    params = AureoleEditOverlayParams(
                        config = AureoleEditOverlayConfig(
                            title = "Stateful Overlay",
                            animated = false
                        ),
                        sheetContent = { Text("Stateful Sheet Content") }
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Stateful Overlay").assertIsDisplayed()
        composeTestRule.onNodeWithText("Stateful Sheet Content").assertIsDisplayed()
    }
}
