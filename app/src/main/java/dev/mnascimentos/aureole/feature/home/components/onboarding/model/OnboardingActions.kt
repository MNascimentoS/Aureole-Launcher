package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.core.data.model.LayoutPreset
import dev.mnascimentos.aureole.core.data.model.OnboardingTask

data class OnboardingActions(
    val onStart: () -> Unit = {},
    val onSkipIntro: () -> Unit = {},
    /** "Use a ready-made layout" on the welcome (RF-E1). */
    val onUsePreset: () -> Unit = {},
    val onClosePresets: () -> Unit = {},
    val onSelectPreset: (LayoutPreset) -> Unit = {},
    val onApplyPreset: (LayoutPreset) -> Unit = {},
    /** "Undo" on "Layout <name> applied" (RF-F5), and the offer running out. */
    val onUndo: () -> Unit = {},
    val onUndoExpired: (Long) -> Unit = {},
    val onRequestDefaultHome: () -> Unit = {},
    val onOpenSystemHomeSettings: () -> Unit = {},
    val onPostponeDefaultHome: () -> Unit = {},
    val onSkipGestures: () -> Unit = {},
    val onShowMeGesture: () -> Unit = {},
    val onAlphabetScrubbed: () -> Unit = {},
    /** The swipe down success card stayed on the focused home long enough to be read. */
    val onTaskClick: (OnboardingTask) -> Unit = {},
    val onSkipTask: (OnboardingTask) -> Unit = {},
    /** The Visual sheet closed: save what was tapped (null = untouched) and complete the step (RF-C1). */
    val onCloseVisual: (VisualChoice) -> Unit = {},
    val onSetChecklistExpanded: (Boolean) -> Unit = {},
    val onDismissChecklist: () -> Unit = {},
    val onFinishCelebration: () -> Unit = {},
    val onSaveGridEdit: () -> Unit = {},
    /** Opens the Aureole settings from the logo of the palette and font sheets; null hides the logo. */
    val onOpenSettings: (() -> Unit)? = null
)
