package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.model.AureolePopupHeaderParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AureolePopupItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering popup menu item, should display title and badge`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureolePopupMenuItem(
                    title = "Menu Item Title",
                    params = AureolePopupMenuItemParams(badge = "99"),
                    onClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Menu Item Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("99").assertIsDisplayed()
    }

    @Test
    fun `when popup menu item is clicked, should invoke onClick callback`() {
        var clicked = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureolePopupMenuItem(
                    title = "Clickable Action",
                    onClick = { clicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Clickable Action").performClick()
        assertTrue(clicked)
    }

    @Test
    fun `when rendering custom icons and trailing icons, should display them`() {
        var trailingClicked = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureolePopupMenuItem(
                    title = "Menu Item Title",
                    onClick = {},
                    params = AureolePopupMenuItemParams(
                        customIcon = { Text("CustomIcon") },
                        trailingIcon = { Text("TrailingIcon") },
                        onTrailingClick = { trailingClicked = true }
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("CustomIcon").assertIsDisplayed()
        composeTestRule.onNodeWithText("TrailingIcon").assertIsDisplayed()

        composeTestRule.onNodeWithText("TrailingIcon").performClick()
        assertTrue(trailingClicked)
    }

    @Test
    fun `when rendering preview, should display all variations`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureolePopupBox {
                    AureolePopupHeader(
                        title = "Folder name",
                        params = AureolePopupHeaderParams(
                            sessionTitle = "Titulo de sessão"
                        )
                    )
                    AureolePopupMenuItem(title = "Add folder", onClick = {})
                    AureolePopupMenuItem(title = "Create stack", onClick = {})
                    AureolePopupMenuItem(
                        title = "Remove",
                        params = AureolePopupMenuItemParams(isDestructive = true),
                        onClick = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Folder name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Titulo de sessão").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add folder").assertIsDisplayed()
        composeTestRule.onNodeWithText("Create stack").assertIsDisplayed()
        composeTestRule.onNodeWithText("Remove").assertIsDisplayed()
    }
}
