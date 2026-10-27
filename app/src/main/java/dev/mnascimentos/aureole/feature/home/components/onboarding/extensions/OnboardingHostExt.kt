package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearanceSelection
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.GestureCoachConfig
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingHostParams
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSurface
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
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
            isTouchExplorationEnabled = isTouchExplorationEnabled
        ),
        selection = AppearanceSelection(paletteName = selectedThemeName, fontName = selectedFontName),
        isHomeOverlayActive = isHomeOverlayActive
    )
