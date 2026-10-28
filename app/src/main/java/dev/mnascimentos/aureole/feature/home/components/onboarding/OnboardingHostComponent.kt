package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.isIdle
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.successCount
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.visibleSurface
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingHostParams
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSurface
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState

/**
 * Top layer of the home: routes the onboarding stage to its surface, plus the minimized pill and the
 * appearance pickers. Draws nothing once the tour is over.
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
            targetState = onboarding.visibleSurface,
            transitionSpec = {
                fadeIn(tween(OnboardingMotionSpec.TEXT_ENTER_MS)) togetherWith
                    fadeOut(tween(OnboardingMotionSpec.WELCOME_EXIT_MS))
            },
            label = "onboardingSurface"
        ) { surface ->
            OnboardingSurfaceContent(surface = surface, params = params, actions = actions)
        }
        OnboardingChecklistPillComponent(
            onboarding = onboarding,
            isOtherOverlayActive = params.isHomeOverlayActive,
            glass = params.glass,
            onExpand = { actions.onSetChecklistExpanded(true) }
        )
        OnboardingEditHintComponent(
            isVisible = params.showEditHint,
            glass = params.glass,
            onSave = actions.onSaveGridEdit
        )
        OnboardingAppearancePickerComponent(
            picker = onboarding.activePicker,
            selection = params.selection,
            actions = actions
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
        OnboardingSurface.WELCOME -> WelcomeScreen(onStart = actions.onStart, onSkipIntro = actions.onSkipIntro)
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
        OnboardingSurface.CHECKLIST -> OnboardingChecklistComponent(
            onboarding = onboarding,
            actions = actions,
            glass = params.glass
        )
        OnboardingSurface.CELEBRATION -> OnboardingCelebrationComponent(onFinished = actions.onFinishCelebration)
        OnboardingSurface.NONE -> Unit
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
