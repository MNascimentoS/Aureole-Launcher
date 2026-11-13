package dev.mnascimentos.aureole.composable.menu

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AureoleSwitchRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when tapping the row, should report the opposite value`() {
        val values = mutableListOf<Boolean>()
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleSwitchRow(SwitchRowParams(title = "Option", checked = false, onCheckedChange = { values += it }))
            }
        }

        composeTestRule.onNodeWithText("Option").assertIsOff().performClick()

        assertEquals(listOf(true), values)
    }

    @Test
    fun `when checked with a description, should show both texts and the on state`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleSwitchRow(
                    SwitchRowParams(title = "Option", checked = true, onCheckedChange = {}, description = "Details")
                )
            }
        }

        composeTestRule.onNodeWithText("Option").assertIsOn()
        composeTestRule.onNodeWithText("Details").assertIsDisplayed()
    }

    @Test
    fun `when a menu row has a subtitle, should show it under the title`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleMenuRow(MenuRowParams(title = "Title", subtitle = "Subtitle", onClick = {}))
            }
        }

        composeTestRule.onNodeWithText("Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Subtitle").assertIsDisplayed()
    }
}
