package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.data.model.resolvedTasks
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingTaskStatus
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.resolvedTasks
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.statusOf
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.taskProgress
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.toProgress
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.toUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingSkipTest {

    private val checklist = OnboardingUiState(stage = OnboardingStage.CHECKLIST, isChecklistExpanded = false)

    @Test
    fun `when a skippable task is skipped, should not complete it and should show the list again`() {
        val result = checklist.skipping(OnboardingTask.ADD_CLOCK, hasHomeItems = true)

        assertEquals(setOf(OnboardingTask.ADD_CLOCK), result.skippedTasks)
        assertTrue(result.completedTasks.isEmpty())
        assertTrue(result.isChecklistExpanded)
    }

    @Test
    fun `when the task cannot be skipped or is done, should change nothing`() {
        assertSame(checklist, checklist.skipping(OnboardingTask.CHOOSE_FONT, hasHomeItems = true))
        val done = checklist.copy(completedTasks = setOf(OnboardingTask.ADD_CLOCK))
        assertSame(done, done.skipping(OnboardingTask.ADD_CLOCK, hasHomeItems = true))
    }

    @Test
    fun `when the skipped task was pending, should drop it`() {
        val pending = checklist.copy(pendingTask = OnboardingTask.ADD_WIDGET)

        assertNull(pending.skipping(OnboardingTask.ADD_WIDGET, hasHomeItems = true).pendingTask)
    }

    @Test
    fun `when a skipped task is tapped, should take it back`() {
        val skipped = checklist.copy(skippedTasks = setOf(OnboardingTask.ADD_CLOCK, OnboardingTask.ADD_WIDGET))

        assertEquals(setOf(OnboardingTask.ADD_WIDGET), skipped.unskipping(OnboardingTask.ADD_CLOCK).skippedTasks)
        assertSame(checklist, checklist.unskipping(OnboardingTask.ADD_CLOCK))
    }

    @Test
    fun `when clock, widgets and container are skipped with an empty home, should skip the layout too`() {
        val result = checklist
            .skipping(OnboardingTask.ADD_CLOCK, hasHomeItems = false)
            .skipping(OnboardingTask.ADD_WIDGET, hasHomeItems = false)
            .skipping(OnboardingTask.FILL_CONTAINER, hasHomeItems = false)

        assertTrue(OnboardingTask.ADJUST_LAYOUT in result.skippedTasks)
    }

    @Test
    fun `when the home has an item or one task is not skipped, should keep the layout pending`() {
        val withItems = checklist
            .skipping(OnboardingTask.ADD_CLOCK, hasHomeItems = true)
            .skipping(OnboardingTask.ADD_WIDGET, hasHomeItems = true)
            .skipping(OnboardingTask.FILL_CONTAINER, hasHomeItems = true)
        val partial = checklist
            .skipping(OnboardingTask.ADD_CLOCK, hasHomeItems = false)
            .skipping(OnboardingTask.ADD_WIDGET, hasHomeItems = false)

        assertFalse(OnboardingTask.ADJUST_LAYOUT in withItems.skippedTasks)
        assertFalse(OnboardingTask.ADJUST_LAYOUT in partial.skippedTasks)
    }

    @Test
    fun `when skipped tasks exist, should count them as resolved for the next task and the progress`() {
        val state = checklist.copy(
            completedTasks = setOf(
                OnboardingTask.SET_DEFAULT_HOME,
                OnboardingTask.CHOOSE_PALETTE,
                OnboardingTask.CHOOSE_FONT
            ),
            skippedTasks = setOf(OnboardingTask.ADD_CLOCK)
        )

        assertEquals(OnboardingTask.ADD_WIDGET, state.nextPendingTask)
        assertEquals(4, state.resolvedTasks.size)
        assertEquals(4f / OnboardingTask.entries.size, state.taskProgress, 0.0001f)
    }

    @Test
    fun `when skipped tasks are saved, should keep them apart from the done ones round trip`() {
        val state = checklist.copy(
            completedTasks = setOf(OnboardingTask.CHOOSE_FONT),
            skippedTasks = setOf(OnboardingTask.ADD_WIDGET)
        )

        val progress = state.toProgress()

        assertEquals(setOf(OnboardingTask.ADD_WIDGET), progress.skippedTasks)
        assertEquals(setOf(OnboardingTask.CHOOSE_FONT, OnboardingTask.ADD_WIDGET), progress.resolvedTasks)
        assertEquals(progress.skippedTasks, progress.toUiState(isDefaultLauncher = false).skippedTasks)
    }

    @Test
    fun `when the last unresolved task is skipped, should complete and celebrate`() {
        val previous = MainUiState(
            onboarding = checklist.copy(completedTasks = OnboardingTask.entries.toSet() - OnboardingTask.ADD_WIDGET)
        )
        val next = previous.copy(
            onboarding = previous.onboarding.skipping(OnboardingTask.ADD_WIDGET, hasHomeItems = true)
        )

        val result = OnboardingReducer.reduce(previous, next).onboarding

        assertEquals(OnboardingStage.COMPLETED, result.stage)
        assertTrue(result.isCelebrating)
        assertTrue(OnboardingTask.ADD_WIDGET !in result.completedTasks)
    }

    @Test
    fun `when only skipped tasks are missing, should still celebrate`() {
        val skipped = setOf(OnboardingTask.ADD_CLOCK, OnboardingTask.FILL_CONTAINER)
        val state = MainUiState(
            onboarding = checklist.copy(
                completedTasks = OnboardingTask.entries.toSet() - skipped,
                skippedTasks = skipped
            )
        )

        assertTrue(OnboardingReducer.reduce(state, state).onboarding.isCelebrating)
    }

    @Test
    fun `when an item task is done, should let the layout step start`() {
        assertFalse(checklist.hasItemTaskDone)
        assertFalse(checklist.copy(completedTasks = setOf(OnboardingTask.CHOOSE_FONT)).hasItemTaskDone)
        assertTrue(checklist.copy(completedTasks = setOf(OnboardingTask.ADD_FAVORITE_APPS)).hasItemTaskDone)
    }

    @Test
    fun `when the grid is saved with no task, should complete the layout step once`() {
        val result = checklist.copy(skippedTasks = setOf(OnboardingTask.ADJUST_LAYOUT)).completingLayoutOnOwnSave()

        assertEquals(setOf(OnboardingTask.ADJUST_LAYOUT), result.completedTasks)
        assertTrue(result.skippedTasks.isEmpty())
        assertSame(result, result.completingLayoutOnOwnSave())
    }

    @Test
    fun `when a task is pending or the list is not on screen, should not complete the layout step`() {
        val pending = checklist.copy(pendingTask = OnboardingTask.ADD_FAVORITE_APPS)
        val completed = checklist.copy(stage = OnboardingStage.COMPLETED)

        assertSame(pending, pending.completingLayoutOnOwnSave())
        assertSame(completed, completed.completingLayoutOnOwnSave())
    }

    @Test
    fun `the layout step is judged on save and always meets its requirement`() {
        assertTrue(OnboardingTask.ADJUST_LAYOUT.isJudgedOnSave)
        assertTrue(OnboardingTask.ADD_CLOCK.isJudgedOnSave)
        assertFalse(OnboardingTask.EXPLORE_SETTINGS.isJudgedOnSave)
        assertTrue(MainUiState().meetsRequirement(OnboardingTask.ADJUST_LAYOUT))
    }

    @Test
    fun `the checklist has nine tasks with the layout step right before the settings`() {
        val tasks = OnboardingTask.entries

        assertEquals(9, tasks.size)
        assertEquals(OnboardingTask.ADJUST_LAYOUT, tasks[tasks.indexOf(OnboardingTask.EXPLORE_SETTINGS) - 1])
        assertEquals(
            setOf(
                OnboardingTask.ADD_CLOCK,
                OnboardingTask.ADD_WIDGET,
                OnboardingTask.ADD_FAVORITE_APPS,
                OnboardingTask.FILL_CONTAINER,
                OnboardingTask.ADJUST_LAYOUT
            ),
            tasks.filter { it.isSkippable }.toSet()
        )
    }

    @Test
    fun `row status should tell done, skipped, the leading task and the rest apart`() {
        val state = checklist.copy(
            completedTasks = setOf(OnboardingTask.SET_DEFAULT_HOME),
            skippedTasks = setOf(OnboardingTask.CHOOSE_PALETTE)
        )
        val next = state.nextPendingTask

        assertEquals(OnboardingTaskStatus.DONE, state.statusOf(OnboardingTask.SET_DEFAULT_HOME, next))
        assertEquals(OnboardingTaskStatus.SKIPPED, state.statusOf(OnboardingTask.CHOOSE_PALETTE, next))
        assertEquals(OnboardingTaskStatus.NEXT, state.statusOf(OnboardingTask.CHOOSE_FONT, next))
        assertEquals(OnboardingTaskStatus.PENDING, state.statusOf(OnboardingTask.ADD_CLOCK, next))
    }
}
