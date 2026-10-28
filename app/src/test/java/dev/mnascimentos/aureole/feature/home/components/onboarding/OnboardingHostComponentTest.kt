package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingHostParams
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OnboardingHostComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val calls = mutableListOf<String>()
    private val actions = OnboardingActions(onSetChecklistExpanded = { calls += "expanded=$it" })

    private fun render(onboarding: OnboardingUiState) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                OnboardingHostComponent(params = OnboardingHostParams(onboarding = onboarding), actions = actions)
            }
        }
    }

    @Test
    fun `when setting the home app, should show that step`() {
        render(OnboardingUiState(stage = OnboardingStage.DEFAULT_HOME))

        composeTestRule.onNodeWithText("Make Aureole your home").assertExists()
    }

    @Test
    fun `when on the checklist, should show the first steps`() {
        render(OnboardingUiState(stage = OnboardingStage.CHECKLIST))

        composeTestRule.onNodeWithText("First steps").assertExists()
    }

    @Test
    fun `when the checklist is minimized, the pill should reopen it`() {
        render(OnboardingUiState(stage = OnboardingStage.CHECKLIST, isChecklistExpanded = false))

        composeTestRule.onNodeWithText("First steps").assertDoesNotExist()
        composeTestRule.onNodeWithText("0/8 first steps").performClick()

        assertEquals(listOf("expanded=true"), calls)
    }

    @Test
    fun `when a picker is open, should show it instead of the list`() {
        render(OnboardingUiState(stage = OnboardingStage.CHECKLIST, activePicker = AppearancePicker.PALETTE))

        composeTestRule.onNodeWithText("Looks good").assertExists()
        composeTestRule.onNodeWithText("First steps").assertDoesNotExist()
    }

    @Test
    fun `when the last task is done, should celebrate`() {
        render(OnboardingUiState(stage = OnboardingStage.COMPLETED, isCelebrating = true))

        composeTestRule.onNodeWithText("All set").assertExists()
    }
}
