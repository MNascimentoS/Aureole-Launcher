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
class AureoleEditOverlaySheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering overlay sheet body with title, should display header title`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                OverlaySheetBody(
                    title = "Overlay Sheet Title",
                    onSettingsClick = {},
                    bottomBarContent = null,
                    sheetContent = { Text("Sheet Body Content") }
                )
            }
        }

        composeTestRule.onNodeWithText("Overlay Sheet Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sheet Body Content").assertIsDisplayed()
    }

    @Test
    fun `when rendering overlay sheet body with bottom bar, should display sheet content and bottom bar`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                OverlaySheetBody(
                    title = "Sheet with Bottom Bar",
                    onSettingsClick = {},
                    bottomBarContent = { Text("Bottom Bar Item") },
                    sheetContent = { Text("Main Body Item") }
                )
            }
        }

        composeTestRule.onNodeWithText("Sheet with Bottom Bar").assertIsDisplayed()
        composeTestRule.onNodeWithText("Main Body Item").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bottom Bar Item").assertIsDisplayed()
    }
}
