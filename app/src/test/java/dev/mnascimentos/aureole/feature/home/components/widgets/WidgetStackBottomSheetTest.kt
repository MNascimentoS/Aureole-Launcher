package dev.mnascimentos.aureole.feature.home.components.widgets

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetStackBottomSheetParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WidgetStackBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun advanceAnimation() {
        composeTestRule.mainClock.advanceTimeBy(500L)
        composeTestRule.waitForIdle()
    }

    @Test
    fun `when rendering WidgetStackBottomSheet with null widgetId, should display title and menu items`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                WidgetStackBottomSheet(
                    params = WidgetStackBottomSheetParams(
                        widgetId = null,
                        stackId = "top_widgets",
                        onEditStack = {},
                        onToggleDots = {},
                        onRemoveStack = {},
                    )
                ) { }
            }
        }
        advanceAnimation()

        composeTestRule.onNodeWithText("Widget Group").assertIsDisplayed()
        composeTestRule.onNodeWithText("Resize Group").assertIsDisplayed()
        composeTestRule.onNodeWithText("Toggle Position Indicator").assertIsDisplayed()
        composeTestRule.onNodeWithText("Remove").assertIsDisplayed()
    }

    @Test
    fun `when clicking Redimensionar Grupo, should call onEditStack and dismiss`() {
        var editCalled = false
        var dismissed = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                WidgetStackBottomSheet(
                    params = WidgetStackBottomSheetParams(
                        widgetId = 123,
                        stackId = "top_widgets",
                        onEditStack = { editCalled = true },
                        onToggleDots = {},
                        onRemoveStack = {}
                    ),
                    onDismiss = { dismissed = true }
                )
            }
        }
        advanceAnimation()

        composeTestRule.onNodeWithText("Resize Group").performClick()

        assertTrue(editCalled)
        assertTrue(dismissed)
    }

    @Test
    fun `when clicking Alternar Indicador de Posicao, should call onToggleDots and dismiss`() {
        var toggledStackId: String? = null
        var dismissed = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                WidgetStackBottomSheet(
                    params = WidgetStackBottomSheetParams(
                        widgetId = 123,
                        stackId = "stack_abc",
                        onEditStack = {},
                        onToggleDots = { toggledStackId = it },
                        onRemoveStack = {}
                    ),
                    onDismiss = { dismissed = true }
                )
            }
        }
        advanceAnimation()

        composeTestRule.onNodeWithText("Toggle Position Indicator").performClick()

        assertEquals("stack_abc", toggledStackId)
        assertTrue(dismissed)
    }

    @Test
    fun `when clicking Remover, should call onRemoveStack and dismiss`() {
        var removeCalled = false
        var dismissed = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                WidgetStackBottomSheet(
                    params = WidgetStackBottomSheetParams(
                        widgetId = 123,
                        stackId = "top_widgets",
                        onEditStack = {},
                        onToggleDots = {},
                        onRemoveStack = { removeCalled = true }
                    ),
                    onDismiss = { dismissed = true }
                )
            }
        }
        advanceAnimation()

        composeTestRule.onNodeWithText("Remove").performClick()

        assertTrue(removeCalled)
        assertTrue(dismissed)
    }
}
