package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.OnboardingProgress
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSignal
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.toProgress
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.toUiState

/**
 * Loads the stored progress on every resume. The in-memory state wins unless the stored one changed
 * outside the home (for example, Settings restarting the tour), so transient flags survive a resume.
 */
fun HomeViewModel.syncOnboarding() {
    val isDefaultLauncher = settingsRepository.checkDefaultLauncher()
    val stored = settingsRepository.onboarding.read() ?: resolveFirstProgress()
    updateUiState { state ->
        val current = state.onboarding
        val base = if (current.toProgress() == stored) current else stored.toUiState(isDefaultLauncher)
        state.copy(onboarding = base.withDefaultLauncher(isDefaultLauncher))
    }
}

/** Called when the user comes back from the home app role dialog or the system settings. */
fun HomeViewModel.refreshDefaultLauncherStatus() {
    val isDefaultLauncher = settingsRepository.checkDefaultLauncher()
    updateUiState { state ->
        val signalled = OnboardingReducer.applySignal(state, OnboardingSignal.DEFAULT_HOME_RESULT)
        signalled.copy(onboarding = signalled.onboarding.withDefaultLauncher(isDefaultLauncher))
    }
}

fun HomeViewModel.onOnboardingSignal(signal: OnboardingSignal) {
    updateUiState { OnboardingReducer.applySignal(it, signal) }
}

/** Returns true when the onboarding consumed the back press. */
fun HomeViewModel.onOnboardingBack(): Boolean {
    val handled = uiState.value.onboarding.backTransition() ?: return false
    updateUiState { it.copy(onboarding = handled) }
    return true
}

internal fun HomeViewModel.updateOnboarding(
    stage: OnboardingStage,
    transform: OnboardingUiState.() -> OnboardingUiState
) {
    updateUiState { state ->
        if (state.onboarding.stage != stage) state else state.copy(onboarding = state.onboarding.transform())
    }
}

private fun HomeViewModel.resolveFirstProgress(): OnboardingProgress {
    // Existing users already built their home; only fresh installs see the tour.
    val stage = if (settingsRepository.lastSeenVersionCode == 0) OnboardingStage.WELCOME else OnboardingStage.COMPLETED
    return OnboardingProgress(stage = stage).also { settingsRepository.onboarding.write(it) }
}

private fun OnboardingUiState.withDefaultLauncher(isDefaultLauncher: Boolean): OnboardingUiState {
    val refreshed = copy(isDefaultLauncher = isDefaultLauncher)
    // After an attempt the step stays to show the success message; otherwise it is skipped silently.
    val skipsDefaultHome = stage == OnboardingStage.DEFAULT_HOME && isDefaultLauncher && !defaultHomeAttempted
    return if (skipsDefaultHome) refreshed.copy(stage = OnboardingStage.GESTURES) else refreshed
}

private fun OnboardingUiState.backTransition(): OnboardingUiState? = when {
    activePicker != null -> copy(activePicker = null)
    stage == OnboardingStage.WELCOME -> this
    stage == OnboardingStage.DEFAULT_HOME -> copy(stage = OnboardingStage.WELCOME)
    stage == OnboardingStage.CHECKLIST && isChecklistExpanded -> copy(isChecklistExpanded = false)
    else -> null
}
