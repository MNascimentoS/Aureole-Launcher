package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSignal
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/** The home peek after a task screen (RF-41) and "Apply" on the appearance pickers (RF-39), in plain JVM. */
class OnboardingHomePeekTest {

    private val checklist = OnboardingUiState(stage = OnboardingStage.CHECKLIST, isChecklistExpanded = false)

    @Test
    fun `when animations are on, should peek for 900 ms, else for 300 ms`() {
        assertEquals(ONBOARDING_HOME_PEEK_MS, onboardingHomePeekMillis(areAnimatorsEnabled = true))
        assertEquals(900L, ONBOARDING_HOME_PEEK_MS)
        assertEquals(ONBOARDING_HOME_PEEK_REDUCED_MS, onboardingHomePeekMillis(areAnimatorsEnabled = false))
        assertEquals(300L, ONBOARDING_HOME_PEEK_REDUCED_MS)
    }

    @Test
    fun `when the peek starts, should hide the list and flag the peek`() {
        val peeking = checklist.copy(isChecklistExpanded = true).peekingHome()

        assertTrue(peeking.isHomePeeking)
        assertFalse(peeking.isChecklistExpanded)
    }

    @Test
    fun `when the peek ends on the checklist with nothing open, should bring the list back`() {
        val ended = checklist.peekingHome().endingHomePeek()

        assertFalse(ended.isHomePeeking)
        assertTrue(ended.isChecklistExpanded)
    }

    @Test
    fun `when the peek ends with a task or a picker open, should not bring the list back`() {
        val withTask = checklist.peekingHome().copy(pendingTask = OnboardingTask.ADD_WIDGET).endingHomePeek()
        val withPicker = checklist.peekingHome().copy(activePicker = AppearancePicker.FONT).endingHomePeek()
        val completed = checklist.peekingHome().copy(stage = OnboardingStage.COMPLETED).endingHomePeek()

        listOf(withTask, withPicker, completed).forEach {
            assertFalse(it.isHomePeeking)
            assertFalse(it.isChecklistExpanded)
        }
    }

    @Test
    fun `when there is no peek, should leave the state untouched`() {
        assertSame(checklist, checklist.endingHomePeek())
    }

    @Test
    fun `when a task completes during the peek, should keep the list hidden`() {
        val previous = MainUiState(onboarding = checklist.peekingHome())
        val next = previous.copy(
            onboarding = previous.onboarding.copy(completedTasks = setOf(OnboardingTask.ADD_CLOCK))
        )

        val result = OnboardingReducer.reduce(previous, next)

        assertFalse(result.onboarding.isChecklistExpanded)
    }

    @Test
    fun `when Apply confirms the palette, should complete the task, close the picker and reopen the list`() {
        val picking = checklist.copy(activePicker = AppearancePicker.PALETTE, skippedTasks = emptySet())
        val state = MainUiState(onboarding = picking)

        val result = OnboardingReducer.applySignal(state, OnboardingSignal.PICKER_CONFIRMED).onboarding

        assertEquals(setOf(OnboardingTask.CHOOSE_PALETTE), result.completedTasks)
        assertEquals(null, result.activePicker)
        assertTrue(result.isChecklistExpanded)
    }

    @Test
    fun `when Apply confirms the font, should complete the font task`() {
        val state = MainUiState(onboarding = checklist.copy(activePicker = AppearancePicker.FONT))

        val result = OnboardingReducer.applySignal(state, OnboardingSignal.PICKER_CONFIRMED).onboarding

        assertEquals(setOf(OnboardingTask.CHOOSE_FONT), result.completedTasks)
        assertTrue(result.isChecklistExpanded)
    }

    @Test
    fun `when the picker was dismissed instead of applied, should not complete anything`() {
        val state = MainUiState(onboarding = checklist.copy(activePicker = null))

        val result = OnboardingReducer.applySignal(state, OnboardingSignal.PICKER_CONFIRMED)

        assertSame(state, result)
    }
}
