package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.visibleGestureSteps
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Test

/** The steps of the gesture coach: all apps, alphabet, touch and hold (the notifications step is gone). */
class OnboardingGestureStepsTest {

    @Test
    fun `the coach order should be all apps, alphabet, touch and hold`() {
        assertEquals(
            listOf(GestureCoachStep.OPEN_APPS, GestureCoachStep.ALPHABET_SCRUB, GestureCoachStep.CUSTOMIZE),
            visibleGestureSteps(isAlphabetScrubberDisabled = false, isLandscape = false)
        )
    }

    @Test
    fun `whoever finished the tour should not go back to the coach (D-04)`() {
        val finished = MainUiState(
            onboarding = OnboardingUiState(
                stage = OnboardingStage.COMPLETED,
                completedSteps = setOf(GestureCoachStep.OPEN_APPS, GestureCoachStep.ALPHABET_SCRUB)
            )
        )
        val next = finished.copy(isSwipeDownNotificationsEnabled = true)

        assertEquals(OnboardingStage.COMPLETED, OnboardingReducer.reduce(finished, next).onboarding.stage)
    }
}
