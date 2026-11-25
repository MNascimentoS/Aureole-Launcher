package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import dev.mnascimentos.aureole.composable.undo.AureoleUndoSnackbar
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.hostSurface
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.isIdle
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.successCount
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.surfaceExitMillis
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EditHintConfig
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingHostParams
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSurface
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState

/**
 * Top layer of the home: routes the onboarding stage to its surface, plus the checklist (which minimizes into
 * its pill) and the appearance pickers. Draws nothing once the tour is over.
 */
@Composable
fun OnboardingHostComponent(
    params: OnboardingHostParams,
    actions: OnboardingActions,
    modifier: Modifier = Modifier
) {
    val onboarding = params.onboarding
    if (onboarding.isIdle) return
    OnboardingSuccessHaptics(successCount = onboarding.successCount)
    Box(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = onboarding.hostSurface,
            transitionSpec = {
                fadeIn(tween(OnboardingMotionSpec.TEXT_ENTER_MS)) togetherWith
                    fadeOut(tween(surfaceExitMillis(from = initialState)))
            },
            label = "onboardingSurface"
        ) { surface ->
            OnboardingSurfaceContent(surface = surface, params = params, actions = actions)
        }
        OnboardingChecklistMorphComponent(
            onboarding = onboarding,
            isOtherOverlayActive = params.isHomeOverlayActive,
            glass = params.glass,
            actions = actions
        )
        OnboardingEditHintComponent(
            config = EditHintConfig(
                isVisible = params.showEditHint,
                glass = params.glass,
                grid = params.editHintGrid,
                isLandscape = params.coachConfig.isLandscape,
                isLeftHanded = params.coachConfig.isLeftHandedMode
            ),
            onSave = actions.onSaveGridEdit
        )
        OnboardingVisualSheetComponent(
            picker = onboarding.activePicker,
            selection = params.selection,
            actions = actions
        )
        // The preset's "Undo" (RF-F5) sits above the welcome, the home app step and the coach, which cover the home.
        AureoleUndoSnackbar(
            state = params.undoSnackbar,
            isLeftHanded = params.isLeftHanded,
            onUndo = actions.onUndo,
            onTimeout = actions.onUndoExpired,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(AureoleDS.dimens.small)
        )
    }
}

@Composable
private fun OnboardingSurfaceContent(
    surface: OnboardingSurface,
    params: OnboardingHostParams,
    actions: OnboardingActions
) {
    val onboarding = params.onboarding
    when (surface) {
        OnboardingSurface.WELCOME -> WelcomeScreen(
            onStart = actions.onStart,
            onSkipIntro = actions.onSkipIntro,
            onUsePreset = actions.onUsePreset.takeIf { params.canUsePreset }
        )
        OnboardingSurface.PRESETS -> OnboardingPresetScreen(
            onboarding = onboarding,
            actions = actions,
            isLeftHanded = params.isLeftHanded
        )
        OnboardingSurface.DEFAULT_HOME -> DefaultHomeStepComponent(
            onboarding = onboarding,
            actions = actions,
            glass = params.glass
        )
        OnboardingSurface.GESTURE_COACH -> GestureCoachComponent(
            onboarding = onboarding,
            actions = actions,
            glass = params.glass,
            config = params.coachConfig
        )
        OnboardingSurface.CELEBRATION -> OnboardingCelebrationComponent(
            onFinished = actions.onFinishCelebration,
            glass = params.glass
        )
        // The checklist has its own layer, which turns it into the pill.
        OnboardingSurface.CHECKLIST, OnboardingSurface.NONE -> Unit
    }
}

/** Confirm haptic each time a gesture or task gets done; restoring saved progress stays silent. */
@Composable
private fun OnboardingSuccessHaptics(successCount: Int) {
    val haptics = LocalHapticFeedback.current
    var lastCount by remember { mutableIntStateOf(successCount) }
    LaunchedEffect(successCount) {
        if (successCount > lastCount) haptics.performHapticFeedback(HapticFeedbackType.Confirm)
        lastCount = successCount
    }
}

@AureolePreview
@Composable
private fun OnboardingHostPreview() {
    AureoleLauncherTheme {
        OnboardingHostComponent(
            params = OnboardingHostParams(onboarding = OnboardingUiState(stage = OnboardingStage.GESTURES)),
            actions = OnboardingActions()
        )
    }
}
