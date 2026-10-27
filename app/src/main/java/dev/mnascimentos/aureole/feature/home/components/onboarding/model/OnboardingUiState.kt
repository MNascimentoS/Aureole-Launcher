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
    val isCelebrating: Boolean = false
)

val OnboardingUiState.isBlocking: Boolean
    get() = stage == OnboardingStage.WELCOME || stage == OnboardingStage.DEFAULT_HOME

val OnboardingUiState.blocksHome: Boolean
    get() = isBlocking || activePicker != null ||
        (stage == OnboardingStage.CHECKLIST && isChecklistExpanded)

val OnboardingUiState.taskProgress: Float
    get() = completedTasks.size.toFloat() / OnboardingTask.entries.size

fun visibleGestureSteps(isAlphabetScrubberDisabled: Boolean): List<GestureCoachStep> =
    GestureCoachStep.entries.filter { it != GestureCoachStep.ALPHABET_SCRUB || !isAlphabetScrubberDisabled }

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
