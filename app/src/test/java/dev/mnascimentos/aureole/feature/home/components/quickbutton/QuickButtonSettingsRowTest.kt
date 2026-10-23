package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuickButtonSettingsRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering settings row with title and value, should display both texts`() {
        // Arrange
        var clicked = false

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsRow(
                    title = "Appearance",
                    icon = { m -> AureoleDS.icons.Edit(m) },
                    value = "Custom Theme",
                    onClick = { clicked = true }
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Appearance").assertIsDisplayed()
        composeTestRule.onNodeWithText("Custom Theme").assertIsDisplayed()

        composeTestRule.onNodeWithText("Appearance").performClick()
        assertTrue("Expected onClick callback to be triggered", clicked)
    }

    @Test
    fun `when value is null, should render title without value text`() {
        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsRow(
                    title = "Remove Item",
                    icon = { m -> AureoleDS.icons.Edit(m) },
                    value = null,
                    onClick = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Remove Item").assertIsDisplayed()
    }
}
