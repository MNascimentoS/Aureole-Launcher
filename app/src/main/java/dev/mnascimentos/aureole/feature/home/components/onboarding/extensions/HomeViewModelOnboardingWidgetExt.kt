package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.resolvedTasks
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.launch

/**
 * The checklist just opened with the widget step still to do: time to read the widget providers, so the picker
 * opens on the list instead of a spinner (onboarding-redesenho RF-C4, D-20).
 */
internal fun shouldPreloadWidgets(previous: OnboardingUiState, current: OnboardingUiState): Boolean {
    val isOpen = current.stage == OnboardingStage.CHECKLIST && current.isChecklistExpanded
    val wasOpen = previous.stage == OnboardingStage.CHECKLIST && previous.isChecklistExpanded
    return isOpen && !wasOpen && OnboardingTask.ADD_WIDGET !in current.resolvedTasks
}

/** Reads the widget providers in the background when [shouldPreloadWidgets]; the repository keeps them. */
internal fun HomeViewModel.preloadWidgetsOnChecklist(previous: MainUiState) {
    if (!shouldPreloadWidgets(previous.onboarding, uiState.value.onboarding)) return
    viewModelScope.launch { widgetRepository.preloadAvailableWidgets() }
}
