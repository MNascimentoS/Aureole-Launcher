package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DefaultHomeStepComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val calls = mutableListOf<String>()
    private val actions = OnboardingActions(
        onRequestDefaultHome = { calls += "request" },
        onOpenSystemHomeSettings = { calls += "settings" },
        onPostponeDefaultHome = { calls += "later" }
    )

    private fun render(onboarding: OnboardingUiState) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                DefaultHomeStepComponent(onboarding = onboarding, actions = actions, glass = OnboardingGlass())
            }
        }
    }

    @Test
    fun `when the buttons are tapped, should request the role or postpone`() {
        render(OnboardingUiState())

        composeTestRule.onNodeWithText("Set as home app").performClick()
        composeTestRule.onNodeWithText("Not now").performClick()

        assertEquals(listOf("request", "later"), calls)
    }

    @Test
    fun `when nothing was attempted yet, should hide the system settings link`() {
        render(OnboardingUiState())

        composeTestRule.onNodeWithText("Open system settings").assertDoesNotExist()
    }

    @Test
    fun `when an attempt did not stick, should offer the system settings`() {
        render(OnboardingUiState(defaultHomeAttempted = true))

        composeTestRule.onNodeWithText("Open system settings").performClick()

        assertEquals(listOf("settings"), calls)
    }

    @Test
    fun `when aureole became the home app, should celebrate and move on`() {
        render(OnboardingUiState(defaultHomeAttempted = true, isDefaultLauncher = true))

        composeTestRule.onNodeWithText("Done! Aureole is now your home screen.").assertExists()
        composeTestRule.onNodeWithText("Set as home app").assertDoesNotExist()
        composeTestRule.mainClock.advanceTimeBy(SUCCESS_WAIT_MS)
        composeTestRule.waitForIdle()

        assertEquals(listOf("later"), calls)
    }

    private companion object {
        const val SUCCESS_WAIT_MS = 2_000L
    }
}
