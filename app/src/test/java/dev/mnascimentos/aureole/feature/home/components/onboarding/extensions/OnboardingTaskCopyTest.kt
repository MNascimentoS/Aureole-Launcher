package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.isTaskLocked
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingTaskCopyTest {

    @Test
    fun `every task should have its own label`() {
        val labels = OnboardingTask.entries.map { it.titleRes() }

        assertEquals(OnboardingTask.entries.size, labels.toSet().size)
    }

    @Test
    fun `next task should be the first pending one in list order`() {
        val state = OnboardingUiState(
            completedTasks = setOf(OnboardingTask.SET_DEFAULT_HOME, OnboardingTask.CHOOSE_FONT)
        )

        assertEquals(OnboardingTask.CHOOSE_PALETTE, state.nextPendingTask)
    }

    @Test
    fun `after the clock is added, next task should be the widget`() {
        val state = OnboardingUiState(
            completedTasks = setOf(
                OnboardingTask.SET_DEFAULT_HOME,
                OnboardingTask.CHOOSE_PALETTE,
                OnboardingTask.CHOOSE_FONT,
                OnboardingTask.ADD_CLOCK
            )
        )

        assertEquals(OnboardingTask.ADD_WIDGET, state.nextPendingTask)
    }

    @Test
    fun `when every task is done, there should be no next task`() {
        assertNull(OnboardingUiState(completedTasks = OnboardingTask.entries.toSet()).nextPendingTask)
    }

    @Test
    fun `the list should show the widget right after the clock`() {
        assertEquals(
            listOf(
                OnboardingTask.SET_DEFAULT_HOME,
                OnboardingTask.CHOOSE_PALETTE,
                OnboardingTask.CHOOSE_FONT,
                OnboardingTask.ADD_CLOCK,
                OnboardingTask.ADD_WIDGET,
                OnboardingTask.ADD_FAVORITE_APPS,
                OnboardingTask.FILL_CONTAINER,
                OnboardingTask.ADJUST_LAYOUT,
                OnboardingTask.EXPLORE_SETTINGS
            ),
            OnboardingTask.entries.toList()
        )
    }

    @Test
    fun `layout and settings should be locked while another task is pending`() {
        val state = OnboardingUiState(completedTasks = setOf(OnboardingTask.SET_DEFAULT_HOME))

        assertTrue(state.isTaskLocked(OnboardingTask.ADJUST_LAYOUT))
        assertTrue(state.isTaskLocked(OnboardingTask.EXPLORE_SETTINGS))
        assertFalse(state.isTaskLocked(OnboardingTask.ADD_WIDGET))
    }

    @Test
    fun `layout and settings should unlock when the other tasks are done or skipped`() {
        val others = OnboardingTask.entries - setOf(OnboardingTask.ADJUST_LAYOUT, OnboardingTask.EXPLORE_SETTINGS)
        val state = OnboardingUiState(
            completedTasks = others.take(3).toSet(),
            skippedTasks = others.drop(3).toSet()
        )

        assertFalse(state.isTaskLocked(OnboardingTask.ADJUST_LAYOUT))
        assertFalse(state.isTaskLocked(OnboardingTask.EXPLORE_SETTINGS))
    }
}
