package dev.mnascimentos.aureole.composable

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import dev.mnascimentos.aureole.composable.color.model.RecommendedColors
import dev.mnascimentos.aureole.composable.color.model.formatHex
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.testing.closeOverlayWithBack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ColorPickerDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setPicker(
        initial: Int = RecommendedColors[1],
        onSelected: (Int) -> Unit = {},
        onDismiss: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ColorPickerDialog(initialColor = initial, onColorSelected = onSelected, onDismiss = onDismiss)
            }
        }
    }

    @Test
    fun `when opened, should show the title and the hex of the initial color in the preview`() {
        setPicker(initial = 0xFF90CAF9.toInt())

        composeTestRule.onNodeWithText("Choose Primary Color").assertIsDisplayed()
        composeTestRule.onNodeWithText("#90CAF9").assertIsDisplayed()
    }

    @Test
    fun `when closing without changes, should not return a color`() {
        var selected: Int? = null
        var dismissed = false
        setPicker(onSelected = { selected = it }, onDismiss = { dismissed = true })

        composeTestRule.closeOverlayWithBack()

        assertTrue(dismissed)
        assertNull(selected)
    }

    @Test
    fun `when picking a swatch and closing, should update the preview and save that color`() {
        var selected: Int? = null
        setPicker(onSelected = { selected = it })

        composeTestRule.onAllNodes(isSelectable())[0].performSemanticsAction(SemanticsActions.OnClick)
        composeTestRule.onNodeWithText(formatHex(RecommendedColors[0])).assertIsDisplayed()
        composeTestRule.closeOverlayWithBack()

        assertEquals(RecommendedColors[0], selected)
    }
}
