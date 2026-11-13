package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState

/** The tasks that put an item on the home; the layout step needs at least one of them done. */
private val itemTasks = setOf(
    OnboardingTask.ADD_CLOCK,
    OnboardingTask.ADD_WIDGET,
    OnboardingTask.ADD_FAVORITE_APPS,
    OnboardingTask.FILL_CONTAINER
)

/** The tasks that, all skipped with an empty home, leave nothing to adjust. */
private val itemTasksWithoutApps = setOf(
    OnboardingTask.ADD_CLOCK,
    OnboardingTask.ADD_WIDGET,
    OnboardingTask.FILL_CONTAINER
)

/** At least one item task is done, so the layout step has something to adjust. */
internal val OnboardingUiState.hasItemTaskDone: Boolean
    get() = completedTasks.any { it in itemTasks }

/**
 * Marks [task] skipped: it stops blocking but stays undone. A task that is not skippable or is already done is
 * left alone. When the clock, the widgets and the container are all skipped and the home has no item, the layout
 * step has nothing to adjust, so it is skipped with them.
 */
internal fun OnboardingUiState.skipping(task: OnboardingTask, hasHomeItems: Boolean): OnboardingUiState {
    if (!task.isSkippable || task in completedTasks) return this
    val skipped = skippedTasks + task
    val nothingToAdjust = !hasHomeItems && skipped.containsAll(itemTasksWithoutApps)
    return copy(
        skippedTasks = if (nothingToAdjust) skipped + OnboardingTask.ADJUST_LAYOUT else skipped,
        pendingTask = pendingTask.takeUnless { it == task },
        isChecklistExpanded = true
    )
}

/** Tapping a skipped task takes it back: it is pending again. */
internal fun OnboardingUiState.unskipping(task: OnboardingTask): OnboardingUiState =
    if (task in skippedTasks) copy(skippedTasks = skippedTasks - task) else this

/** Saving the grid edit mode with no task pending is the layout step done, when the list is on screen. */
internal fun OnboardingUiState.completingLayoutOnOwnSave(): OnboardingUiState {
    val task = OnboardingTask.ADJUST_LAYOUT
    if (stage != OnboardingStage.CHECKLIST || pendingTask != null || task in completedTasks) return this
    return copy(completedTasks = completedTasks + task, skippedTasks = skippedTasks - task)
}
