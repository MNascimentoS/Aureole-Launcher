package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.core.designsystem.palette.resolveThemeSeedColor
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSignal

internal const val DEFAULT_PALETTE_NAME = "Frostbite"
internal const val DEFAULT_FONT_NAME = "Istok Web"

/**
 * The Visual step opens the palette picker on the home (then the font one). Every other task opens its own screen
 * or the edit mode, so the list collapses to the pill first: the onboarding is the top layer and must not cover it.
 * A skipped task tapped again is taken back.
 */
fun HomeViewModel.onTaskClick(task: OnboardingTask) {
    updateOnboarding(OnboardingStage.CHECKLIST) { unskipping(task) }
    when (task) {
        OnboardingTask.CHOOSE_VISUAL -> openAppearancePicker(AppearancePicker.PALETTE)
        else -> setChecklistExpanded(false)
    }
}

fun HomeViewModel.openAppearancePicker(picker: AppearancePicker) {
    updateOnboarding(OnboardingStage.CHECKLIST) { copy(activePicker = picker) }
}

/**
 * The picker closed without "Apply" and counts as done. A previewed option stays as it is; with no tap at all the
 * default applies (Frostbite / Istok Web). Either way it follows the confirm path.
 */
fun HomeViewModel.dismissAppearancePicker(hadPreview: Boolean = false) {
    if (uiState.value.onboarding.stage != OnboardingStage.CHECKLIST) return
    when (dismissDefaultFor(uiState.value.onboarding.activePicker, hadPreview)) {
        DismissDefault.PALETTE -> selectPalette(DEFAULT_PALETTE_NAME)
        DismissDefault.FONT -> selectFont(DEFAULT_FONT_NAME)
        DismissDefault.NONE -> Unit
    }
    confirmAppearancePicker()
}

internal enum class DismissDefault { NONE, PALETTE, FONT }

/** What a close without "Apply" applies: the default of the [picker] only when nothing was previewed. */
internal fun dismissDefaultFor(picker: AppearancePicker?, hadPreview: Boolean): DismissDefault = when {
    hadPreview -> DismissDefault.NONE
    picker == AppearancePicker.PALETTE -> DismissDefault.PALETTE
    picker == AppearancePicker.FONT -> DismissDefault.FONT
    else -> DismissDefault.NONE
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
    if (uiState.value.onboarding.stage != OnboardingStage.CHECKLIST) return
    // Before completing: completion adds the apps component, which would make the home look built.
    applyDefaultLayoutWhereEmpty()
    updateOnboarding(OnboardingStage.CHECKLIST) { copy(stage = OnboardingStage.COMPLETED, activePicker = null) }
}

fun HomeViewModel.finishCelebration() {
    updateOnboarding(OnboardingStage.COMPLETED) { copy(isCelebrating = false) }
}
