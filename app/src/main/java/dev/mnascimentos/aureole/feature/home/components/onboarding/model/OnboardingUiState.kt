package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingProgress
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask

enum class AppearancePicker {
    PALETTE,
    FONT
}

/** How a checklist row looks and reacts: [NEXT] is the pending task that leads, with the breathing outline. */
enum class OnboardingTaskStatus {
    PENDING,
    NEXT,
    DONE,
    SKIPPED
}

/** The row status of [task]: done wins over skipped, and only a pending task can lead. */
fun OnboardingUiState.statusOf(task: OnboardingTask, next: OnboardingTask?): OnboardingTaskStatus = when {
    task in completedTasks -> OnboardingTaskStatus.DONE
    task in skippedTasks -> OnboardingTaskStatus.SKIPPED
    task == next -> OnboardingTaskStatus.NEXT
    else -> OnboardingTaskStatus.PENDING
}

data class OnboardingUiState(
    val stage: OnboardingStage = OnboardingStage.COMPLETED,
    val completedSteps: Set<GestureCoachStep> = emptySet(),
    val succeededStep: GestureCoachStep? = null,
    val completedTasks: Set<OnboardingTask> = emptySet(),
    val skippedTasks: Set<OnboardingTask> = emptySet(),
    val isDefaultLauncher: Boolean = false,
    val defaultHomeAttempted: Boolean = false,
    val isChecklistExpanded: Boolean = true,
    val activePicker: AppearancePicker? = null,
    val isCelebrating: Boolean = false,
    /** Grid task waiting for the user to save its item; never persisted. */
    val pendingTask: OnboardingTask? = null,
    /** Item the pending task placed on the home, removed again if the task fails. */
    val createdItemId: String? = null,
    /** The home is on screen; the celebration never plays behind another app or Settings. */
    val isHomeInForeground: Boolean = true,
    /** Every task got done while the home was hidden: celebrate on the next resume. Never persisted. */
    val isCelebrationPending: Boolean = false,
    /** The task screen just closed: the home shows alone for a moment before the list comes back. Never persisted. */
    val isHomePeeking: Boolean = false,
    /** The pending task's own screen saved what the task asks for (the container editor's Save). Never persisted. */
    val isPendingTaskSaved: Boolean = false
)

val OnboardingUiState.isBlocking: Boolean
    get() = stage == OnboardingStage.WELCOME || stage == OnboardingStage.DEFAULT_HOME

val OnboardingUiState.blocksHome: Boolean
    get() = isBlocking || activePicker != null ||
        (stage == OnboardingStage.CHECKLIST && isChecklistExpanded)

/** Done or skipped: what the progress, the next task and the celebration count. */
val OnboardingUiState.resolvedTasks: Set<OnboardingTask>
    get() = completedTasks + skippedTasks

val OnboardingUiState.taskProgress: Float
    get() = resolvedTasks.size.toFloat() / OnboardingTask.entries.size

/** The scrubber is off when disabled and does not exist in landscape, so its step is left out. */
fun visibleGestureSteps(isAlphabetScrubberDisabled: Boolean, isLandscape: Boolean = false): List<GestureCoachStep> =
    GestureCoachStep.entries.filter {
        it != GestureCoachStep.ALPHABET_SCRUB || (!isAlphabetScrubberDisabled && !isLandscape)
    }

/** The step the coach teaches now: the first visible one not done yet. */
fun OnboardingUiState.currentGestureStep(
    isAlphabetScrubberDisabled: Boolean,
    isLandscape: Boolean = false
): GestureCoachStep? = visibleGestureSteps(
    isAlphabetScrubberDisabled,
    isLandscape
).firstOrNull { it !in completedSteps }

fun OnboardingUiState.toProgress(): OnboardingProgress = OnboardingProgress(
    stage = stage,
    completedSteps = completedSteps,
    completedTasks = completedTasks,
    skippedTasks = skippedTasks,
    isChecklistCollapsed = !isChecklistExpanded
)

fun OnboardingProgress.toUiState(isDefaultLauncher: Boolean): OnboardingUiState = OnboardingUiState(
    stage = stage,
    completedSteps = completedSteps,
    completedTasks = completedTasks,
    skippedTasks = skippedTasks,
    isDefaultLauncher = isDefaultLauncher,
    isChecklistExpanded = !isChecklistCollapsed
)
