package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.doubleClick
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val GESTURE_ZONE = "Hold the top of the sheet"
private const val LONG_PRESS_MS = 800L
private const val SWIPE_DISTANCE_PX = 600f

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
class OverlaySheetGestureZoneTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var settingsClicks = 0

    private fun setOverlay(state: AureoleEditOverlayState, isLeftHanded: Boolean = false) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleEditOverlay(
                    state = state,
                    params = AureoleEditOverlayParams(
                        config = AureoleEditOverlayConfig(
                            title = "Zone Overlay",
                            animated = false,
                            isLeftHanded = isLeftHanded,
                            onSettingsClick = { settingsClicks++ }
                        ),
                        previewContent = { Text("Zone Preview") },
                        bottomBarContent = { Text("Zone Footer") },
                        sheetContent = { Text("Zone Content") }
                    )
                )
            }
        }
    }

    @Test
    fun `when expand action runs, should expand and then offer collapse`() {
        val state = AureoleEditOverlayState()
        setOverlay(state)

        composeTestRule.onNodeWithContentDescription(GESTURE_ZONE).performCustomAccessibilityActionWithLabel("Expand")
        composeTestRule.waitForIdle()
        assertTrue(state.isExpanded)

        composeTestRule.onNodeWithContentDescription(GESTURE_ZONE).performCustomAccessibilityActionWithLabel("Collapse")
        composeTestRule.waitForIdle()
        assertFalse(state.isExpanded)
    }

    @Test
    fun `when settings action runs, should call settings`() {
        setOverlay(AureoleEditOverlayState(), isLeftHanded = true)

        composeTestRule.onNodeWithContentDescription(GESTURE_ZONE)
            .performCustomAccessibilityActionWithLabel("Aureole settings")

        assertEquals(1, settingsClicks)
    }

    @Test
    fun `when gesture zone is double tapped, should toggle expanded`() {
        val state = AureoleEditOverlayState()
        setOverlay(state)

        composeTestRule.onNodeWithContentDescription(GESTURE_ZONE).performTouchInput { doubleClick() }
        composeTestRule.waitForIdle()

        assertTrue(state.isExpanded)
    }

    @Test
    fun `when gesture zone is held, should open settings`() {
        setOverlay(AureoleEditOverlayState())

        composeTestRule.onNodeWithContentDescription(GESTURE_ZONE)
            .performTouchInput { longClick(durationMillis = LONG_PRESS_MS) }

        assertEquals(1, settingsClicks)
    }

    @Test
    fun `when sheet is swiped up, should expand`() {
        val state = AureoleEditOverlayState()
        setOverlay(state)

        composeTestRule.onNodeWithText("Zone Overlay")
            .performTouchInput { swipeUp(startY = centerY, endY = centerY - SWIPE_DISTANCE_PX) }
        composeTestRule.waitForIdle()

        assertTrue(state.isExpanded)
    }

    @Test
    fun `when gesture zone is swiped up, should expand`() {
        val state = AureoleEditOverlayState()
        setOverlay(state)

        composeTestRule.onNodeWithContentDescription(GESTURE_ZONE)
            .performTouchInput { swipeUp(startY = centerY, endY = centerY - SWIPE_DISTANCE_PX) }
        composeTestRule.waitForIdle()

        assertTrue(state.isExpanded)
    }

    @Test
    fun `when overlay starts expanded, should show content and bottom bar`() {
        setOverlay(AureoleEditOverlayState(initialExpanded = true))

        composeTestRule.onNodeWithText("Zone Content").assertIsDisplayed()
        composeTestRule.onNodeWithText("Zone Footer").assertIsDisplayed()
    }
}
