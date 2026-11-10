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

private const val HANDLE = "Sheet handle"
private const val LONG_PRESS_MS = 800L
private const val SWIPE_DISTANCE_PX = 600f

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
class OverlaySheetHandleTest {

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
                            title = "Handle Overlay",
                            animated = false,
                            isLeftHanded = isLeftHanded,
                            onSettingsClick = { settingsClicks++ }
                        ),
                        previewContent = { Text("Handle Preview") },
                        bottomBarContent = { Text("Handle Footer") },
                        sheetContent = { Text("Handle Content") }
                    )
                )
            }
        }
    }

    @Test
    fun `when expand action runs, should expand and then offer collapse`() {
        val state = AureoleEditOverlayState()
        setOverlay(state)

        composeTestRule.onNodeWithContentDescription(HANDLE).performCustomAccessibilityActionWithLabel("Expand")
        composeTestRule.waitForIdle()
        assertTrue(state.isExpanded)

        composeTestRule.onNodeWithContentDescription(HANDLE).performCustomAccessibilityActionWithLabel("Collapse")
        composeTestRule.waitForIdle()
        assertFalse(state.isExpanded)
    }

    @Test
    fun `when settings action runs, should call settings`() {
        setOverlay(AureoleEditOverlayState(), isLeftHanded = true)

        composeTestRule.onNodeWithContentDescription(HANDLE)
            .performCustomAccessibilityActionWithLabel("Aureole settings")

        assertEquals(1, settingsClicks)
    }

    @Test
    fun `when handle is double tapped, should toggle expanded`() {
        val state = AureoleEditOverlayState()
        setOverlay(state)

        composeTestRule.onNodeWithContentDescription(HANDLE).performTouchInput { doubleClick() }
        composeTestRule.waitForIdle()

        assertTrue(state.isExpanded)
    }

    @Test
    fun `when handle is held, should open settings`() {
        setOverlay(AureoleEditOverlayState())

        composeTestRule.onNodeWithContentDescription(HANDLE)
            .performTouchInput { longClick(durationMillis = LONG_PRESS_MS) }

        assertEquals(1, settingsClicks)
    }

    @Test
    fun `when sheet is swiped up, should expand`() {
        val state = AureoleEditOverlayState()
        setOverlay(state)

        composeTestRule.onNodeWithText("Handle Overlay")
            .performTouchInput { swipeUp(startY = centerY, endY = centerY - SWIPE_DISTANCE_PX) }
        composeTestRule.waitForIdle()

        assertTrue(state.isExpanded)
    }

    @Test
    fun `when overlay starts expanded, should show content and bottom bar`() {
        setOverlay(AureoleEditOverlayState(initialExpanded = true))

        composeTestRule.onNodeWithText("Handle Content").assertIsDisplayed()
        composeTestRule.onNodeWithText("Handle Footer").assertIsDisplayed()
    }
}
