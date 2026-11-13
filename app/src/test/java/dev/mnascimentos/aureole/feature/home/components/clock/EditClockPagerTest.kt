package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import androidx.compose.ui.test.swipeRight
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditClockPagerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val stylePage = hasContentDescription("Style tab", substring = true)

    private fun setSheet() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockBottomSheet(
                    uiState = MainUiState(),
                    actions = createDefaultHomeScreenActions(),
                    onDismissRequest = {}
                )
            }
        }
        settle()
    }

    private fun settle() {
        composeTestRule.mainClock.advanceTimeBy(1_000L)
        composeTestRule.waitForIdle()
    }

    @Test
    fun `when sheet opens, should select the style tab`() {
        setSheet()

        composeTestRule.onNode(hasText("Style") and isSelectable()).assertIsSelected()
    }

    @Test
    fun `when tapping a tab, should show its content and select it`() {
        setSheet()

        composeTestRule.onNodeWithText("Colors").performClick()
        settle()

        composeTestRule.onNode(hasText("Colors") and isSelectable()).assertIsSelected()
        composeTestRule.onNodeWithText("TEXT COLOR").assertExists()
    }

    @Test
    fun `when swiping content left, should move to the next tab`() {
        setSheet()

        composeTestRule.onNode(stylePage).performTouchInput { swipeLeft() }
        settle()

        composeTestRule.onNode(hasText("Format") and isSelectable()).assertIsSelected()
    }

    @Test
    fun `when swiping right on the first tab, should stay on it`() {
        setSheet()

        composeTestRule.onNode(stylePage).performTouchInput { swipeRight() }
        settle()

        composeTestRule.onNode(hasText("Style") and isSelectable()).assertIsSelected()
    }
}
