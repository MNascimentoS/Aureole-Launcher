package dev.mnascimentos.aureole.composable.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.down
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.up
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.menu.model.MENU_HOLD_MS
import dev.mnascimentos.aureole.composable.menu.model.MenuQuickAction
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.MenuSheetParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AureoleMenuTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    private fun Themed(isLeftHanded: Boolean = false, content: @Composable () -> Unit) {
        CompositionLocalProvider(LocalHomeUiState provides MainUiState(isLeftHandedMode = isLeftHanded)) {
            AureoleLauncherTheme { content() }
        }
    }

    @Test
    fun `menu row is 56 dp tall and calls onClick`() {
        var clicked = false
        composeTestRule.setContent {
            Themed { AureoleMenuRow(MenuRowParams(title = "Rename", onClick = { clicked = true })) }
        }

        composeTestRule.onNodeWithText("Rename").assertIsDisplayed().performClick()

        assertTrue(clicked)
        val bounds = composeTestRule.onNodeWithText("Rename").getUnclippedBoundsInRoot()
        assertTrue(bounds.bottom - bounds.top > 0.dp)
    }

    @Test
    fun `row with a chevron and a destructive row render their titles`() {
        composeTestRule.setContent {
            Themed(isLeftHanded = true) {
                AureoleMenuRow(MenuRowParams(title = "Go", onClick = {}, showChevron = true))
                AureoleMenuSectionLabel(text = "section")
                AureoleMenuNote(text = "note")
            }
        }

        composeTestRule.onNodeWithText("Go").assertIsDisplayed()
        composeTestRule.onNodeWithText("SECTION").assertIsDisplayed()
        composeTestRule.onNodeWithText("note").assertIsDisplayed()
    }

    @Test
    fun `quick action with a tap fires once`() {
        var count = 0
        composeTestRule.setContent {
            Themed {
                AureoleMenuQuickActions(listOf(MenuQuickAction("Info", { _, _ -> }, { count++ })))
            }
        }

        composeTestRule.onNodeWithText("Info").performClick()

        assertEquals(1, count)
    }

    @Test
    fun `quick actions put the first one at the thumb side`() {
        val actions = listOf(
            MenuQuickAction("First", { _, _ -> }, {}),
            MenuQuickAction("Last", { _, _ -> }, {})
        )
        composeTestRule.setContent { Themed { AureoleMenuQuickActions(actions) } }

        val first = composeTestRule.onNodeWithText("First").getUnclippedBoundsInRoot().left
        val last = composeTestRule.onNodeWithText("Last").getUnclippedBoundsInRoot().left
        assertTrue(first > last)
    }

    @Test
    fun `quick actions put the first one at the left for left-handed users`() {
        val actions = listOf(
            MenuQuickAction("First", { _, _ -> }, {}),
            MenuQuickAction("Last", { _, _ -> }, {})
        )
        composeTestRule.setContent { Themed(isLeftHanded = true) { AureoleMenuQuickActions(actions) } }

        val first = composeTestRule.onNodeWithText("First").getUnclippedBoundsInRoot().left
        val last = composeTestRule.onNodeWithText("Last").getUnclippedBoundsInRoot().left
        assertTrue(first < last)
    }

    @Test
    fun `hold action does not fire on a short press and fires after the hold time`() {
        var fired = false
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            Themed {
                AureoleMenuQuickActions(
                    listOf(MenuQuickAction("Uninstall", { _, _ -> }, { fired = true }, requiresHold = true))
                )
            }
        }

        composeTestRule.onNodeWithText("Uninstall").assertIsDisplayed()
        composeTestRule.onNodeWithText("Uninstall").performTouchInput { down(center) }
        composeTestRule.mainClock.advanceTimeBy(MENU_HOLD_MS / 2)
        composeTestRule.onNodeWithText("Uninstall").performTouchInput { up() }
        composeTestRule.mainClock.advanceTimeBy(MENU_HOLD_MS)
        assertFalse(fired)

        composeTestRule.onNodeWithText("Uninstall").performTouchInput { down(center) }
        composeTestRule.mainClock.advanceTimeBy(MENU_HOLD_MS + 100L)
        assertTrue(fired)
    }

    @Test
    fun `menu sheet shows title, rows and bottom bar and the settings button calls back`() {
        var settingsClicked = false
        composeTestRule.setContent {
            Themed {
                AureoleMenuSheet(
                    title = "Browser",
                    onDismiss = {},
                    params = MenuSheetParams(
                        onSettingsClick = { settingsClicked = true },
                        bottomBar = { AureoleMenuQuickActions(listOf(MenuQuickAction("Info", { _, _ -> }, {}))) }
                    )
                ) {
                    AureoleMenuRow(MenuRowParams(title = "Row", onClick = {}))
                }
            }
        }

        composeTestRule.onNodeWithText("Browser").assertIsDisplayed()
        composeTestRule.onNodeWithText("Row").assertIsDisplayed()
        composeTestRule.onNodeWithText("Info").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Aureole settings").performClick()

        assertTrue(settingsClicked)
    }
}
