package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class OnboardingChecklistPillComponentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var expandCount = 0
    private val minimized = OnboardingUiState(
        stage = OnboardingStage.CHECKLIST,
        isChecklistExpanded = false,
        completedTasks = setOf(
            OnboardingTask.SET_DEFAULT_HOME,
            OnboardingTask.CHOOSE_PALETTE,
            OnboardingTask.CHOOSE_FONT
        )
    )

    private fun render(onboarding: OnboardingUiState, isOtherOverlayActive: Boolean = false) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                OnboardingChecklistPillComponent(
                    onboarding = onboarding,
                    isOtherOverlayActive = isOtherOverlayActive,
                    glass = OnboardingGlass(),
                    onExpand = { expandCount++ }
                )
            }
        }
    }

    @Test
    fun `when minimized, should show the progress and reopen on tap`() {
        render(minimized)

        composeTestRule.onNodeWithText(PILL_TEXT).performClick()

        assertEquals(1, expandCount)
    }

    @Test
    fun `when another home overlay is open, should hide`() {
        render(minimized, isOtherOverlayActive = true)

        composeTestRule.onNodeWithText(PILL_TEXT).assertDoesNotExist()
    }

    @Test
    fun `when the list is expanded, should hide`() {
        render(minimized.copy(isChecklistExpanded = true))

        composeTestRule.onNodeWithText(PILL_TEXT).assertDoesNotExist()
    }

    @Test
    fun `when an appearance picker is open, should hide`() {
        render(minimized.copy(activePicker = AppearancePicker.FONT))

        composeTestRule.onNodeWithText(PILL_TEXT).assertDoesNotExist()
    }

    @Test
    fun `when the checklist stage is over, should hide`() {
        render(minimized.copy(stage = OnboardingStage.COMPLETED))

        composeTestRule.onNodeWithText(PILL_TEXT).assertDoesNotExist()
    }

    private companion object {
        const val PILL_TEXT = "3/7 first steps"
    }
}
