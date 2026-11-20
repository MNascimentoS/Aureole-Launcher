package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.core.data.model.OnboardingTask

data class OnboardingActions(
    val onStart: () -> Unit = {},
    val onSkipIntro: () -> Unit = {},
    val onRequestDefaultHome: () -> Unit = {},
    val onOpenSystemHomeSettings: () -> Unit = {},
    val onPostponeDefaultHome: () -> Unit = {},
    val onSkipGestures: () -> Unit = {},
    val onShowMeGesture: () -> Unit = {},
    val onAlphabetScrubbed: () -> Unit = {},
    /** The swipe down success card stayed on the focused home long enough to be read. */
    val onNotificationsStepSeen: () -> Unit = {},
    val onTaskClick: (OnboardingTask) -> Unit = {},
    val onSkipTask: (OnboardingTask) -> Unit = {},
    val onSelectPalette: (String) -> Unit = {},
    val onSelectFont: (String) -> Unit = {},
    val onConfirmAppearancePicker: () -> Unit = {},
    /** The picker closed without applying; the flag tells whether the user had tapped an option. */
    val onDismissAppearancePicker: (hadPreview: Boolean) -> Unit = {},
    val onSetChecklistExpanded: (Boolean) -> Unit = {},
    val onDismissChecklist: () -> Unit = {},
    val onFinishCelebration: () -> Unit = {},
    val onSaveGridEdit: () -> Unit = {},
    /** Opens the Aureole settings from the logo of the palette and font sheets; null hides the logo. */
    val onOpenSettings: (() -> Unit)? = null
)
