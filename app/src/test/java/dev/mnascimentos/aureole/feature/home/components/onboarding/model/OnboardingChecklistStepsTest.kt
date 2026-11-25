package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.checklistCollapseDelayMillis
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.nextPendingTask
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/** The 8-step machine of onboarding-redesenho: sections, current step, locked extras and what reacts (RF-A1–A4). */
class OnboardingChecklistStepsTest {

    private val homeTasks = OnboardingTask.entries.filterNot { it.isExtra }

    @Test
    fun `should split the eight steps into six for the home and two extras`() {
        assertEquals(homeTasks, onboardingHomeTasks)
        assertEquals(6, onboardingHomeTasks.size)
        assertEquals(listOf(OnboardingTask.ADJUST_LAYOUT, OnboardingTask.EXPLORE_SETTINGS), onboardingExtraTasks)
    }

    @Test
    fun `the home should be built only when all its steps are done or skipped`() {
        val partial = OnboardingUiState(completedTasks = homeTasks.drop(1).toSet())
        val built = partial.copy(skippedTasks = setOf(homeTasks.first()))

        assertFalse(OnboardingUiState().isHomeBuilt)
        assertFalse(partial.isHomeBuilt)
        assertTrue(built.isHomeBuilt)
    }

    @Test
    fun `extras should be locked until the home is built, and never the home steps`() {
        val pending = OnboardingUiState()
        val built = OnboardingUiState(completedTasks = homeTasks.toSet())

        onboardingExtraTasks.forEach { assertTrue(pending.isTaskLocked(it)) }
        homeTasks.forEach { assertFalse(pending.isTaskLocked(it)) }
        onboardingExtraTasks.forEach { assertFalse(built.isTaskLocked(it)) }
    }

    @Test
    fun `the current step should be the first unresolved one, extras only after the home`() {
        val state = OnboardingUiState(
            completedTasks = homeTasks.toSet() - OnboardingTask.ADD_WIDGET,
            skippedTasks = setOf(OnboardingTask.ADD_WIDGET)
        )

        assertEquals(OnboardingTask.ADJUST_LAYOUT, state.nextPendingTask)
        assertEquals(OnboardingTaskStatus.NEXT, state.statusOf(OnboardingTask.ADJUST_LAYOUT, state.nextPendingTask))
        assertEquals(6f / 8, state.taskProgress, 0.0001f)
    }

    @Test
    fun `done rows should react to be redone, except the home app, and locked rows never`() {
        assertTrue(OnboardingTaskStatus.DONE.isClickable(OnboardingTask.ADD_CLOCK))
        assertFalse(OnboardingTaskStatus.DONE.isClickable(OnboardingTask.SET_DEFAULT_HOME))
        assertFalse(OnboardingTaskStatus.LOCKED.isClickable(OnboardingTask.ADJUST_LAYOUT))
        assertTrue(OnboardingTaskStatus.SKIPPED.isClickable(OnboardingTask.SET_DEFAULT_HOME))
        assertTrue(OnboardingTaskStatus.NEXT.isClickable(OnboardingTask.CHOOSE_VISUAL))
        assertTrue(OnboardingTaskStatus.PENDING.isClickable(OnboardingTask.EXPLORE_SETTINGS))
    }

    @Test
    fun `the glass should drop the blur only while the pill and the card move`() {
        val glass = OnboardingGlass(isHazeEnabled = true)

        assertFalse(glass.atRest(isMoving = true).isHazeEnabled)
        assertSame(glass, glass.atRest(isMoving = false))
    }

    @Test
    fun `a tapped step should wait for the card to shrink, and not at all with animations off`() {
        assertEquals(OnboardingMotionSpec.CHECKLIST_MORPH_MS.toLong(), checklistCollapseDelayMillis(true))
        assertEquals(0L, checklistCollapseDelayMillis(false))
    }
}
