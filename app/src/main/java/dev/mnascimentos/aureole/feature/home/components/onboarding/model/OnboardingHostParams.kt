package dev.mnascimentos.aureole.feature.home.components.onboarding.model

/** The single onboarding surface shown above the home at a time; pill and pickers sit outside it. */
enum class OnboardingSurface {
    NONE,
    WELCOME,
    DEFAULT_HOME,
    GESTURE_COACH,
    CHECKLIST,
    CELEBRATION
}

/** Everything the onboarding host reads from the home, gathered once per frame by the scaffold. */
data class OnboardingHostParams(
    val onboarding: OnboardingUiState,
    val glass: OnboardingGlass = OnboardingGlass(),
    val coachConfig: GestureCoachConfig = GestureCoachConfig(),
    val selection: AppearanceSelection = AppearanceSelection(),
    /** A home sheet, dialog or drawer is open: the pill must not cover it. */
    val isHomeOverlayActive: Boolean = false
)
