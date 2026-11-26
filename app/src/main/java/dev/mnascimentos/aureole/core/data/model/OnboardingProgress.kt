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

/**
 * The 8 checklist steps, in list order (onboarding-redesenho RF-A1): the "Your home" section, then the [isExtra]
 * ones, which stay locked until the home section is done or skipped. Every step can be skipped while it is the
 * current one. The names are persisted: never rename (the old `CHOOSE_PALETTE`/`CHOOSE_FONT` migrate to
 * [CHOOSE_VISUAL], see [migrateOnboardingTasks]).
 */
enum class OnboardingTask(val isExtra: Boolean = false) {
    SET_DEFAULT_HOME,
    CHOOSE_VISUAL,
    ADD_CLOCK,
    ADD_WIDGET,
    ADD_FAVORITE_APPS,
    FILL_CONTAINER,
    ADJUST_LAYOUT(isExtra = true),
    EXPLORE_SETTINGS(isExtra = true)
}

data class OnboardingProgress(
    val stage: OnboardingStage,
    val completedSteps: Set<GestureCoachStep> = emptySet(),
    val completedTasks: Set<OnboardingTask> = emptySet(),
    /** Tasks the user skipped: they stop blocking the checklist but are not done. */
    val skippedTasks: Set<OnboardingTask> = emptySet(),
    val isChecklistCollapsed: Boolean = false,
    /** The ready-made layout the welcome applied, which the checklist names on the steps it did; null for none. */
    val appliedPreset: LayoutPreset? = null
)

/** Done or skipped: the tasks that no longer block the checklist or its celebration. */
val OnboardingProgress.resolvedTasks: Set<OnboardingTask>
    get() = completedTasks + skippedTasks
