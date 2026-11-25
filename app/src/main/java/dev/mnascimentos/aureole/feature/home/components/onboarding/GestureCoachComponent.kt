package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.RoundCheck
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.coachBodyRes
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.coachTitleRes
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.GestureCoachConfig
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.currentGestureStep
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.isHiddenOverDrawer
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.isWaitingForAlphabet
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.visibleGestureSteps
import kotlinx.coroutines.delay

private const val PENDING_DOT_ALPHA = 0.35f
private const val CHIP_SHRINK_SCALE = 0.3f
private val CHIP_ORIGIN = TransformOrigin(1f, 0f)

/**
 * Teaches the home gestures on the live home. Only the card's buttons take touches: the layer itself
 * has no pointer input, so the real gesture reaches the home underneath and completes the step.
 */
@Composable
fun GestureCoachComponent(
    onboarding: OnboardingUiState,
    actions: OnboardingActions,
    glass: OnboardingGlass,
    config: GestureCoachConfig,
    modifier: Modifier = Modifier
) {
    val step = onboarding.succeededStep
        ?: onboarding.currentGestureStep(
            config.isAlphabetScrubberDisabled,
            config.isLandscape
        )
        ?: return
    if (config.isHiddenOverDrawer(step)) return
    val succeeded = onboarding.succeededStep != null
    // "Nice!" stays a moment, then shrinks into the chip so the list behind it is free again (RF-E2, D-04).
    var isChip by remember(step, succeeded) { mutableStateOf(false) }
    if (succeeded && !isChip) {
        LaunchedEffect(step) {
            delay(OnboardingMotionSpec.COACH_SUCCESS_CARD_MS)
            isChip = true
        }
    }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        if (!succeeded && !config.isWaitingForAlphabet(step)) {
            GestureCoachHandComponent(step = step, isLeftHandedMode = config.isLeftHandedMode)
        }
        AnimatedContent(
            targetState = isChip,
            transitionSpec = {
                (fadeIn() + scaleIn(transformOrigin = CHIP_ORIGIN)) togetherWith
                    (fadeOut() + scaleOut(targetScale = CHIP_SHRINK_SCALE, transformOrigin = CHIP_ORIGIN))
            },
            label = "coachChip"
        ) { chip ->
            if (chip) {
                GestureCoachBackChip(glass = glass)
            } else {
                GestureCoachCard(onboarding = onboarding, step = step, actions = actions, glass = glass, config = config)
            }
        }
    }
}

@Composable
private fun GestureCoachCard(
    onboarding: OnboardingUiState,
    step: GestureCoachStep,
    actions: OnboardingActions,
    glass: OnboardingGlass,
    config: GestureCoachConfig
) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        OnboardingCardEntrance(
            fromTop = true,
            modifier = Modifier
                .widthIn(max = OnboardingMotionSpec.CHECKLIST_MAX_WIDTH)
                .statusBarsPadding()
                .padding(AureoleDS.dimens.medium)
        ) {
            OnboardingGlassCard(
                glass = glass,
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { liveRegion = LiveRegionMode.Polite }
            ) {
                GestureCoachCardContent(onboarding = onboarding, step = step, actions = actions, config = config)
            }
        }
    }
}

/** "◁ Go back to continue", in the top end corner once "Nice!" shrank (RF-E2). */
@Composable
private fun GestureCoachBackChip(glass: OnboardingGlass) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
        OnboardingGlassCard(
            glass = glass,
            contentPadding = AureoleDS.dimens.small,
            modifier = Modifier
                .statusBarsPadding()
                .padding(AureoleDS.dimens.medium)
                .semantics { liveRegion = LiveRegionMode.Polite }
        ) {
            AureoleText(
                text = stringResource(R.string.home_onboarding_coach_back_chip),
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall, maxLines = 1)
            )
        }
    }
}

@Composable
private fun GestureCoachCardContent(
    onboarding: OnboardingUiState,
    step: GestureCoachStep,
    actions: OnboardingActions,
    config: GestureCoachConfig
) {
    val succeeded = onboarding.succeededStep != null
    val bodyRes = if (!succeeded && config.isWaitingForAlphabet(step)) {
        R.string.home_apps_loading
    } else {
        step.coachBodyRes(succeeded)
    }
    Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
        GestureCoachStepIndicator(
            steps = visibleGestureSteps(
                config.isAlphabetScrubberDisabled,
                config.isLandscape
            ),
            completed = onboarding.completedSteps,
            current = step
        )
        AureoleText(
            text = stringResource(
                if (succeeded) R.string.home_onboarding_coach_success else step.coachTitleRes(config.isLeftHandedMode)
            ),
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleLarge)
        )
        AureoleText(
            text = stringResource(bodyRes),
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
        )
        if (!succeeded) {
            GestureCoachButtons(
                actions = actions,
                canShowMe = !config.isWaitingForAlphabet(step)
            )
        }
    }
}

/** "Show me" is there from the start, so the card never changes its layout while the step is taught (RF-E2). */
@Composable
private fun GestureCoachButtons(
    actions: OnboardingActions,
    canShowMe: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OnboardingTextButton(
            text = stringResource(R.string.home_onboarding_coach_skip),
            onClick = actions.onSkipGestures
        )
        if (canShowMe) {
            OnboardingTextButton(
                text = stringResource(R.string.home_onboarding_coach_show_me),
                onClick = actions.onShowMeGesture
            )
        }
    }
}

@Composable
private fun GestureCoachStepIndicator(
    steps: List<GestureCoachStep>,
    completed: Set<GestureCoachStep>,
    current: GestureCoachStep
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEach { item ->
            Box(modifier = Modifier.size(AureoleDS.dimens.iconSmall), contentAlignment = Alignment.Center) {
                GestureCoachStepDot(isDone = item in completed, isCurrent = item == current)
            }
        }
    }
}

@Composable
private fun GestureCoachStepDot(isDone: Boolean, isCurrent: Boolean) {
    if (isDone) {
        AureoleDS.icons.RoundCheck(
            tint = AureoleDS.colors.badgeText,
            modifier = Modifier.size(AureoleDS.dimens.iconSmall)
        )
        return
    }
    val color = if (isCurrent) {
        AureoleDS.colors.onSurfaceHigh
    } else {
        AureoleDS.colors.onSurfaceHigh.copy(alpha = PENDING_DOT_ALPHA)
    }
    Spacer(modifier = Modifier.size(AureoleDS.dimens.xSmall).background(color, CircleShape))
}

@AureolePreview
@Composable
private fun GestureCoachPreview() {
    AureoleLauncherTheme {
        GestureCoachComponent(
            onboarding = OnboardingUiState(completedSteps = setOf(GestureCoachStep.OPEN_APPS)),
            actions = OnboardingActions(),
            glass = OnboardingGlass(),
            config = GestureCoachConfig()
        )
    }
}
