package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.EDIT_HINT_TEXT_MS
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintConfig
import dev.mnascimentos.aureole.feature.home.grid.model.GridGestureTracker
import dev.mnascimentos.aureole.feature.home.grid.model.LocalGridGestureTracker
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OnboardingEditHintComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var saveCount = 0

    private fun render(isVisible: Boolean, tracker: GridGestureTracker? = null) {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalGridGestureTracker provides tracker) {
                    OnboardingEditHintComponent(config = EditHintConfig(isVisible = isVisible), onSave = { saveCount++ })
                }
            }
        }
    }

    @Test
    fun `when visible, should explain the edit mode and save on tap`() {
        render(isVisible = true)

        composeTestRule.onNodeWithText(HINT).assertExists()
        composeTestRule.onNodeWithText("Save").performClick()

        assertEquals(1, saveCount)
    }

    @Test
    fun `after three seconds, should fold into the save pill and drop the text for good`() {
        render(isVisible = true)

        composeTestRule.mainClock.advanceTimeBy(EDIT_HINT_TEXT_MS + 100)

        composeTestRule.onNodeWithText(HINT).assertDoesNotExist()
        composeTestRule.onNodeWithText("Save").performClick()
        assertEquals(1, saveCount)
    }

    @Test
    fun `on the first drag, should fold at once and ignore taps until the gesture ends`() {
        val tracker = GridGestureTracker()
        render(isVisible = true, tracker = tracker)

        composeTestRule.runOnUiThread { tracker.begin(); Snapshot.sendApplyNotifications() }
        composeTestRule.mainClock.advanceTimeBy(100)
        composeTestRule.onNodeWithText(HINT).assertDoesNotExist()
        composeTestRule.onNodeWithText("Save").performClick()
        assertEquals(0, saveCount)

        composeTestRule.runOnUiThread { tracker.end(); Snapshot.sendApplyNotifications() }
        composeTestRule.mainClock.advanceTimeBy(100)
        composeTestRule.onNodeWithText("Save").performClick()
        assertEquals(1, saveCount)
    }

    @Test
    fun `when hidden, should draw nothing`() {
        render(isVisible = false)

        composeTestRule.onNodeWithText(HINT).assertDoesNotExist()
    }

    private companion object {
        const val HINT = "Drag to move, pull the edge to resize."
    }
}
