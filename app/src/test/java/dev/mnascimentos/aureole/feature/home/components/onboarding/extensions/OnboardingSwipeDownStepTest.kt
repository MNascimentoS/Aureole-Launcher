package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSignal
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.currentGestureStep
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.visibleGestureSteps
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

/** The "Swipe down" step of the gesture coach (RF-B4, RF-B5). */
class OnboardingSwipeDownStepTest {

    private val beforeSwipeDown = setOf(GestureCoachStep.OPEN_APPS, GestureCoachStep.ALPHABET_SCRUB)

    private fun coaching(
        completedSteps: Set<GestureCoachStep> = emptySet(),
        succeededStep: GestureCoachStep? = null,
        isSwipeDownEnabled: Boolean = true
    ) = MainUiState(
        isSwipeDownNotificationsEnabled = isSwipeDownEnabled,
        onboarding = OnboardingUiState(
            stage = OnboardingStage.GESTURES,
            completedSteps = completedSteps,
            succeededStep = succeededStep
        )
    )

    @Test
    fun `the coach order should be all apps, alphabet, swipe down, touch and hold`() {
        assertEquals(
            listOf(
                GestureCoachStep.OPEN_APPS,
                GestureCoachStep.ALPHABET_SCRUB,
                GestureCoachStep.NOTIFICATIONS,
                GestureCoachStep.CUSTOMIZE
            ),
            visibleGestureSteps(isAlphabetScrubberDisabled = false, isLandscape = false, isSwipeDownEnabled = true)
        )
    }

    @Test
    fun `with the toggle off, the swipe down step should not show`() {
        assertEquals(
            listOf(GestureCoachStep.OPEN_APPS, GestureCoachStep.ALPHABET_SCRUB, GestureCoachStep.CUSTOMIZE),
            visibleGestureSteps(isAlphabetScrubberDisabled = false, isLandscape = false, isSwipeDownEnabled = false)
        )
    }

    @Test
    fun `an update in the middle of the coach should go on from where it stopped`() {
        val midCoach = OnboardingUiState(stage = OnboardingStage.GESTURES, completedSteps = beforeSwipeDown)

        assertEquals(GestureCoachStep.NOTIFICATIONS, midCoach.currentGestureStep(false, false, true))
        assertEquals(GestureCoachStep.CUSTOMIZE, midCoach.currentGestureStep(false, false, false))
    }

    @Test
    fun `the swipe down should complete the step and show its success`() {
        val result = OnboardingReducer.applySignal(
            coaching(beforeSwipeDown),
            OnboardingSignal.NOTIFICATIONS_OPENED
        ).onboarding

        assertEquals(beforeSwipeDown + GestureCoachStep.NOTIFICATIONS, result.completedSteps)
        assertEquals(GestureCoachStep.NOTIFICATIONS, result.succeededStep)
    }

    @Test
    fun `once the success was read, the coach should move to touch and hold`() {
        val succeeded = coaching(beforeSwipeDown + GestureCoachStep.NOTIFICATIONS, GestureCoachStep.NOTIFICATIONS)

        val next = succeeded.copy(onboarding = succeeded.onboarding.finishedNotificationsStep())
        val result = OnboardingReducer.reduce(succeeded, next).onboarding

        assertNull(result.succeededStep)
        assertEquals(OnboardingStage.GESTURES, result.stage)
        assertEquals(GestureCoachStep.CUSTOMIZE, result.currentGestureStep(false, false, true))
    }

    @Test
    fun `reading the success should not clear another step's success`() {
        val onboarding = OnboardingUiState(succeededStep = GestureCoachStep.CUSTOMIZE)

        assertSame(onboarding, onboarding.finishedNotificationsStep())
    }

    @Test
    fun `the last step done by swipe down should finish the coach once read`() {
        val all = GestureCoachStep.entries.toSet()
        val succeeded = coaching(all, GestureCoachStep.NOTIFICATIONS)

        val next = succeeded.copy(onboarding = succeeded.onboarding.finishedNotificationsStep())

        assertEquals(OnboardingStage.CHECKLIST, OnboardingReducer.reduce(succeeded, next).onboarding.stage)
    }

    @Test
    fun `turning the toggle off on the swipe down step should skip it`() {
        val previous = coaching(beforeSwipeDown + GestureCoachStep.CUSTOMIZE)

        val result = OnboardingReducer.reduce(previous, previous.copy(isSwipeDownNotificationsEnabled = false))

        assertEquals(OnboardingStage.CHECKLIST, result.onboarding.stage)
    }

    @Test
    fun `a swipe down after the step or outside the coach should change nothing`() {
        val done = coaching(beforeSwipeDown + GestureCoachStep.NOTIFICATIONS)
        val finished = MainUiState(onboarding = OnboardingUiState(stage = OnboardingStage.COMPLETED))
        val checklist = MainUiState(onboarding = OnboardingUiState(stage = OnboardingStage.CHECKLIST))

        listOf(done, finished, checklist).forEach { state ->
            assertSame(state, OnboardingReducer.applySignal(state, OnboardingSignal.NOTIFICATIONS_OPENED))
        }
    }

    @Test
    fun `whoever finished the tour should not go back to the coach (D-04)`() {
        val finished = MainUiState(
            onboarding = OnboardingUiState(stage = OnboardingStage.COMPLETED, completedSteps = beforeSwipeDown)
        )
        val next = finished.copy(isSwipeDownNotificationsEnabled = true)

        assertEquals(OnboardingStage.COMPLETED, OnboardingReducer.reduce(finished, next).onboarding.stage)
    }
}
