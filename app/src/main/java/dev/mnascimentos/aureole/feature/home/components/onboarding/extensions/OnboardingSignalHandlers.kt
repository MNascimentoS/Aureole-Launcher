package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState

internal fun OnboardingUiState.onAlphabetScrubbed(): OnboardingUiState {
    if (stage != OnboardingStage.GESTURES || GestureCoachStep.ALPHABET_SCRUB in completedSteps) return this
    return copy(
        completedSteps = completedSteps + GestureCoachStep.ALPHABET_SCRUB,
        succeededStep = GestureCoachStep.ALPHABET_SCRUB
    )
}

/** The Visual sheet (Palette | Font) closed: the step is done and the list comes back (RF-C1). */
internal fun OnboardingUiState.onPickerConfirmed(): OnboardingUiState {
    if (stage != OnboardingStage.CHECKLIST || activePicker == null) return this
    val task = OnboardingTask.CHOOSE_VISUAL
    return copy(
        completedTasks = completedTasks + task,
        skippedTasks = skippedTasks - task,
        activePicker = null,
        isChecklistExpanded = true
    )
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

/**
 * Back on the home from another screen: a Settings visit counts as done (RF-A1 extra), and the list comes back. The
 * home is not taken as shown yet ([showingHome]), so a celebration this completes waits for it.
 */
internal fun OnboardingUiState.returningToHome(): OnboardingUiState {
    if (pendingTask != OnboardingTask.EXPLORE_SETTINGS) return this
    return copy(
        completedTasks = completedTasks + OnboardingTask.EXPLORE_SETTINGS,
        pendingTask = null,
        isChecklistExpanded = true
    )
}

/** The home is on screen again, the transition over: a celebration that waited for it plays now (RF-D5). */
internal fun OnboardingUiState.showingHome(): OnboardingUiState {
    val shown = copy(isHomeInForeground = true)
    return if (shown.isCelebrationPending) shown.copy(isCelebrating = true, isCelebrationPending = false) else shown
}

/** The container editor saved: the container task counts only when the group has [hasItems] (RF-C6). */
internal fun OnboardingUiState.savingContainerTask(hasItems: Boolean): OnboardingUiState =
    if (pendingTask == OnboardingTask.FILL_CONTAINER && hasItems) copy(isPendingTaskSaved = true) else this

/** The grid task ended without a save: clear it and bring the list back unchecked. */
internal fun OnboardingUiState.abandonPendingTask(): OnboardingUiState {
    if (pendingTask == null) return this
    return copy(pendingTask = null, createdItemId = null, isPendingTaskSaved = false, isChecklistExpanded = true)
}

/**
 * The task's screen closed: hides the list and the pill so the home shows alone until [endingHomePeek], with the
 * bubble in the pill's place and [itemId] (the item the task left, if it succeeded) outlined.
 */
internal fun OnboardingUiState.peekingHome(itemId: String? = null): OnboardingUiState =
    copy(isChecklistExpanded = false, isHomePeeking = true, peekItemId = itemId)

/** The peek is over: the list comes back, unless the tour moved on or the user already opened something else. */
internal fun OnboardingUiState.endingHomePeek(): OnboardingUiState {
    if (!isHomePeeking) return this
    val canReopen = stage == OnboardingStage.CHECKLIST && pendingTask == null && activePicker == null
    return copy(isHomePeeking = false, peekItemId = null, isChecklistExpanded = isChecklistExpanded || canReopen)
}
