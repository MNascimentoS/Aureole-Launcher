package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState

internal fun OnboardingUiState.onAlphabetScrubbed(): OnboardingUiState {
    if (stage != OnboardingStage.GESTURES || GestureCoachStep.ALPHABET_SCRUB in completedSteps) return this
    return copy(
        completedSteps = completedSteps + GestureCoachStep.ALPHABET_SCRUB,
        succeededStep = GestureCoachStep.ALPHABET_SCRUB
    )
}

internal fun OnboardingUiState.onPickerConfirmed(): OnboardingUiState {
    if (stage != OnboardingStage.CHECKLIST) return this
    val task = when (activePicker) {
        AppearancePicker.PALETTE -> OnboardingTask.CHOOSE_PALETTE
        AppearancePicker.FONT -> OnboardingTask.CHOOSE_FONT
        null -> return this
    }
    return copy(completedTasks = completedTasks + task, activePicker = null)
}

/** Opening Settings only starts the task: it completes when the user comes back to the home. */
internal fun OnboardingUiState.onSettingsOpened(): OnboardingUiState {
    if (stage != OnboardingStage.CHECKLIST || OnboardingTask.EXPLORE_SETTINGS in completedTasks) return this
    return copy(pendingTask = OnboardingTask.EXPLORE_SETTINGS)
}

internal fun OnboardingUiState.onDefaultHomeResult(): OnboardingUiState {
    if (stage != OnboardingStage.DEFAULT_HOME && stage != OnboardingStage.CHECKLIST) return this
    return copy(defaultHomeAttempted = true)
}

/** The grid task ended without a save: clear it and bring the list back unchecked. */
internal fun OnboardingUiState.abandonPendingTask(): OnboardingUiState {
    if (pendingTask == null) return this
    return copy(pendingTask = null, createdItemId = null, isChecklistExpanded = true)
}
