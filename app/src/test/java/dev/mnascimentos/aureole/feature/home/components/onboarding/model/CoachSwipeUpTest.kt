package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CoachSwipeUpTest {

    @Test
    fun `when the tour reaches the gestures with swipe up off, should switch it on`() {
        assertTrue(coachNeedsSwipeUp(OnboardingStage.WELCOME, OnboardingStage.GESTURES, isSwipeUpEnabled = false))
        assertTrue(coachNeedsSwipeUp(OnboardingStage.DEFAULT_HOME, OnboardingStage.GESTURES, isSwipeUpEnabled = false))
    }

    @Test
    fun `when swipe up is already on, should leave it alone`() {
        assertFalse(coachNeedsSwipeUp(OnboardingStage.WELCOME, OnboardingStage.GESTURES, isSwipeUpEnabled = true))
    }

    @Test
    fun `when the stage is not newly the gestures, should not touch the setting`() {
        assertFalse(coachNeedsSwipeUp(OnboardingStage.GESTURES, OnboardingStage.GESTURES, isSwipeUpEnabled = false))
        assertFalse(coachNeedsSwipeUp(OnboardingStage.WELCOME, OnboardingStage.CHECKLIST, isSwipeUpEnabled = false))
        assertFalse(coachNeedsSwipeUp(OnboardingStage.COMPLETED, OnboardingStage.COMPLETED, isSwipeUpEnabled = false))
    }
}
