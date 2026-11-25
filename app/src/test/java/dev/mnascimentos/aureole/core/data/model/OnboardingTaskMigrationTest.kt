package dev.mnascimentos.aureole.core.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingTaskMigrationTest {

    @Test
    fun `when palette and font were both done, the visual step should be done`() {
        val result = migrateOnboardingTasks(setOf("CHOOSE_PALETTE", "CHOOSE_FONT", "ADD_CLOCK"), emptySet())

        assertEquals(setOf(OnboardingTask.CHOOSE_VISUAL, OnboardingTask.ADD_CLOCK), result.completed)
        assertTrue(result.skipped.isEmpty())
    }

    @Test
    fun `when one of them was done and the other skipped, the visual step should be skipped`() {
        val result = migrateOnboardingTasks(setOf("CHOOSE_PALETTE"), setOf("CHOOSE_FONT"))

        assertTrue(result.completed.isEmpty())
        assertEquals(setOf(OnboardingTask.CHOOSE_VISUAL), result.skipped)
    }

    @Test
    fun `when only one of them was done, the visual step should stay pending`() {
        val result = migrateOnboardingTasks(setOf("CHOOSE_FONT", "SET_DEFAULT_HOME"), setOf("ADD_WIDGET"))

        assertEquals(setOf(OnboardingTask.SET_DEFAULT_HOME), result.completed)
        assertEquals(setOf(OnboardingTask.ADD_WIDGET), result.skipped)
    }

    @Test
    fun `when neither was done, nothing should change`() {
        val result = migrateOnboardingTasks(setOf("ADD_CLOCK"), setOf("ADD_WIDGET"))

        assertEquals(OnboardingTaskSets(setOf(OnboardingTask.ADD_CLOCK), setOf(OnboardingTask.ADD_WIDGET)), result)
    }

    @Test
    fun `when the new format is already saved, should read it as it is and ignore unknown names`() {
        val result = migrateOnboardingTasks(setOf("CHOOSE_VISUAL", "OLD_TASK"), setOf("EXPLORE_SETTINGS"))

        assertEquals(setOf(OnboardingTask.CHOOSE_VISUAL), result.completed)
        assertEquals(setOf(OnboardingTask.EXPLORE_SETTINGS), result.skipped)
    }

    @Test
    fun `when the visual step is already done, an old skip should not mark it skipped too`() {
        val result = migrateOnboardingTasks(setOf("CHOOSE_VISUAL"), setOf("CHOOSE_FONT"))

        assertEquals(setOf(OnboardingTask.CHOOSE_VISUAL), result.completed)
        assertTrue(result.skipped.isEmpty())
    }
}
