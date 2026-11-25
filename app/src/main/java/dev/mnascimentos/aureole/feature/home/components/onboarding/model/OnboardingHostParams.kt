package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.composable.undo.model.UndoSnackbarState

/** The single onboarding surface shown above the home at a time; pill and pickers sit outside it. */
enum class OnboardingSurface {
    NONE,
    WELCOME,

    /** The ready-made layouts, opened from the welcome (RF-F1). */
    PRESETS,
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
    val isHomeOverlayActive: Boolean = false,
    /** A grid task waits in edit mode with no editor on top: show how to adjust and save. */
    val showEditHint: Boolean = false,
    /** Rows the grid items cover, to keep the edit hint off the item being adjusted. */
    val editHintGrid: EditHintGrid = EditHintGrid(),
    /** The home is empty in both orientations: the welcome offers the ready-made layouts (D-22). */
    val canUsePreset: Boolean = false,
    /** The undo offer to show above the onboarding ("Layout <name> applied"), which covers the home's own. */
    val undoSnackbar: UndoSnackbarState? = null,
    val isLeftHanded: Boolean = false
)
