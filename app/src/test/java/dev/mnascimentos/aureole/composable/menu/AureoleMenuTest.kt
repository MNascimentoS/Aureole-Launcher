package dev.mnascimentos.aureole.composable.menu

import androidx.compose.runtime.Composable
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
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.menu.model.MENU_HOLD_MS
import dev.mnascimentos.aureole.composable.menu.model.MenuEntry
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.MenuSheetParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.MainUiState
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
        ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
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
    fun `hold row does not fire on a short press and fires after the hold time`() {
        var fired = false
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            Themed {
                AureoleMenuRow(MenuRowParams(title = "Uninstall", onClick = { fired = true }, requiresHold = true))
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
    fun `list sheet draws destructive rows after the others even when given first`() {
        composeTestRule.setContent {
            Themed {
                AureoleMenuListSheet(title = "Menu", onDismiss = {}) {
                    listOf(
                        MenuEntry.Row(MenuRowParams(title = "Remove", onClick = {}, isDestructive = true)),
                        MenuEntry.Row(MenuRowParams(title = "Rename", onClick = {}))
                    )
                }
            }
        }

        val rename = composeTestRule.onNodeWithText("Rename").getUnclippedBoundsInRoot().top
        val remove = composeTestRule.onNodeWithText("Remove").getUnclippedBoundsInRoot().top
        assertTrue(rename < remove)
    }

    @Test
    fun `list sheet draws every entry kind`() {
        composeTestRule.setContent {
            Themed {
                AureoleMenuListSheet(title = "Menu", onDismiss = {}) {
                    listOf(
                        MenuEntry.Section("part"),
                        MenuEntry.Note("a note"),
                        MenuEntry.Content { AureoleMenuRow(MenuRowParams(title = "Custom", onClick = {})) },
                        MenuEntry.Divider
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("PART").assertIsDisplayed()
        composeTestRule.onNodeWithText("a note").assertIsDisplayed()
        composeTestRule.onNodeWithText("Custom").assertIsDisplayed()
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
                        bottomBar = { AureoleMenuRow(MenuRowParams(title = "Info", onClick = {})) }
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
