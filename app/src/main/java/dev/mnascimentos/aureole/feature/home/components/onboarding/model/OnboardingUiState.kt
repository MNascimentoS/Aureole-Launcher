package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingProgress
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.data.model.OnboardingTask

enum class AppearancePicker {
    PALETTE,
    FONT
}

data class OnboardingUiState(
    val stage: OnboardingStage = OnboardingStage.COMPLETED,
    val completedSteps: Set<GestureCoachStep> = emptySet(),
    val succeededStep: GestureCoachStep? = null,
    val completedTasks: Set<OnboardingTask> = emptySet(),
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
    val isCelebrationPending: Boolean = false
)

val OnboardingUiState.isBlocking: Boolean
    get() = stage == OnboardingStage.WELCOME || stage == OnboardingStage.DEFAULT_HOME

val OnboardingUiState.blocksHome: Boolean
    get() = isBlocking || activePicker != null ||
        (stage == OnboardingStage.CHECKLIST && isChecklistExpanded)

val OnboardingUiState.taskProgress: Float
    get() = completedTasks.size.toFloat() / OnboardingTask.entries.size

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
    isChecklistCollapsed = !isChecklistExpanded
)

fun OnboardingProgress.toUiState(isDefaultLauncher: Boolean): OnboardingUiState = OnboardingUiState(
    stage = stage,
    completedSteps = completedSteps,
    completedTasks = completedTasks,
    isDefaultLauncher = isDefaultLauncher,
    isChecklistExpanded = !isChecklistCollapsed
)
