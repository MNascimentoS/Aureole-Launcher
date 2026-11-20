package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Style › Layout and Message, and Format › Date with a layout without date (RF-H1, RF-H2, RF-H4). */
@RunWith(RobolectricTestRunner::class)
class EditClockLayoutSectionsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setStyleSection(uiState: MainUiState, actions: HomeScreenActions = createDefaultHomeScreenActions()) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    EditClockStyleSection(
                        uiState = uiState,
                        actions = actions,
                        tempCustomGreeting = "",
                        onGreetingChange = {}
                    )
                }
            }
        }
    }

    @Test
    fun `when the layout has no message, should hide the message section`() {
        setStyleSection(MainUiState(clockStyle = "TIME_ON_TOP"))

        composeTestRule.onNodeWithText("LAYOUT").assertIsDisplayed()
        assertEquals(0, countOf(hasText("MESSAGE")))
    }

    @Test
    fun `when the layout has a message, should offer the greeting and the custom text`() {
        var picked: String? = null
        val actions = createDefaultHomeScreenActions().copy(onUpdateClockMessage = { picked = it })
        setStyleSection(MainUiState(clockStyle = "MSG_ON_TOP"), actions)

        composeTestRule.onNodeWithText("MESSAGE").performScrollTo().assertIsDisplayed()
        assertEquals(0, countOf(hasSetTextAction()))
        composeTestRule.onNodeWithText("Custom text").performScrollTo().performClick()

        assertEquals("CUSTOM", picked)
    }

    @Test
    fun `when the message is the custom text, should show the text field`() {
        setStyleSection(MainUiState(clockStyle = "MSG_ON_BOTTOM", clockMessage = "CUSTOM"))

        composeTestRule.onNode(hasSetTextAction()).performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `when the layout has no date, should disable the date formats and show the notice`() {
        var pickedDate: String? = null
        val actions = createDefaultHomeScreenActions().copy(onUpdateClockDateFormat = { pickedDate = it })
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockFormatSection(uiState = MainUiState(clockStyle = "TIME_ONLY"), actions = actions)
            }
        }

        composeTestRule.onNodeWithText("This layout doesn't show the date.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Short").assertIsNotEnabled().performClick()

        assertNull(pickedDate)
    }

    @Test
    fun `when the layout has a date, should keep the date formats enabled without the notice`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockFormatSection(
                    uiState = MainUiState(clockStyle = "TIME_ON_TOP"),
                    actions = createDefaultHomeScreenActions()
                )
            }
        }

        assertEquals(0, countOf(hasText("This layout doesn't show the date.")))
    }

    private fun countOf(matcher: SemanticsMatcher) = composeTestRule.onAllNodes(matcher).fetchSemanticsNodes().size
}
