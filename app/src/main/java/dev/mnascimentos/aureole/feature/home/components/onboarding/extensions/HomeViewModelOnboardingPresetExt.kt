package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.LayoutPreset
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.containers.extensions.loadContainers
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.updateContainerFavorites
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.LayoutPresetPlan
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.PresetGroupKind
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.PresetPlacement
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.applyingPreset
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.planAll
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.undoingPreset
import dev.mnascimentos.aureole.feature.home.grid.extensions.loadGridItemsNow
import kotlinx.coroutines.launch

/**
 * "Use a ready-made layout" on the welcome (RF-F1): shows the presets and works them out from the installed apps,
 * off the main thread. Only an empty home takes a preset (D-22), so the button is not there otherwise.
 */
fun HomeViewModel.openPresets() {
    val state = uiState.value
    if (!canTakeLayoutPreset(state.portraitGridItems, state.landscapeGridItems)) return
    updateOnboarding(OnboardingStage.WELCOME) { copy(isChoosingPreset = true) }
    viewModelScope.launch {
        val plans = runCatching { appRepository.readPresetCatalog().planAll() }.getOrDefault(emptyList())
        updateOnboarding(OnboardingStage.WELCOME) { copy(presetPlans = plans) }
    }
}

/** Back from the presets to the welcome (the system back does the same). */
fun HomeViewModel.closePresets() {
    updateOnboarding(OnboardingStage.WELCOME) { copy(isChoosingPreset = false) }
}

fun HomeViewModel.selectPreset(preset: LayoutPreset) {
    updateOnboarding(OnboardingStage.WELCOME) { copy(selectedPreset = preset) }
}

/**
 * "Use <name>": saves the preset's home in both orientations, its clock look, favorites and group, and goes on to
 * the gestures with the steps it did already done (RF-F4). The 4 s "Undo" takes it all back (RF-F5). Nothing
 * happens while the plans are still being read or once the home has items (D-22).
 */
fun HomeViewModel.applyPreset(preset: LayoutPreset) {
    val state = uiState.value
    val onboarding = state.onboarding
    val plan = onboarding.presetPlans.firstOrNull { it.preset == preset } ?: return
    if (onboarding.stage != OnboardingStage.WELCOME) return
    if (!canTakeLayoutPreset(state.portraitGridItems, state.landscapeGridItems)) return
    val isLeftHanded = settingsRepository.isLeftHandedMode
    val portrait = presetPortraitItems(plan, isLeftHanded)
    gridRepository.saveGridItems(portrait, isLandscape = false)
    gridRepository.saveGridItems(onboardingLandscapeLayout(portrait, isLeftHanded), isLandscape = true)
    loadGridItemsNow(state.isLandscape)
    val placement = PresetPlacement(
        clockId = portrait.idOf(LauncherItemType.CLOCK),
        appsListId = portrait.idOf(LauncherItemType.APPS_LIST),
        containerId = portrait.idOf(LauncherItemType.SHORTCUTS_CONTAINER)
    )
    savePresetContent(plan, placement)
    updateOnboarding(OnboardingStage.WELCOME) { applyingPreset(plan) }
    offerPresetUndo(plan.preset, before = onboarding, placement = placement)
}

private fun HomeViewModel.savePresetContent(plan: LayoutPresetPlan, placement: PresetPlacement) {
    placement.clockId?.let { id ->
        val config = clockConfigRepository.defaultConfig().copy(style = plan.spec.clockLayout.key)
        updateUiState { it.copy(clockConfigs = it.clockConfigs + (id to config)) }
        viewModelScope.launch { clockConfigRepository.save(id, config) }
    }
    placement.appsListId?.takeIf { plan.favorites.isNotEmpty() }?.let { id ->
        updateContainerFavorites(id, plan.favorites)
    }
    val containerId = placement.containerId?.takeIf { plan.groupKind != PresetGroupKind.NONE } ?: return
    viewModelScope.launch {
        containerRepository.saveContainer(
            presetContainerModel(
                plan = plan,
                id = containerId,
                position = settingsRepository.containerPosition,
                isExpandCell = settingsRepository.isContainerExpandCell
            )
        )
        loadContainers()
    }
}

/**
 * The preset's "Layout <name> applied" with "Undo" (RF-F5). It goes straight to the controller: the usual offers
 * wait for the end of the tour, but this one belongs to it.
 */
private fun HomeViewModel.offerPresetUndo(preset: LayoutPreset, before: OnboardingUiState, placement: PresetPlacement) {
    val message = TextResource.fromResId(R.string.home_onboarding_preset_applied, TextResource.fromResId(preset.nameRes))
    undoController.show(message) { undoPreset(before, placement) }
    syncUndoState()
}

/** Back to the empty home and the welcome: the grid, the clock look, the favorites and the group all go. */
private fun HomeViewModel.undoPreset(before: OnboardingUiState, placement: PresetPlacement) {
    gridRepository.saveGridItems(emptyList(), isLandscape = false)
    gridRepository.saveGridItems(emptyList(), isLandscape = true)
    loadGridItemsNow(uiState.value.isLandscape)
    updateUiState { state ->
        state.copy(
            onboarding = state.onboarding.undoingPreset(before),
            clockConfigs = placement.clockId?.let { state.clockConfigs - it } ?: state.clockConfigs,
            containerFavorites = placement.appsListId?.let { state.containerFavorites - it } ?: state.containerFavorites
        )
    }
    viewModelScope.launch {
        placement.clockId?.let { clockConfigRepository.remove(it) }
        placement.appsListId?.let { favoriteContainerRepository.deleteContainer(it) }
        placement.containerId?.let { containerRepository.deleteContainer(it) }
        loadContainers()
    }
}
