package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.clock.extensions.giveNewClockItsConfig
import dev.mnascimentos.aureole.feature.home.components.containers.extensions.loadContainers
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingPlacementRequest
import dev.mnascimentos.aureole.feature.home.grid.extensions.enterGridEditMode
import dev.mnascimentos.aureole.feature.home.grid.extensions.updateActiveGridItemsInState
import java.util.UUID

/**
 * The clock and container tasks: puts the item on the home at its compact size and saves it on the spot, with no
 * editor and no grid edit mode. A home that already has one counts as done; with no room the list comes back
 * and nothing is added.
 */
fun HomeViewModel.addGridItemSilently(task: OnboardingTask) {
    val type = task.gridItemType ?: return
    val hasItem = uiState.value.gridItems.any { it.safeType == type }
    if (hasItem || placeSilently(type) != null) {
        completeTask(task)
    } else {
        expandChecklist()
    }
}

/**
 * The widgets task: places the (empty) widget list at its compact size when the home has none, saved, and
 * remembers the task until the picker closes. Returns the list id to open the picker for, or null with no room.
 */
fun HomeViewModel.startWidgetTask(task: OnboardingTask): String? {
    val type = task.gridItemType ?: return null
    val existing = uiState.value.gridItems.firstOrNull { it.safeType == type }?.id
    val itemId = existing ?: placeSilently(type)
    if (itemId == null) {
        expandChecklist()
        return null
    }
    updateUiState { state ->
        state.copy(
            onboarding = state.onboarding.copy(pendingTask = task, createdItemId = itemId.takeIf { existing == null })
        )
    }
    return itemId
}

/**
 * The layout step: opens the grid edit mode, which completes the task when saved. Before any item task is done
 * there is nothing to adjust, so only the hint shows and nothing changes.
 */
fun HomeViewModel.startLayoutTask() {
    if (!uiState.value.onboarding.hasItemTaskDone) {
        updateUiState {
            it.copy(gridErrorMessage = TextResource.fromResId(R.string.home_onboarding_layout_needs_item))
        }
        expandChecklist()
        return
    }
    if (!uiState.value.isGridEditMode) enterGridEditMode()
    updateUiState { it.copy(onboarding = it.onboarding.copy(pendingTask = OnboardingTask.ADJUST_LAYOUT)) }
}

/** The user saved the grid edit mode on their own, outside any task: the layout step counts as done. */
internal fun HomeViewModel.completeLayoutOnOwnSave() {
    updateUiState { it.copy(onboarding = it.onboarding.completingLayoutOnOwnSave()) }
}

/** "Skip" on the current row: the task stops blocking the list without being done. */
fun HomeViewModel.skipTask(task: OnboardingTask) {
    updateUiState { state ->
        if (state.onboarding.stage != OnboardingStage.CHECKLIST) {
            state
        } else {
            state.copy(onboarding = state.onboarding.skipping(task, hasHomeItems = state.gridItems.isNotEmpty()))
        }
    }
}

private fun HomeViewModel.expandChecklist() {
    updateOnboarding(OnboardingStage.CHECKLIST) { copy(isChecklistExpanded = true) }
}

/** Adds the compact item of [type] to the active layout and saves it; returns its id, null with no room. */
private fun HomeViewModel.placeSilently(type: LauncherItemType): String? {
    val state = uiState.value
    val item = placeOnboardingItem(
        OnboardingPlacementRequest(
            id = UUID.randomUUID().toString(),
            type = type,
            items = state.gridItems,
            limits = state.gridLimits,
            isLandscape = state.isLandscape,
            isLeftHanded = settingsRepository.isLeftHandedMode
        )
    ) ?: return null
    updateUiState { updateActiveGridItemsInState(it, it.gridItems + item) }
    gridRepository.saveGridItems(uiState.value.gridItems, uiState.value.isLandscape)
    when (type) {
        LauncherItemType.CLOCK -> giveNewClockItsConfig(item.id)
        LauncherItemType.SHORTCUTS_CONTAINER -> loadContainers()
        else -> Unit
    }
    return item.id
}
