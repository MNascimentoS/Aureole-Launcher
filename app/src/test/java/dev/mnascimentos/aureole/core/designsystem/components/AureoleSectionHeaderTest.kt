package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AureoleSectionHeaderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when section header rendered, should display title`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleSectionHeader(title = "Section Header Title")
            }
        }

        composeTestRule.onNodeWithText("Section Header Title").assertIsDisplayed()
    }

    @Test
    fun `when section header preview rendered, should display title`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleSectionHeader(title = "A")
            }
        }

        composeTestRule.onNodeWithText("A").assertIsDisplayed()
    }

    @Test
    fun `when section header rendered with empty text, should not crash`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleSectionHeader(
                    title = "",
                    color = Color.Red,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
