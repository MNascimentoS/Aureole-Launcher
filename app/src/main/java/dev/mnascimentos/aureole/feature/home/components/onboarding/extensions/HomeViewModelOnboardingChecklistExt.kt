package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.designsystem.palette.resolveThemeSeedColor
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSignal
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.VisualChoice
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.resolveVisualClose

internal const val DEFAULT_PALETTE_NAME = "Frostbite"
internal const val DEFAULT_FONT_NAME = "Istok Web"

/** What the Visual sheet applies to a tab left untouched the first time (as the separate pickers did). */
internal val visualDefaults = VisualChoice(paletteName = DEFAULT_PALETTE_NAME, fontName = DEFAULT_FONT_NAME)

/**
 * The Visual step opens its sheet (Palette | Font, RF-C1) on the home. Every other task opens its own screen or the
 * edit mode, so the list collapses to the pill first: the onboarding is the top layer and must not cover it.
 * A skipped task tapped again is taken back.
 */
fun HomeViewModel.onTaskClick(task: OnboardingTask) {
    updateOnboarding(OnboardingStage.CHECKLIST) { unskipping(task) }
    when (task) {
        OnboardingTask.CHOOSE_VISUAL -> openVisualSheet()
        else -> setChecklistExpanded(false)
    }
}

fun HomeViewModel.openVisualSheet(tab: AppearancePicker = AppearancePicker.PALETTE) {
    updateOnboarding(OnboardingStage.CHECKLIST) { copy(activePicker = tab) }
}

/**
 * The Visual sheet closed, whichever way (no button, D-05): applies what was tapped, and the defaults of the tabs
 * left untouched the first time ([resolveVisualClose]), then completes the step.
 */
fun HomeViewModel.closeVisualSheet(tapped: VisualChoice) {
    val onboarding = uiState.value.onboarding
    if (onboarding.stage != OnboardingStage.CHECKLIST) return
    val isFirstVisit = OnboardingTask.CHOOSE_VISUAL !in onboarding.completedTasks
    val choice = resolveVisualClose(tapped, isFirstVisit, visualDefaults)
    choice.paletteName?.let(::selectPalette)
    choice.fontName?.let(::selectFont)
    onOnboardingSignal(OnboardingSignal.PICKER_CONFIRMED)
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

fun HomeViewModel.setChecklistExpanded(expanded: Boolean) {
    updateOnboarding(OnboardingStage.CHECKLIST) { copy(isChecklistExpanded = expanded) }
}

fun HomeViewModel.dismissChecklist() {
    if (uiState.value.onboarding.stage != OnboardingStage.CHECKLIST) return
    // Before completing: completion adds the apps component, which would make the home look built.
    applyDefaultLayoutWhereEmpty()
    updateOnboarding(OnboardingStage.CHECKLIST) { copy(stage = OnboardingStage.COMPLETED, activePicker = null) }
}

fun HomeViewModel.finishCelebration() {
    updateOnboarding(OnboardingStage.COMPLETED) { copy(isCelebrating = false) }
}
