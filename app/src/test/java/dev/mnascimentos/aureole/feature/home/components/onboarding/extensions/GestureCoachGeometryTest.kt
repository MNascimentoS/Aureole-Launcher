package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.visibleGestureSteps
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** FUE-24 A: the coach teaches the scrub inside the band the alphabet really takes. */
class GestureCoachGeometryTest {

    private val gestures = MainUiState(onboarding = OnboardingUiState(stage = OnboardingStage.GESTURES))
    private val atScrub = gestures.copy(
        onboarding = gestures.onboarding.copy(completedSteps = setOf(GestureCoachStep.OPEN_APPS))
    )

    @Test
    fun `when computing the hint path, should start at 20 percent and end at 75 percent of the band`() {
        val (start, end) = scrubberHintPath(heightPx = 2000f, paddingBottomPx = 300f)

        // band: 2000 × 0.45 = 900 down to 2000 − 300 = 1700
        assertEquals(1060f, start, DELTA)
        assertEquals(1500f, end, DELTA)
    }

    @Test
    fun `when the screen is shorter, should scale the path with it`() {
        val (start, end) = scrubberHintPath(heightPx = 1000f, paddingBottomPx = 112f)

        assertEquals(537.6f, start, DELTA)
        assertEquals(778.5f, end, DELTA)
        assertTrue(start > 1000f * 0.45f && end < 1000f - 112f)
    }

    @Test
    fun `when the padding eats the band, should not invert the path`() {
        val (start, end) = scrubberHintPath(heightPx = 100f, paddingBottomPx = 90f)

        assertTrue(start <= end)
    }

    @Test
    fun `when in landscape or with the scrubber off, should leave the scrub step out`() {
        val all = GestureCoachStep.entries.toList()
        val withoutScrub = all - GestureCoachStep.ALPHABET_SCRUB

        assertEquals(all, visibleGestureSteps(isAlphabetScrubberDisabled = false, isLandscape = false))
        assertEquals(withoutScrub, visibleGestureSteps(isAlphabetScrubberDisabled = false, isLandscape = true))
        assertEquals(withoutScrub, visibleGestureSteps(isAlphabetScrubberDisabled = true, isLandscape = false))
    }

    @Test
    fun `when the coach is on the scrub step, should show the ghost alphabet`() {
        assertTrue(atScrub.isScrubberHintVisible)
    }

    @Test
    fun `when on another step, after a success, in landscape or outside gestures, should hide the ghost`() {
        val succeeded = atScrub.copy(
            onboarding = atScrub.onboarding.copy(succeededStep = GestureCoachStep.OPEN_APPS)
        )

        assertFalse(gestures.isScrubberHintVisible)
        assertFalse(succeeded.isScrubberHintVisible)
        assertFalse(atScrub.copy(isLandscape = true).isScrubberHintVisible)
        assertFalse(
            atScrub.copy(onboarding = atScrub.onboarding.copy(stage = OnboardingStage.CHECKLIST)).isScrubberHintVisible
        )
    }

    @Test
    fun `when the screen turns during the scrub step, should move on`() {
        val almost = atScrub.copy(
            onboarding = atScrub.onboarding.copy(
                completedSteps = setOf(GestureCoachStep.OPEN_APPS, GestureCoachStep.CUSTOMIZE)
            )
        )

        val result = OnboardingReducer.reduce(almost, almost.copy(isLandscape = true))

        assertEquals(OnboardingStage.CHECKLIST, result.onboarding.stage)
    }

    private companion object {
        const val DELTA = 0.5f
    }
}
