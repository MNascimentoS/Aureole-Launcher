package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.icons.RoundCheck
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.rememberOnboardingLoopsEnabled
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import kotlinx.coroutines.delay

private const val SCRIM_ALPHA = 0.55f
private const val ILLUSTRATION_ALPHA = 0.5f
private const val HOME_BAR_MIN_ALPHA = 0.3f
private const val HOME_BAR_PULSE_MS = 900

/** "Make Aureole your home": bottom card over a scrim, plus the system dialog through [actions]. */
@Composable
fun DefaultHomeStepComponent(
    onboarding: OnboardingUiState,
    actions: OnboardingActions,
    glass: OnboardingGlass,
    modifier: Modifier = Modifier
) {
    if (onboarding.isDefaultLauncher) {
        LaunchedEffect(Unit) {
            delay(OnboardingMotionSpec.DEFAULT_HOME_SUCCESS_MS)
            actions.onPostponeDefaultHome()
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AureoleDS.colors.background.copy(alpha = SCRIM_ALPHA))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {},
        contentAlignment = Alignment.BottomCenter
    ) {
        OnboardingCardEntrance(
            fromTop = false,
            modifier = Modifier
                .widthIn(max = OnboardingMotionSpec.CHECKLIST_MAX_WIDTH)
                .navigationBarsPadding()
                .padding(AureoleDS.dimens.medium)
        ) {
            OnboardingGlassCard(glass = glass, modifier = Modifier.fillMaxWidth()) {
                DefaultHomeCardContent(onboarding = onboarding, actions = actions)
            }
        }
    }
}

@Composable
private fun DefaultHomeCardContent(onboarding: OnboardingUiState, actions: OnboardingActions) {
    Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)) {
        DefaultHomeIllustration()
        AureoleText(
            text = stringResource(R.string.home_onboarding_default_title),
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleLarge)
        )
        AureoleText(
            text = stringResource(R.string.home_onboarding_default_body),
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
        )
        if (onboarding.isDefaultLauncher) {
            DefaultHomeSuccess()
        } else {
            DefaultHomeButtons(showSystemSettings = onboarding.defaultHomeAttempted, actions = actions)
        }
    }
}

@Composable
private fun DefaultHomeButtons(showSystemSettings: Boolean, actions: OnboardingActions) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
        OnboardingPrimaryButton(
            text = stringResource(R.string.home_onboarding_default_confirm),
            onClick = actions.onRequestDefaultHome
        )
        // Shown after one attempt: some vendors' dialogs do not stick, the system screen always works.
        if (showSystemSettings) {
            OnboardingTextButton(
                text = stringResource(R.string.home_onboarding_default_open_settings),
                onClick = actions.onOpenSystemHomeSettings
            )
        }
        OnboardingTextButton(
            text = stringResource(R.string.home_onboarding_default_later),
            onClick = actions.onPostponeDefaultHome
        )
    }
}

@Composable
private fun DefaultHomeSuccess() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { liveRegion = LiveRegionMode.Polite }
            .padding(vertical = AureoleDS.dimens.small),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            AureoleDS.icons.RoundCheck(
                tint = AureoleDS.colors.badgeText,
                modifier = Modifier.size(AureoleDS.dimens.iconLarge)
            )
            Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
            AureoleText(
                text = stringResource(R.string.home_onboarding_default_success),
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.text, fontWeight = FontWeight.Bold)
            )
        }
    }
}

/** Mini phone whose Home bar pulses: the button that will open Aureole. */
@Composable
private fun DefaultHomeIllustration() {
    val loopsEnabled = rememberOnboardingLoopsEnabled()
    val pulse by rememberInfiniteTransition(label = "homeBar").animateFloat(
        initialValue = if (loopsEnabled) HOME_BAR_MIN_ALPHA else 1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(HOME_BAR_PULSE_MS), RepeatMode.Reverse),
        label = "homeBarAlpha"
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(AureoleDS.dimens.sizeExtraLarge)
            .background(
                AureoleDS.colors.background.copy(alpha = ILLUSTRATION_ALPHA),
                RoundedCornerShape(AureoleDS.dimens.radiusMedium)
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = AureoleDS.dimens.iconHuge, height = AureoleDS.dimens.sizeLarge)
                .border(
                    AureoleDS.dimens.borderMinimal,
                    AureoleDS.colors.onSurfaceHigh,
                    RoundedCornerShape(AureoleDS.dimens.radiusXSmall)
                ),
            contentAlignment = Alignment.Center
        ) {
            AureoleDS.icons.Logo(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = AureoleDS.dimens.xxSmall)
                    .size(width = AureoleDS.dimens.small, height = AureoleDS.dimens.minimal)
                    .alpha(pulse)
                    .background(AureoleDS.colors.onSurfaceHigh, RoundedCornerShape(AureoleDS.dimens.radiusFull))
            )
        }
    }
}

@AureolePreview
@Composable
private fun DefaultHomeStepPreview() {
    AureoleLauncherTheme {
        DefaultHomeStepComponent(
            onboarding = OnboardingUiState(defaultHomeAttempted = true),
            actions = OnboardingActions(),
            glass = OnboardingGlass()
        )
    }
}
