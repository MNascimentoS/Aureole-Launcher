package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingProgress
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingUiStateTest {

    @Test
    fun `when created with defaults, should be completed and not block the home`() {
        val state = OnboardingUiState()

        assertEquals(OnboardingStage.COMPLETED, state.stage)
        assertFalse(state.isBlocking)
        assertFalse(state.blocksHome)
    }

    @Test
    fun `when in welcome or default home, should block`() {
        assertTrue(OnboardingUiState(stage = OnboardingStage.WELCOME).isBlocking)
        assertTrue(OnboardingUiState(stage = OnboardingStage.DEFAULT_HOME).isBlocking)
    }

    @Test
    fun `when teaching gestures, should let touches reach the home`() {
        val state = OnboardingUiState(stage = OnboardingStage.GESTURES)

        assertFalse(state.isBlocking)
        assertFalse(state.blocksHome)
    }

    @Test
    fun `when the checklist is expanded, should block the home`() {
        assertTrue(OnboardingUiState(stage = OnboardingStage.CHECKLIST, isChecklistExpanded = true).blocksHome)
    }

    @Test
    fun `when the checklist is collapsed, should not block the home`() {
        assertFalse(OnboardingUiState(stage = OnboardingStage.CHECKLIST, isChecklistExpanded = false).blocksHome)
    }

    @Test
    fun `when an appearance picker is open, should block the home`() {
        val state = OnboardingUiState(
            stage = OnboardingStage.CHECKLIST,
            isChecklistExpanded = false,
            activePicker = AppearancePicker.PALETTE
        )

        assertTrue(state.blocksHome)
    }

    @Test
    fun `when tasks are done, should report the fraction of all tasks`() {
        val state = OnboardingUiState(completedTasks = setOf(OnboardingTask.CHOOSE_FONT))

        assertEquals(1f / OnboardingTask.entries.size, state.taskProgress, 0.0001f)
        assertEquals(1f, OnboardingUiState(completedTasks = OnboardingTask.entries.toSet()).taskProgress, 0.0001f)
    }

    @Test
    fun `when the alphabet scrubber is enabled, should show every gesture step`() {
        assertEquals(GestureCoachStep.entries, visibleGestureSteps(isAlphabetScrubberDisabled = false))
    }

    @Test
    fun `when the alphabet scrubber is disabled, should skip the scrub step`() {
        assertEquals(
            listOf(GestureCoachStep.OPEN_APPS, GestureCoachStep.CUSTOMIZE),
            visibleGestureSteps(isAlphabetScrubberDisabled = true)
        )
    }

    @Test
    fun `when mapped to progress and back, should keep the persisted fields`() {
        val state = OnboardingUiState(
            stage = OnboardingStage.CHECKLIST,
            completedSteps = setOf(GestureCoachStep.OPEN_APPS),
            completedTasks = setOf(OnboardingTask.ADD_WIDGET),
            isDefaultLauncher = true,
            isChecklistExpanded = false
        )

        val restored = state.toProgress().toUiState(isDefaultLauncher = true)

        assertEquals(state, restored)
    }

    @Test
    fun `when mapped from progress, should expand the checklist unless it was collapsed`() {
        val progress = OnboardingProgress(stage = OnboardingStage.CHECKLIST, isChecklistCollapsed = false)

        val state = progress.toUiState(isDefaultLauncher = false)

        assertTrue(state.isChecklistExpanded)
        assertFalse(state.isDefaultLauncher)
    }
}
