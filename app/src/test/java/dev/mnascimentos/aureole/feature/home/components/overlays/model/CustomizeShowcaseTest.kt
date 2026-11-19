package dev.mnascimentos.aureole.feature.home.components.overlays.model

import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.OnboardingReducer
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/** The Customize showcase of the coach's customize step (RF-01 to RF-04), in plain JVM. */
class CustomizeShowcaseTest {

    /** The coach teaching the last step: opening Customize finishes the gestures. */
    private val coaching = MainUiState(
        onboarding = OnboardingUiState(
            stage = OnboardingStage.GESTURES,
            completedSteps = setOf(GestureCoachStep.OPEN_APPS, GestureCoachStep.ALPHABET_SCRUB)
        )
    )

    private fun open(): MainUiState = OnboardingReducer.reduce(coaching, coaching.copy(showCustomizeBottomSheet = true))

    @Test
    fun `when the coach opens Customize, should mark the step done and open it as a showcase`() {
        val opened = open()

        assertTrue(GestureCoachStep.CUSTOMIZE in opened.onboarding.completedSteps)
        assertEquals(GestureCoachStep.CUSTOMIZE, opened.onboarding.succeededStep)
        assertTrue(opened.isCustomizeShowcase)
    }

    @Test
    fun `when the showcase time runs out and the sheet closes, should clear the success and move to the checklist`() {
        val opened = open()

        val closed = OnboardingReducer.reduce(opened, opened.copy(showCustomizeBottomSheet = false))

        assertNull(closed.onboarding.succeededStep)
        assertEquals(OnboardingStage.CHECKLIST, closed.onboarding.stage)
        assertTrue(closed.onboarding.isChecklistExpanded)
        assertFalse(closed.isCustomizeShowcase)
    }

    @Test
    fun `when back is pressed during the showcase, should change nothing`() {
        // The locked sheet swallows back: the sheet stays open, so the state the reducer sees does not move.
        val opened = open()

        val afterBack = OnboardingReducer.reduce(opened, opened)

        assertSame(opened, afterBack)
        assertTrue(afterBack.isCustomizeShowcase)
    }

    @Test
    fun `when Customize opens outside the tour or after its step, should not be a showcase`() {
        val outside = MainUiState(showCustomizeBottomSheet = true)
        val stepAlreadyDone = coaching.copy(
            showCustomizeBottomSheet = true,
            onboarding = coaching.onboarding.copy(completedSteps = GestureCoachStep.entries.toSet())
        )
        val checklist = MainUiState(
            showCustomizeBottomSheet = true,
            onboarding = OnboardingUiState(
                stage = OnboardingStage.CHECKLIST,
                succeededStep = GestureCoachStep.CUSTOMIZE
            )
        )

        assertFalse(outside.isCustomizeShowcase)
        assertFalse(OnboardingReducer.reduce(stepAlreadyDone, stepAlreadyDone).isCustomizeShowcase)
        assertFalse(checklist.isCustomizeShowcase)
    }

    @Test
    fun `when time passes, should fill the ring linearly in 3 s and stop at full`() {
        assertEquals(3_000L, CUSTOMIZE_SHOWCASE_MS)
        assertEquals(0f, customizeShowcaseProgress(0L), DELTA)
        assertEquals(0.5f, customizeShowcaseProgress(1_500L), DELTA)
        assertEquals(1f, customizeShowcaseProgress(3_000L), DELTA)
        assertEquals(1f, customizeShowcaseProgress(10_000L), DELTA)
        assertEquals(0f, customizeShowcaseProgress(-5L), DELTA)
        assertEquals(1f, customizeShowcaseProgress(10L, durationMs = 0L), DELTA)
    }

    private companion object {
        const val DELTA = 0.0001f
    }
}
