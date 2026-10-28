package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.grid.extensions.saveGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.extensions.updateActiveGridItemsInState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import java.util.UUID

/**
 * Puts the task's item on the home (in edit mode) and remembers the task until the user saves.
 * Returns the item id to open its editor with, or null when the grid has no room for it.
 */
fun HomeViewModel.startGridTask(task: OnboardingTask): String? {
    val type = task.gridItemType ?: return null
    val existed = uiState.value.gridItems.any { it.safeType == type }
    val itemId = ensureGridItem(type)
    updateUiState { state ->
        val onboarding = if (itemId == null) {
            state.onboarding.copy(isChecklistExpanded = true)
        } else {
            state.onboarding.copy(pendingTask = task, createdItemId = itemId.takeUnless { existed })
        }
        state.copy(onboarding = onboarding)
    }
    return itemId
}

/**
 * Runs after every state update. The pending task is judged once, when its editor and the grid edit
 * mode have both closed: that is the moment the user saved.
 */
internal fun HomeViewModel.confirmPendingTask(previous: MainUiState) {
    val state = uiState.value
    val task = state.onboarding.pendingTask?.takeIf { it.gridItemType != null } ?: return
    if (!previous.isTaskInProgress || state.isTaskInProgress) return
    if (state.meetsRequirement(task)) completePendingTask(task) else failPendingTask(task)
}

/**
 * The container editor's Save confirms the container task, even with the container empty. The layout
 * is saved in the same action so the new container survives a later cancel of the edit mode.
 */
fun HomeViewModel.confirmContainerTaskOnSave() {
    if (uiState.value.onboarding.pendingTask != OnboardingTask.FILL_CONTAINER) return
    completePendingTask(OnboardingTask.FILL_CONTAINER)
    if (uiState.value.isGridEditMode) saveGridEditMode()
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

private fun HomeViewModel.completePendingTask(task: OnboardingTask) {
    updateUiState { state ->
        val onboarding = state.onboarding
        state.copy(
            onboarding = onboarding.copy(
                completedTasks = onboarding.completedTasks + task,
                pendingTask = null,
                createdItemId = null,
                isChecklistExpanded = true
            )
        )
    }
}

/**
 * Takes the created item back off the home. A favorites list left empty becomes the all apps list
 * instead, so the home still shows apps; the task stays pending either way.
 */
private fun HomeViewModel.failPendingTask(task: OnboardingTask) {
    val createdId = uiState.value.onboarding.createdItemId
    updateUiState { state ->
        val grid = state.gridItems.withoutFailedItem(task, createdId)
        updateActiveGridItemsInState(state, grid).copy(onboarding = state.onboarding.abandonPendingTask())
    }
    if (createdId != null) gridRepository.saveGridItems(uiState.value.gridItems, uiState.value.isLandscape)
}

private fun List<LauncherItemState>.withoutFailedItem(
    task: OnboardingTask,
    createdId: String?
): List<LauncherItemState> = when {
    createdId == null -> this
    task == OnboardingTask.ADD_FAVORITE_APPS -> map { item ->
        if (item.id == createdId) {
            item.copy(id = UUID.randomUUID().toString(), type = LauncherItemType.ALL_APPS_LIST)
        } else {
            item
        }
    }
    else -> filterNot { it.id == createdId }
}
