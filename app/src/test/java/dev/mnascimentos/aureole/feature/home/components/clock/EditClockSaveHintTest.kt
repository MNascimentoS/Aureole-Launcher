package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The clock editor explains, only in the onboarding, that it closes by dragging and saves as it goes (RF-D2). */
@RunWith(RobolectricTestRunner::class)
class EditClockSaveHintTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var dismissCount = 0

    private fun setSheet(uiState: MainUiState) {
        val actions = createDefaultHomeScreenActions().copy(onDismissClockSaveHint = { dismissCount++ })
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockBottomSheet(uiState = uiState, actions = actions, onDismissRequest = {})
            }
        }
        composeTestRule.mainClock.advanceTimeBy(500L)
        composeTestRule.waitForIdle()
    }

    @Test
    fun `when the onboarding runs, should show the notice and dismiss it on tap`() {
        setSheet(MainUiState(onboarding = OnboardingUiState(stage = OnboardingStage.CHECKLIST)))

        composeTestRule.onNodeWithText(HINT).assertIsDisplayed()
        composeTestRule.onNodeWithText(HINT).performClick()

        assertEquals(1, dismissCount)
    }

    @Test
    fun `when the onboarding is over, should not show the notice`() {
        setSheet(MainUiState())

        composeTestRule.onNodeWithText(HINT).assertDoesNotExist()
    }

    @Test
    fun `when the notice was dismissed, should not show it in the onboarding`() {
        setSheet(
            MainUiState(
                onboarding = OnboardingUiState(stage = OnboardingStage.CHECKLIST),
                isClockSaveHintDismissed = true
            )
        )

        composeTestRule.onNodeWithText(HINT).assertDoesNotExist()
    }

    private companion object {
        const val HINT = "Your changes are saved automatically when you close"
    }
}
