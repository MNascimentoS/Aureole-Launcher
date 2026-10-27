package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeUp
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.GestureCoachConfig
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GestureCoachComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val calls = mutableListOf<String>()
    private val actions = OnboardingActions(
        onSkipGestures = { calls += "skip" },
        onShowMeGesture = { calls += "showMe" }
    )

    private fun render(
        onboarding: OnboardingUiState = OnboardingUiState(),
        config: GestureCoachConfig = GestureCoachConfig()
    ) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                GestureCoachComponent(
                    onboarding = onboarding,
                    actions = actions,
                    glass = OnboardingGlass(),
                    config = config
                )
            }
        }
    }

    @Test
    fun `when the coach covers the home, a swipe up should still reach the home`() {
        var dragged = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(HOME_TAG)
                        .pointerInput(Unit) { detectDragGestures { _, _ -> dragged = true } }
                ) {
                    GestureCoachComponent(
                        onboarding = OnboardingUiState(),
                        actions = actions,
                        glass = OnboardingGlass(),
                        config = GestureCoachConfig()
                    )
                }
            }
        }

        composeTestRule.onNodeWithTag(HOME_TAG).performTouchInput { swipeUp() }

        assertTrue(dragged)
    }

    @Test
    fun `when nothing is done, should teach opening the apps first`() {
        render()

        composeTestRule.onNodeWithText("Swipe up").assertExists()
    }

    @Test
    fun `when skip is tapped, should skip the gestures`() {
        render()

        composeTestRule.onNodeWithText("Skip").performClick()

        assertEquals(listOf("skip"), calls)
    }

    @Test
    fun `when the user waits without progress, should offer show me`() {
        render()
        composeTestRule.onNodeWithText("Show me").assertDoesNotExist()

        composeTestRule.mainClock.advanceTimeBy(OnboardingMotionSpec.SHOW_ME_DELAY_MS)
        composeTestRule.onNodeWithText("Show me").performClick()

        assertEquals(listOf("showMe"), calls)
    }

    @Test
    fun `when talkback is on, should offer show me right away`() {
        render(config = GestureCoachConfig(isTouchExplorationEnabled = true))

        composeTestRule.onNodeWithText("Show me").assertExists()
    }

    @Test
    fun `when left handed, should point the alphabet to the left edge`() {
        render(
            onboarding = OnboardingUiState(completedSteps = setOf(GestureCoachStep.OPEN_APPS)),
            config = GestureCoachConfig(isLeftHandedMode = true)
        )

        composeTestRule.onNodeWithText("Swipe along the left edge").assertExists()
    }

    @Test
    fun `when the alphabet is disabled, should go from apps to customize`() {
        render(
            onboarding = OnboardingUiState(completedSteps = setOf(GestureCoachStep.OPEN_APPS)),
            config = GestureCoachConfig(isAlphabetScrubberDisabled = true)
        )

        composeTestRule.onNodeWithText("Touch and hold").assertExists()
    }

    @Test
    fun `when a gesture succeeded, should cheer and hide the buttons`() {
        render(
            onboarding = OnboardingUiState(
                completedSteps = setOf(GestureCoachStep.OPEN_APPS),
                succeededStep = GestureCoachStep.OPEN_APPS
            ),
            config = GestureCoachConfig(isTouchExplorationEnabled = true)
        )

        composeTestRule.onNodeWithText("Nice!").assertExists()
        composeTestRule.onNodeWithText("Your apps live here. Tap outside to close.").assertExists()
        composeTestRule.onNodeWithText("Skip").assertDoesNotExist()
        composeTestRule.onNodeWithText("Show me").assertDoesNotExist()
    }

    @Test
    fun `when every step is done, should show nothing`() {
        render(onboarding = OnboardingUiState(completedSteps = GestureCoachStep.entries.toSet()))

        composeTestRule.onNodeWithText("Skip").assertDoesNotExist()
    }

    private companion object {
        const val HOME_TAG = "home"
    }
}
