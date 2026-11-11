package dev.mnascimentos.aureole.composable.menu

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.composable.menu.model.RadioOption
import dev.mnascimentos.aureole.composable.menu.model.RadioSheetParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AureoleRadioSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setSheet(onSelect: (String) -> Unit, onDismiss: () -> Unit) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleRadioSheet(
                    params = RadioSheetParams(
                        title = "Position",
                        options = listOf(RadioOption("Left", "Left label"), RadioOption("Right", "Right label")),
                        selectedValue = "Right"
                    ),
                    onSelect = onSelect,
                    onDismiss = onDismiss
                )
            }
        }
    }

    @Test
    fun `when an option is clicked, should select it and only dismiss after the exit animation`() {
        val selected = mutableListOf<String>()
        var dismissCount = 0
        setSheet(onSelect = { selected += it }, onDismiss = { dismissCount++ })

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.onNodeWithText("Left label").performClick()
        composeTestRule.mainClock.advanceTimeBy(100)

        assertEquals(listOf("Left"), selected)
        assertEquals(0, dismissCount)

        composeTestRule.mainClock.advanceTimeBy(400)
        composeTestRule.waitForIdle()
        assertEquals(1, dismissCount)
    }
}
