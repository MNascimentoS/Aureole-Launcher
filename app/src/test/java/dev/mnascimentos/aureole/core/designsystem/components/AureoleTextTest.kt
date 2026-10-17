package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AureoleTextTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering text, should display given content`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleText(
                    text = "Aureole Launcher Text",
                    style = AureoleDS.typography.titleMedium
                )
            }
        }

        composeTestRule.onNodeWithText("Aureole Launcher Text").assertIsDisplayed()
    }

    @Test
    fun `when rendering text with custom params, should apply them`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleText(
                    text = "Custom Params Text",
                    style = AureoleDS.typography.titleMedium,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    minLines = 1,
                    softWrap = false,
                    letterSpacing = TextUnit.Unspecified,
                    lineHeight = TextUnit.Unspecified,
                    fontSize = TextUnit.Unspecified,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    textDecoration = TextDecoration.Underline,
                    onTextLayout = {}
                )
            }
        }
        composeTestRule.onNodeWithText("Custom Params Text").assertIsDisplayed()
    }

    @Test
    fun `when rendering text preview, should display text previews`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleText(
                    text = "Title Medium Text",
                    style = AureoleDS.typography.titleMedium
                )
                AureoleText(
                    text = "Body Medium Text",
                    style = AureoleDS.typography.bodyMedium
                )
                AureoleText(
                    text = "Label Small Text",
                    style = AureoleDS.typography.labelSmall
                )
            }
        }

        composeTestRule.onNodeWithText("Title Medium Text").assertIsDisplayed()
        composeTestRule.onNodeWithText("Body Medium Text").assertIsDisplayed()
        composeTestRule.onNodeWithText("Label Small Text").assertIsDisplayed()
    }
}
