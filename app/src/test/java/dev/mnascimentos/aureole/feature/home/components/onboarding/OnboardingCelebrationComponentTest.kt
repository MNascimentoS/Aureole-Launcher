package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OnboardingCelebrationComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `should celebrate and finish by itself after the celebration time`() {
        var finished = 0
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                OnboardingCelebrationComponent(onFinished = { finished++ })
            }
        }

        composeTestRule.onNodeWithText("All set").assertExists()
        assertEquals(0, finished)

        composeTestRule.mainClock.advanceTimeBy(OnboardingMotionSpec.CELEBRATION_MS + FRAME_MARGIN_MS)

        assertEquals(1, finished)
    }

    private companion object {
        const val FRAME_MARGIN_MS = 100L
    }
}
