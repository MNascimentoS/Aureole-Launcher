package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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
    fun `when every task is done, there should be no next task`() {
        assertNull(OnboardingUiState(completedTasks = OnboardingTask.entries.toSet()).nextPendingTask)
    }
}
