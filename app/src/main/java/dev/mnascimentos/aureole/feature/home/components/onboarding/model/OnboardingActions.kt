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
    val onTaskClick: (OnboardingTask) -> Unit = {},
    val onSelectPalette: (String) -> Unit = {},
    val onSelectFont: (String) -> Unit = {},
    val onConfirmAppearancePicker: () -> Unit = {},
    val onDismissAppearancePicker: () -> Unit = {},
    val onSetChecklistExpanded: (Boolean) -> Unit = {},
    val onDismissChecklist: () -> Unit = {},
    val onFinishCelebration: () -> Unit = {},
    val onSaveGridEdit: () -> Unit = {}
)
