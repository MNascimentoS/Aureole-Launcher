package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearanceSelection
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.ChecklistMorph
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.GestureCoachConfig
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingHostParams
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSurface
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.currentGestureStep
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.isHomeOverlayActive

/** The expanded list steps aside while a picker is open, so the home re-themes in full view. */
val OnboardingUiState.visibleSurface: OnboardingSurface
    get() = when (stage) {
        OnboardingStage.WELCOME -> OnboardingSurface.WELCOME
        OnboardingStage.DEFAULT_HOME -> OnboardingSurface.DEFAULT_HOME
        OnboardingStage.GESTURES -> OnboardingSurface.GESTURE_COACH
        OnboardingStage.CHECKLIST -> if (isChecklistExpanded && activePicker == null) {
            OnboardingSurface.CHECKLIST
        } else {
            OnboardingSurface.NONE
        }
        OnboardingStage.COMPLETED -> if (isCelebrating) OnboardingSurface.CELEBRATION else OnboardingSurface.NONE
    }

/** The surface of the host's crossfade; the checklist is drawn by its own layer, which turns it into the pill. */
val OnboardingUiState.hostSurface: OnboardingSurface
    get() = visibleSurface.takeUnless { it == OnboardingSurface.CHECKLIST } ?: OnboardingSurface.NONE

/** The list is minimized to the pill: nothing else of the tour covers the home. */
val OnboardingUiState.isChecklistMinimized: Boolean
    get() = stage == OnboardingStage.CHECKLIST && !isChecklistExpanded && activePicker == null && !isHomePeeking

/** The home shows alone after a task: the bubble sits in the pill's place until the list comes back. */
val OnboardingUiState.isPeekBubbleShown: Boolean
    get() = stage == OnboardingStage.CHECKLIST && isHomePeeking && activePicker == null && pendingTask == null

/**
 * What the checklist layer shows; the pill and the peek bubble hide while [isOtherOverlayActive], so they never
 * cover an editor.
 */
fun OnboardingUiState.checklistMorph(isOtherOverlayActive: Boolean): ChecklistMorph = when {
    visibleSurface == OnboardingSurface.CHECKLIST -> ChecklistMorph.EXPANDED
    isOtherOverlayActive -> ChecklistMorph.HIDDEN
    isChecklistMinimized -> ChecklistMorph.MINIMIZED
    isPeekBubbleShown -> ChecklistMorph.PEEK
    else -> ChecklistMorph.HIDDEN
}

/** Nothing to draw: the usual case once the tour is over, kept free of any onboarding layer. */
val OnboardingUiState.isIdle: Boolean
    get() = stage == OnboardingStage.COMPLETED && !isCelebrating

/** How many gestures and tasks are done; a rise means a success worth a confirm haptic. */
val OnboardingUiState.successCount: Int
    get() = completedSteps.size + completedTasks.size

fun MainUiState.toOnboardingHostParams(hazeState: HazeState?, isTouchExplorationEnabled: Boolean) =
    OnboardingHostParams(
        onboarding = onboarding,
        glass = OnboardingGlass(hazeState = hazeState, isHazeEnabled = isHazeEnabled, hazeOpacity = hazeOpacity),
        coachConfig = GestureCoachConfig(
            isAlphabetScrubberDisabled = isAlphabetScrubberDisabled,
            isLeftHandedMode = isLeftHandedMode,
            isTouchExplorationEnabled = isTouchExplorationEnabled,
            isLandscape = isLandscape,
            isAlphabetLoading = alphabet.isEmpty()
        ),
        selection = AppearanceSelection(paletteName = selectedThemeName, fontName = selectedFontName),
        isHomeOverlayActive = isHomeOverlayActive,
        showEditHint = isGridEditMode && onboarding.pendingTask?.isJudgedOnSave == true && !isTaskEditorOpen
    )

/** The faint alphabet shows only while the coach teaches the scrub step. */
val MainUiState.isScrubberHintVisible: Boolean
    get() = onboarding.stage == OnboardingStage.GESTURES &&
        onboarding.succeededStep == null &&
        onboarding.currentGestureStep(isAlphabetScrubberDisabled, isLandscape) == GestureCoachStep.ALPHABET_SCRUB
