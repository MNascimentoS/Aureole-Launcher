package dev.mnascimentos.aureole.feature.home.components.widgets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupActions
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupParams
import org.junit.Assert.assertEquals
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
    fun `when rendering EditWidgetGroupScreen, should display title and indicator switch label`() {
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
    }

    @Test
    fun `when rendering preview carousel with widgets, should render title`() {
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

        composeTestRule.onNodeWithText("Widget Stack").assertIsDisplayed()
    }

    private fun setScreen(isIndicatorEnabled: Boolean, onToggle: () -> Unit, onAdd: () -> Unit) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditWidgetGroupScreen(
                    params = EditWidgetGroupParams(
                        item = LauncherItemState(id = "item_1"),
                        widgets = listOf(10),
                        isIndicatorEnabled = isIndicatorEnabled
                    ),
                    actions = EditWidgetGroupActions(
                        onToggleIndicator = onToggle,
                        onRemoveWidget = {},
                        onAddWidgetClick = onAdd,
                        onDismissRequest = {}
                    )
                )
            }
        }
        advanceAnimation()
    }

    @Test
    fun `when rendering, should show the indicator row and no preview label`() {
        setScreen(isIndicatorEnabled = true, onToggle = {}, onAdd = {})

        composeTestRule.onNodeWithText("LIVE PREVIEW").assertDoesNotExist()
        composeTestRule.onNodeWithText("Show widget indicator").assertIsDisplayed()
        composeTestRule.onNodeWithText("•••").assertDoesNotExist()
    }

    @Test
    fun `when tapping the indicator row, should toggle the position indicator`() {
        var toggled = 0
        setScreen(isIndicatorEnabled = false, onToggle = { toggled++ }, onAdd = {})

        composeTestRule.onNodeWithText("Show widget indicator").performClick()

        assertEquals(1, toggled)
    }

    private fun setStack(widgets: List<Int>, onReorder: (List<Int>) -> Unit) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditWidgetGroupScreen(
                    params = EditWidgetGroupParams(item = LauncherItemState(id = "item_1"), widgets = widgets),
                    actions = EditWidgetGroupActions(
                        onToggleIndicator = {},
                        onRemoveWidget = {},
                        onAddWidgetClick = {},
                        onDismissRequest = {},
                        onReorderWidgets = onReorder
                    )
                )
            }
        }
        advanceAnimation()
    }

    @Test
    fun `when the stack has three widgets, should show the position and disable Move earlier on the first`() {
        setStack(listOf(1, 2, 3), onReorder = {})

        composeTestRule.onNodeWithText("1 of 3").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Move Widget earlier").assertIsNotEnabled()
        composeTestRule.onNodeWithContentDescription("Move Widget later").assertIsEnabled()
    }

    @Test
    fun `when tapping Move later, should reorder the stack and report the new order`() {
        var order: List<Int>? = null
        setStack(listOf(1, 2, 3), onReorder = { order = it })

        composeTestRule.onNodeWithContentDescription("Move Widget later").performClick()
        advanceAnimation()

        assertEquals(listOf(2, 1, 3), order)
        composeTestRule.onNodeWithText("2 of 3").assertIsDisplayed()
    }

    @Test
    fun `when the stack has one widget, should disable both Move buttons`() {
        setStack(listOf(1), onReorder = {})

        composeTestRule.onNodeWithContentDescription("Move Widget earlier").assertIsNotEnabled()
        composeTestRule.onNodeWithContentDescription("Move Widget later").assertIsNotEnabled()
    }
}
