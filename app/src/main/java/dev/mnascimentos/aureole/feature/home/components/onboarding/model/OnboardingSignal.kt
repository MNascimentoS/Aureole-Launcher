package dev.mnascimentos.aureole.feature.home.components.onboarding.model

/** Onboarding progress that cannot be derived from `MainUiState` and is reported explicitly. */
enum class OnboardingSignal {
    ALPHABET_SCRUBBED,
    PICKER_CONFIRMED,
    SETTINGS_OPENED,
    DEFAULT_HOME_RESULT
}
