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

internal fun HomeViewModel.expandChecklist() {
    updateOnboarding(OnboardingStage.CHECKLIST) { copy(isChecklistExpanded = true) }
}

/** Adds the item of [type] at its onboarding size to the active layout, saved; returns its id, null with no room. */
internal fun HomeViewModel.placeSilently(type: LauncherItemType): String? {
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
