package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.extensions.persistGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.extensions.updateActiveGridItemsInState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Starts an item task: puts its item on the home at the onboarding size (saved at once, with no grid edit mode),
 * collapses the checklist and remembers the task until its own screen closes. A home that already has the item
 * reuses it. Returns the item id to open the task's screen with, or null when the grid has no room for it (the
 * checklist stays open and nothing is added).
 */
fun HomeViewModel.startGridTask(task: OnboardingTask): String? {
    val type = task.gridItemType ?: return null
    val existing = uiState.value.gridItems.firstOrNull { it.safeType == type }?.id
    val itemId = existing ?: placeSilently(type)
    if (itemId == null) {
        expandChecklist()
        return null
    }
    updateUiState { state ->
        state.copy(
            onboarding = state.onboarding.copy(
                pendingTask = task,
                createdItemId = itemId.takeIf { existing == null },
                isPendingTaskSaved = false,
                isChecklistExpanded = false
            )
        )
    }
    return itemId
}

/**
 * Runs after every state update. A task's screen (or the grid edit mode) closing is the moment to judge the
 * pending task, whether the user saved or not.
 */
internal fun HomeViewModel.confirmPendingTask(previous: MainUiState) {
    val state = uiState.value
    val task = state.onboarding.pendingTask?.takeIf { it.isJudgedOnSave } ?: return
    if (!previous.isTaskInProgress || state.isTaskInProgress) return
    onOnboardingTaskScreenClosed(task)
}

/**
 * The single exit of a task's screen. Done when the home shows what the task asks for, else the task stays pending
 * and the item it created goes away (a clock or container stays: they come with default content). The layout step
 * goes straight back to the list; the other tasks show the home alone for a moment first.
 */
fun HomeViewModel.onOnboardingTaskScreenClosed(task: OnboardingTask) {
    val meets = uiState.value.meetsRequirement(task)
    val hasPeek = task != OnboardingTask.ADJUST_LAYOUT
    if (meets) completeTask(task, hasPeek) else failPendingTask(task, hasPeek)
    if (hasPeek) scheduleEndOfHomePeek()
}

private fun HomeViewModel.scheduleEndOfHomePeek() {
    viewModelScope.launch {
        delay(onboardingHomePeekMillis())
        updateUiState { it.copy(onboarding = it.onboarding.endingHomePeek()) }
    }
}

/**
 * The container editor's Save: the container task counts as saved even with the container empty, and is judged
 * when the editor closes. The layout is saved in the same action in case it was being edited.
 */
fun HomeViewModel.confirmContainerTaskOnSave() {
    if (uiState.value.onboarding.pendingTask != OnboardingTask.FILL_CONTAINER) return
    updateUiState { it.copy(onboarding = it.onboarding.copy(isPendingTaskSaved = true)) }
    if (uiState.value.isGridEditMode) persistGridEditMode()
}

/** The home went to the background (Settings, another app, a system dialog). */
fun HomeViewModel.onHomeHidden() {
    updateUiState { it.copy(onboarding = it.onboarding.copy(isHomeInForeground = false)) }
}

/** Back on the home: completes a Settings visit and plays a celebration that waited for the home. */
fun HomeViewModel.onReturnedToHome() {
    updateUiState { state ->
        val shown = state.onboarding.copy(isHomeInForeground = true)
        val visited = if (shown.pendingTask == OnboardingTask.EXPLORE_SETTINGS) {
            shown.copy(
                completedTasks = shown.completedTasks + OnboardingTask.EXPLORE_SETTINGS,
                pendingTask = null,
                isChecklistExpanded = true
            )
        } else {
            shown
        }
        val celebrated = if (visited.isCelebrationPending) {
            visited.copy(isCelebrating = true, isCelebrationPending = false)
        } else {
            visited
        }
        state.copy(onboarding = celebrated)
    }
}

/**
 * Marks [task] done (it leaves the skipped ones) and clears a pending task. The list comes back at once, or after
 * the home peek when [hasPeek].
 */
internal fun HomeViewModel.completeTask(task: OnboardingTask, hasPeek: Boolean = false) {
    updateUiState { state ->
        val onboarding = state.onboarding.copy(
            completedTasks = state.onboarding.completedTasks + task,
            skippedTasks = state.onboarding.skippedTasks - task,
            pendingTask = null,
            createdItemId = null,
            isPendingTaskSaved = false,
            isChecklistExpanded = true
        )
        state.copy(onboarding = if (hasPeek) onboarding.peekingHome() else onboarding)
    }
}

/**
 * Takes the created item back off the home, except a clock or container, which keep their default content. An apps
 * list the task created and left without favorites stays, with the same id and "Show all apps" on, so the home
 * still shows apps and a retry reuses it; the task stays pending either way.
 */
private fun HomeViewModel.failPendingTask(task: OnboardingTask, hasPeek: Boolean) {
    val createdId = uiState.value.onboarding.createdItemId
    val keepsItem = task == OnboardingTask.ADD_CLOCK || task == OnboardingTask.FILL_CONTAINER
    updateUiState { state ->
        val grid = if (keepsItem) state.gridItems else state.gridItems.withoutFailedItem(task, createdId)
        val abandoned = state.onboarding.abandonPendingTask()
        updateActiveGridItemsInState(state, grid).copy(
            onboarding = if (hasPeek) abandoned.peekingHome() else abandoned
        )
    }
    if (createdId != null && !keepsItem) {
        gridRepository.saveGridItems(uiState.value.gridItems, uiState.value.isLandscape)
    }
}
