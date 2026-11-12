package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import dev.mnascimentos.aureole.composable.menu.model.MENU_HOLD_MS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val HOLD_MARGIN_MS = 100L

@RunWith(RobolectricTestRunner::class)
class FactoryResetBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when row is tapped quickly, should not confirm factory reset`() {
        var confirmed = false
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FactoryResetBottomSheet(SettingsScreenActions(onConfirmFactoryReset = { confirmed = true }))
            }
        }

        composeTestRule.onNodeWithText("Reset launcher?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Reset").performClick()
        composeTestRule.mainClock.advanceTimeBy(MENU_HOLD_MS + HOLD_MARGIN_MS)
        assertFalse(confirmed)
    }

    @Test
    fun `when row is held, should confirm factory reset`() {
        var confirmed = false
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FactoryResetBottomSheet(SettingsScreenActions(onConfirmFactoryReset = { confirmed = true }))
            }
        }

        composeTestRule.onNodeWithText("Reset").performTouchInput { down(center) }
        composeTestRule.mainClock.advanceTimeBy(MENU_HOLD_MS + HOLD_MARGIN_MS)
        assertTrue(confirmed)
    }
}
