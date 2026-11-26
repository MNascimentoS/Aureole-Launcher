package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.LayoutPreset
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
    SKIPPED,

    /** An extra waiting for the home section to be done or skipped (onboarding-redesenho RF-A4). */
    LOCKED
}

/** The row status of [task]: done wins over skipped, and only a pending task can lead. */
fun OnboardingUiState.statusOf(task: OnboardingTask, next: OnboardingTask?): OnboardingTaskStatus = when {
    task in completedTasks -> OnboardingTaskStatus.DONE
    task in skippedTasks -> OnboardingTaskStatus.SKIPPED
    isTaskLocked(task) -> OnboardingTaskStatus.LOCKED
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
    /** The item the finished task left on the home, outlined while the home peeks. Never persisted. */
    val peekItemId: String? = null,
    /** The pending task's own screen saved what the task asks for (the container editor's Save). Never persisted. */
    val isPendingTaskSaved: Boolean = false,
    /** The welcome shows the ready-made layouts ("Use a ready-made layout", RF-F1). Never persisted. */
    val isChoosingPreset: Boolean = false,
    /** The presets worked out for this device, empty while the apps are read. Never persisted. */
    val presetPlans: List<LayoutPresetPlan> = emptyList(),
    /** The card selected on the presets screen. Never persisted. */
    val selectedPreset: LayoutPreset = LayoutPreset.MINIMAL,
    /** The preset the welcome applied; the checklist names it on the steps it did (RF-F4). */
    val appliedPreset: LayoutPreset? = null
)

val OnboardingUiState.isBlocking: Boolean
    get() = stage == OnboardingStage.WELCOME || stage == OnboardingStage.DEFAULT_HOME

/**
 * The home's own items (clock, widget, favorites, group) ignore touches until the onboarding is over, so a swipe,
 * a scroll or a hold taught by the coach is never eaten by a component already on the home. The layout step
 * edits the grid, so it is up to the caller to leave edit mode out.
 */
val OnboardingUiState.locksHomeItems: Boolean
    get() = stage != OnboardingStage.COMPLETED

val OnboardingUiState.blocksHome: Boolean
    get() = isBlocking || activePicker != null ||
        (stage == OnboardingStage.CHECKLIST && isChecklistExpanded)

/** Done or skipped: what the progress, the next task and the celebration count. */
val OnboardingUiState.resolvedTasks: Set<OnboardingTask>
    get() = completedTasks + skippedTasks

val OnboardingUiState.taskProgress: Float
    get() = resolvedTasks.size.toFloat() / OnboardingTask.entries.size

/**
 * The scrubber is off when disabled and does not exist in landscape, so its step is left out. Order: all apps,
 * alphabet, touch and hold.
 */
fun visibleGestureSteps(
    isAlphabetScrubberDisabled: Boolean,
    isLandscape: Boolean = false
): List<GestureCoachStep> = GestureCoachStep.entries.filter {
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
    isChecklistCollapsed = !isChecklistExpanded,
    appliedPreset = appliedPreset
)

fun OnboardingProgress.toUiState(isDefaultLauncher: Boolean): OnboardingUiState = OnboardingUiState(
    stage = stage,
    completedSteps = completedSteps,
    completedTasks = completedTasks,
    skippedTasks = skippedTasks,
    isDefaultLauncher = isDefaultLauncher,
    isChecklistExpanded = !isChecklistCollapsed,
    appliedPreset = appliedPreset
)

/** The "Your home" section of the checklist, in list order (RF-A1). */
val onboardingHomeTasks: List<OnboardingTask> = OnboardingTask.entries.filterNot { it.isExtra }

/** The "Extras" section, locked until the home section is resolved (RF-A4, D-09). */
val onboardingExtraTasks: List<OnboardingTask> = OnboardingTask.entries.filter { it.isExtra }

/** Every step of "Your home" is done or skipped: the extras unlock. */
val OnboardingUiState.isHomeBuilt: Boolean
    get() = resolvedTasks.containsAll(onboardingHomeTasks)

/** True while the extra [task] waits for the home section to be done or skipped (RF-A4). */
fun OnboardingUiState.isTaskLocked(task: OnboardingTask): Boolean =
    task.isExtra && task !in resolvedTasks && !isHomeBuilt

/**
 * A done step taps back into its screen to redo it (RF-A3); the home app has nothing to redo once it is set.
 * Skipped and pending steps always react, locked ones never.
 */
fun OnboardingTaskStatus.isClickable(task: OnboardingTask): Boolean = when (this) {
    OnboardingTaskStatus.LOCKED -> false
    OnboardingTaskStatus.DONE -> task != OnboardingTask.SET_DEFAULT_HOME
    else -> true
}

/** The preset whose name a done step shows below its title ("Layout <name> applied"), or null (RF-F4). */
fun OnboardingUiState.presetAppliedTo(task: OnboardingTask): LayoutPreset? =
    appliedPreset?.takeIf { task in presetLayoutTasks && task in completedTasks }

/**
 * The welcome applied [plan] (RF-F4): the tour goes on to the gestures (through the home app step when needed, as
 * "Start from scratch"), with the steps the preset did already done.
 */
fun OnboardingUiState.applyingPreset(plan: LayoutPresetPlan): OnboardingUiState = copy(
    stage = if (isDefaultLauncher) OnboardingStage.GESTURES else OnboardingStage.DEFAULT_HOME,
    completedTasks = completedTasks + plan.completedTasks(),
    appliedPreset = plan.preset,
    isChoosingPreset = false
)

/** "Undo" on the applied preset (RF-F5): back to the welcome as it was before, with its buttons, not the list. */
fun OnboardingUiState.undoingPreset(before: OnboardingUiState): OnboardingUiState =
    before.copy(isChoosingPreset = false, presetPlans = presetPlans, isDefaultLauncher = isDefaultLauncher)
