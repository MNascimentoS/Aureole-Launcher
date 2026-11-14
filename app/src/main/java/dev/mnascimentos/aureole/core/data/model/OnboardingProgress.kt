package dev.mnascimentos.aureole.core.data.model

enum class OnboardingStage {
    WELCOME,
    DEFAULT_HOME,
    GESTURES,
    CHECKLIST,
    COMPLETED
}

enum class GestureCoachStep {
    OPEN_APPS,
    ALPHABET_SCRUB,
    CUSTOMIZE
}

/** [isSkippable] tasks (the home items and the layout) get a "Skip" button on the checklist. */
enum class OnboardingTask(val isSkippable: Boolean = false) {
    SET_DEFAULT_HOME,
    CHOOSE_PALETTE,
    CHOOSE_FONT,
    ADD_CLOCK(isSkippable = true),
    ADD_WIDGET(isSkippable = true),
    ADD_FAVORITE_APPS(isSkippable = true),
    FILL_CONTAINER(isSkippable = true),
    ADJUST_LAYOUT(isSkippable = true),
    EXPLORE_SETTINGS
}

data class OnboardingProgress(
    val stage: OnboardingStage,
    val completedSteps: Set<GestureCoachStep> = emptySet(),
    val completedTasks: Set<OnboardingTask> = emptySet(),
    /** Tasks the user skipped: they stop blocking the checklist but are not done. */
    val skippedTasks: Set<OnboardingTask> = emptySet(),
    val isChecklistCollapsed: Boolean = false
)

/** Done or skipped: the tasks that no longer block the checklist or its celebration. */
val OnboardingProgress.resolvedTasks: Set<OnboardingTask>
    get() = completedTasks + skippedTasks
