package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSignal
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.currentGestureStep
import dev.mnascimentos.aureole.feature.home.extensions.setAllAppsDrawerOpen
import dev.mnascimentos.aureole.feature.home.extensions.setShowCustomizeBottomSheet

fun HomeViewModel.startOnboarding() {
    updateOnboarding(OnboardingStage.WELCOME) {
        copy(stage = if (isDefaultLauncher) OnboardingStage.GESTURES else OnboardingStage.DEFAULT_HOME)
    }
}

/** Nothing essential is lost: setting the home app is the first checklist item. */
fun HomeViewModel.skipIntro() {
    updateOnboarding(OnboardingStage.WELCOME) {
        copy(stage = OnboardingStage.CHECKLIST, isChecklistExpanded = true)
    }
}

/** Leaves the home app step, either after "Not now" or after its success message. */
fun HomeViewModel.postponeDefaultHome() {
    updateOnboarding(OnboardingStage.DEFAULT_HOME) { copy(stage = OnboardingStage.GESTURES) }
}

fun HomeViewModel.skipGestures() {
    updateOnboarding(OnboardingStage.GESTURES) {
        copy(
            stage = OnboardingStage.CHECKLIST,
            completedSteps = GestureCoachStep.entries.toSet(),
            succeededStep = null,
            isChecklistExpanded = true
        )
    }
}

/** Performs the current gesture for the user; the reducer then completes the step. */
fun HomeViewModel.showMeGesture() {
    val state = uiState.value
    if (state.onboarding.stage != OnboardingStage.GESTURES) return
    when (state.onboarding.currentGestureStep(state.isAlphabetScrubberDisabled, state.isLandscape)) {
        GestureCoachStep.OPEN_APPS -> setAllAppsDrawerOpen(true)
        GestureCoachStep.ALPHABET_SCRUB -> {
            setAllAppsDrawerOpen(true)
            onOnboardingSignal(OnboardingSignal.ALPHABET_SCRUBBED)
        }
        GestureCoachStep.CUSTOMIZE -> setShowCustomizeBottomSheet(true)
        null -> Unit
    }
}
