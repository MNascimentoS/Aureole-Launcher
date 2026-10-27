package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.designsystem.palette.resolveThemeSeedColor
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSignal

/**
 * Appearance tasks open their picker on the home. Every other task opens an existing editor, so the
 * list collapses to the pill first: the onboarding is the top layer and must not cover that editor.
 */
fun HomeViewModel.onTaskClick(task: OnboardingTask) {
    when (task) {
        OnboardingTask.CHOOSE_PALETTE -> openAppearancePicker(AppearancePicker.PALETTE)
        OnboardingTask.CHOOSE_FONT -> openAppearancePicker(AppearancePicker.FONT)
        else -> setChecklistExpanded(false)
    }
}

fun HomeViewModel.openAppearancePicker(picker: AppearancePicker) {
    updateOnboarding(OnboardingStage.CHECKLIST) { copy(activePicker = picker) }
}

fun HomeViewModel.dismissAppearancePicker() {
    updateOnboarding(OnboardingStage.CHECKLIST) { copy(activePicker = null) }
}

/** Applies the palette right away so the whole home re-themes behind the picker. */
fun HomeViewModel.selectPalette(themeName: String) {
    val seedColor = resolveThemeSeedColor(themeName)
    settingsRepository.selectedThemeName = themeName
    settingsRepository.manualSeedColor = seedColor
    updateUiState { it.copy(selectedThemeName = themeName, manualSeedColor = seedColor) }
}

fun HomeViewModel.selectFont(fontName: String) {
    settingsRepository.selectedFontName = fontName
    updateUiState { it.copy(selectedFontName = fontName) }
}

fun HomeViewModel.confirmAppearancePicker() {
    onOnboardingSignal(OnboardingSignal.PICKER_CONFIRMED)
}

fun HomeViewModel.setChecklistExpanded(expanded: Boolean) {
    updateOnboarding(OnboardingStage.CHECKLIST) { copy(isChecklistExpanded = expanded) }
}

fun HomeViewModel.dismissChecklist() {
    updateOnboarding(OnboardingStage.CHECKLIST) { copy(stage = OnboardingStage.COMPLETED, activePicker = null) }
}

fun HomeViewModel.finishCelebration() {
    updateOnboarding(OnboardingStage.COMPLETED) { copy(isCelebrating = false) }
}
