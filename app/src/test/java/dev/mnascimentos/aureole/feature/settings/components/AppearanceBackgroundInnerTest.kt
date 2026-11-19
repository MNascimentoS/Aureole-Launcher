package dev.mnascimentos.aureole.feature.settings.components

import androidx.activity.compose.BackHandler
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.click
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.SettingsNavGraph
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.testing.closeOverlayWithBack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Long enough for a sheet to slide out and back in. */
private const val SHEET_SLIDE_MS = 1_000L
private const val SAVED_SEED = 0xFF112233.toInt()

/** "Set background" opens inside the Appearance sheet (RF-01 to RF-03 of polimento-ui). */
@RunWith(RobolectricTestRunner::class)
class AppearanceBackgroundInnerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var leftSettings = false

    private fun openBackground(
        uiState: SettingsUiState = SettingsUiState(manualSeedColor = SAVED_SEED),
        actions: SettingsScreenActions = SettingsScreenActions()
    ) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                BackHandler { leftSettings = true }
                SettingsNavGraph(uiState = uiState, actions = actions)
            }
        }
        composeTestRule.onNodeWithText("Appearance").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Set Background").performScrollTo().performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun `opens inside the Appearance sheet with its title, the preview and no Vivid section`() {
        openBackground()

        composeTestRule.onNodeWithText("Set background").assertExists()
        composeTestRule.onNodeWithText("TYPE").assertExists()
        composeTestRule.onNodeWithText("FROM YOUR THEME").assertExists()
        composeTestRule.onNodeWithText("VIVID").assertDoesNotExist()
        composeTestRule.onNodeWithText("09:41").assertExists()
        composeTestRule.onNodeWithText("Themed icons").assertDoesNotExist()
    }

    @Test
    fun `back without a change brings the Appearance list back and saves nothing`() {
        val saved = mutableListOf<Int>()
        openBackground(actions = SettingsScreenActions(onSelectBackgroundColor = { saved += it }))

        composeTestRule.closeOverlayWithBack()

        assertTrue(saved.isEmpty())
        composeTestRule.onNodeWithText("Themed icons").assertExists()
        composeTestRule.onNodeWithText("TYPE").assertDoesNotExist()
        assertFalse(leftSettings)
    }

    @Test
    fun `back after picking a swatch saves the color once and keeps the sheet open`() {
        val saved = mutableListOf<Int>()
        openBackground(actions = SettingsScreenActions(onSelectBackgroundColor = { saved += it }))

        composeTestRule.onAllNodes(isSelectable()).onLast().performSemanticsAction(SemanticsActions.OnClick)
        assertTrue(saved.isEmpty())
        composeTestRule.closeOverlayWithBack()

        assertEquals(1, saved.size)
        assertNotEquals(SAVED_SEED, saved.single())
        composeTestRule.onNodeWithText("Themed icons").assertExists()

        // Closing the sheet afterwards has nothing left to save.
        composeTestRule.closeOverlayWithBack()
        assertEquals(1, saved.size)
    }

    @Test
    fun `tapping outside brings the Appearance list back instead of closing the sheet`() {
        openBackground()

        // The long picker fills most of the small test window: tap above the sheet, at the top edge.
        composeTestRule.onRoot().performTouchInput { click(Offset(centerX, 1f)) }
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.advanceTimeBy(SHEET_SLIDE_MS)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Themed icons").assertExists()
        composeTestRule.onNodeWithText("TYPE").assertDoesNotExist()
    }

    @Test
    fun `the custom image card opens the photo picker`() {
        var picked = false
        openBackground(actions = SettingsScreenActions(onChangeWallpaperClick = { picked = true }))

        composeTestRule.onNodeWithText("Custom image").performClick()

        assertTrue(picked)
    }

    @Test
    fun `turning the lock screen on the first time asks the warning beside the sheet`() {
        var turnedOn = false
        openBackground(actions = SettingsScreenActions(onLockScreenTurnOn = { turnedOn = true }))

        composeTestRule.onNodeWithText("Also use on lock screen").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Use on lock screen?").assertExists()
        assertFalse(turnedOn)
    }
}
