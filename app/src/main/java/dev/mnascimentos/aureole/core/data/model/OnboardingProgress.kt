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

enum class OnboardingTask {
    SET_DEFAULT_HOME,
    CHOOSE_PALETTE,
    CHOOSE_FONT,
    ADD_FAVORITE_APPS,
    FILL_CONTAINER,
    ADD_WIDGET,
    EXPLORE_SETTINGS
}

data class OnboardingProgress(
    val stage: OnboardingStage,
    val completedSteps: Set<GestureCoachStep> = emptySet(),
    val completedTasks: Set<OnboardingTask> = emptySet(),
    val isChecklistCollapsed: Boolean = false
)
