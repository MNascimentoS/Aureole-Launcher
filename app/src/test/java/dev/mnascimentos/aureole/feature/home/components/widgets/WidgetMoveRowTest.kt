package dev.mnascimentos.aureole.feature.home.components.widgets

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetMoveParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.moveLabelText
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WidgetMoveRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun show(isLeftHanded: Boolean, previous: (() -> Unit)?) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalLeftHanded provides isLeftHanded) {
                    WidgetMoveRow(
                        WidgetMoveParams("Clock", position = 1, count = 3, onPrevious = previous, onNext = {})
                    )
                }
            }
        }
    }

    @Test
    fun `label text keeps the reading order in the left-handed mode`() {
        show(isLeftHanded = true, previous = null)
        composeTestRule.onNodeWithText("Move Clock").assertIsDisplayed()
    }

    @Test
    fun `label text is one node in the right-handed mode`() {
        show(isLeftHanded = false, previous = null)
        composeTestRule.onNodeWithText("Move Clock").assertIsDisplayed()
    }

    @Test
    fun `left-handed label sits on the far right and the buttons on the thumb side`() {
        show(isLeftHanded = true, previous = {})
        val label = composeTestRule.onNodeWithText("Move Clock").getUnclippedBoundsInRoot()
        val previous = composeTestRule.onNodeWithContentDescription("Move Clock earlier").getUnclippedBoundsInRoot()
        assertTrue(label.left >= previous.right)
    }

    @Test
    fun `arrow buttons have a 48 dp target and disable on a null step`() {
        show(isLeftHanded = false, previous = null)
        val previous = composeTestRule.onNodeWithContentDescription("Move Clock earlier")
        previous.assertIsNotEnabled()
        val bounds = previous.getUnclippedBoundsInRoot()
        assertEquals(48.dp, bounds.right - bounds.left)
        composeTestRule.onNodeWithContentDescription("Move Clock later").assertIsEnabled()
    }

    @Test
    fun `moveLabelText joins the verb and the name with a space`() {
        val text = moveLabelText("Mover", "Relógio", Color.Gray, Color.White)
        assertEquals("Mover Relógio", text.text)
        assertEquals(2, text.spanStyles.size)
    }
}
