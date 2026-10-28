package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSignal
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.visibleGestureSteps
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * Pure onboarding transitions. [reduce] runs on every home state update, so it returns `next`
 * untouched unless the onboarding is teaching gestures or showing the checklist.
 */
internal object OnboardingReducer {

    fun reduce(previous: MainUiState, next: MainUiState): MainUiState = when (next.onboarding.stage) {
        OnboardingStage.GESTURES -> reduceGestures(previous, next)
        OnboardingStage.CHECKLIST -> reduceChecklist(previous, next)
        else -> next
    }

    fun reduceGestures(previous: MainUiState, next: MainUiState): MainUiState {
        val onboarding = next.onboarding
        val openedStep = openedSurfaceStep(previous, next)
        val closedSurface = (previous.isAllAppsDrawerOpen && !next.isAllAppsDrawerOpen) ||
            (previous.showCustomizeBottomSheet && !next.showCustomizeBottomSheet)
        val updated = when {
            openedStep != null -> onboarding.copy(
                completedSteps = onboarding.completedSteps + openedStep,
                succeededStep = openedStep
            )
            closedSurface && onboarding.succeededStep != null -> onboarding.copy(succeededStep = null)
            else -> onboarding
        }
        val visibleSteps = visibleGestureSteps(next.isAlphabetScrubberDisabled, next.isLandscape)
        val isFinished = updated.succeededStep == null && updated.completedSteps.containsAll(visibleSteps)
        // Finishing can also come from the step list shrinking, as when the screen turns to landscape.
        return when {
            !isFinished && updated == onboarding -> next
            !isFinished -> next.copy(onboarding = updated)
            else -> reduceChecklist(
                previous,
                next.copy(onboarding = updated.copy(stage = OnboardingStage.CHECKLIST, isChecklistExpanded = true))
            )
        }
    }

    fun reduceChecklist(previous: MainUiState, next: MainUiState): MainUiState {
        val onboarding = next.onboarding
        val tasks = onboarding.completedTasks + deriveCompletedTasks(next)
        if (tasks.containsAll(OnboardingTask.entries)) {
            return next.copy(
                onboarding = onboarding.copy(
                    stage = OnboardingStage.COMPLETED,
                    completedTasks = tasks,
                    activePicker = null,
                    isCelebrating = onboarding.isHomeInForeground,
                    isCelebrationPending = !onboarding.isHomeInForeground
                )
            )
        }
        // Tasks restored from disk are not news: only reopen for completions seen on the checklist.
        val hasNewTask = previous.onboarding.stage == OnboardingStage.CHECKLIST &&
            !previous.onboarding.completedTasks.containsAll(tasks)
        val updated = onboarding.copy(
            completedTasks = tasks,
            isChecklistExpanded = onboarding.isChecklistExpanded || hasNewTask
        )
        return if (updated == onboarding) next else next.copy(onboarding = updated)
    }

    /**
     * Only the home app is read from the state. Grid tasks complete when their item is saved
     * (see `confirmPendingTask`), so marking a favorite with the picker still open does not count.
     */
    fun deriveCompletedTasks(state: MainUiState): Set<OnboardingTask> =
        if (state.onboarding.isDefaultLauncher) setOf(OnboardingTask.SET_DEFAULT_HOME) else emptySet()

    fun applySignal(state: MainUiState, signal: OnboardingSignal): MainUiState {
        val onboarding = state.onboarding
        val updated = when (signal) {
            OnboardingSignal.ALPHABET_SCRUBBED -> onboarding.onAlphabetScrubbed()
            OnboardingSignal.PICKER_CONFIRMED -> onboarding.onPickerConfirmed()
            OnboardingSignal.SETTINGS_OPENED -> onboarding.onSettingsOpened()
            OnboardingSignal.DEFAULT_HOME_RESULT -> onboarding.onDefaultHomeResult()
        }
        return if (updated == onboarding) state else state.copy(onboarding = updated)
    }

    private fun openedSurfaceStep(previous: MainUiState, next: MainUiState): GestureCoachStep? {
        val step = when {
            !previous.isAllAppsDrawerOpen && next.isAllAppsDrawerOpen -> GestureCoachStep.OPEN_APPS
            !previous.showCustomizeBottomSheet && next.showCustomizeBottomSheet -> GestureCoachStep.CUSTOMIZE
            else -> null
        }
        return step?.takeUnless { it in next.onboarding.completedSteps }
    }
}
