package dev.mnascimentos.aureole.feature.home.components.widgets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupActions
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupParams
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditWidgetGroupScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun advanceAnimation() {
        composeTestRule.mainClock.advanceTimeBy(500L)
        composeTestRule.waitForIdle()
    }

    @Test
    fun `when rendering EditWidgetGroupScreen, should display title, indicator switch label and save button`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditWidgetGroupScreen(
                    params = EditWidgetGroupParams(
                        item = LauncherItemState(id = "item_1"),
                        widgets = listOf(1, 2),
                    ),
                    actions = EditWidgetGroupActions(
                        onToggleIndicator = {},
                        onRemoveWidget = {},
                        onAddWidgetClick = {},
                        onDismissRequest = {},
                    )
                )
            }
        }
        composeTestRule.mainClock.advanceTimeBy(1000L)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Widget Stack").assertIsDisplayed()
        composeTestRule.onNodeWithText("Show widget indicator").assertIsDisplayed()
        composeTestRule.onNodeWithText("Salvar").assertIsDisplayed()
    }

    @Test
    fun `when rendering preview carousel with widgets, should render widget cards`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditWidgetGroupScreen(
                    params = EditWidgetGroupParams(
                        item = LauncherItemState(id = "item_1"),
                        widgets = listOf(10, 20)
                    ),
                    actions = EditWidgetGroupActions(
                        onToggleIndicator = {},
                        onRemoveWidget = {},
                        onAddWidgetClick = {},
                        onDismissRequest = {}
                    )
                )
            }
        }
        advanceAnimation()

        composeTestRule.onNodeWithText("Salvar").assertIsDisplayed()
    }

    @Test
    fun `when clicking save button, should request dismiss`() {
        var dismissCalled = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditWidgetGroupScreen(
                    params = EditWidgetGroupParams(
                        item = LauncherItemState(id = "item_1"),
                        widgets = listOf(10)
                    ),
                    actions = EditWidgetGroupActions(
                        onToggleIndicator = {},
                        onRemoveWidget = {},
                        onAddWidgetClick = {},
                        onDismissRequest = { dismissCalled = true }
                    )
                )
            }
        }
        advanceAnimation()

        composeTestRule.onNodeWithText("Salvar").performClick()
        advanceAnimation()

        assertTrue(dismissCalled)
    }
}
