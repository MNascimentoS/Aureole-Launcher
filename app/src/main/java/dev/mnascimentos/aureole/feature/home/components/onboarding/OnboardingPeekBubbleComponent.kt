package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.mnascimentos.aureole.core.designsystem.components.AureoleLogoRing
import dev.mnascimentos.aureole.core.designsystem.model.LOGO_RING_LOGO_FRACTION_LARGE
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.ONBOARDING_HOME_PEEK_MS
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.rememberOnboardingLoopsEnabled
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.ChecklistSharedModifiers
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass

/**
 * The peek bubble (RF-13, RF-14): while the home shows alone after a task, a glass bubble with the logo and its ring
 * sits where the checklist pill goes, and the ring fills from 0 to 1 over the peek. With the system animations off
 * the peek is short and the ring stands full, the logo still. It has no click and lets every touch reach the home.
 */
@Composable
internal fun OnboardingPeekBubbleComponent(
    glass: OnboardingGlass,
    modifier: Modifier = Modifier,
    shared: ChecklistSharedModifiers = ChecklistSharedModifiers()
) {
    val loopsEnabled = rememberOnboardingLoopsEnabled()
    val progress = remember { Animatable(if (loopsEnabled) 0f else 1f) }
    LaunchedEffect(loopsEnabled) {
        if (loopsEnabled) {
            progress.animateTo(1f, tween(ONBOARDING_HOME_PEEK_MS.toInt(), easing = LinearEasing))
        }
    }
    OnboardingPeekBubble(progress = progress.value, glass = glass, modifier = modifier, shared = shared)
}

/**
 * The bubble at [progress], with no clock. Inside the checklist morph, [shared] ties its outline to the list card (or
 * the pill) and its logo to the list's halo, so the bubble opens into them.
 */
@Composable
internal fun OnboardingPeekBubble(
    progress: Float,
    glass: OnboardingGlass,
    modifier: Modifier = Modifier,
    shared: ChecklistSharedModifiers = ChecklistSharedModifiers()
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        // Same place as the pill: the bubble turns into it when the list comes back minimized.
        OnboardingGlassCard(
            glass = glass,
            contentPadding = AureoleDS.dimens.xSmall,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(AureoleDS.dimens.medium)
                .then(shared.card)
                .clip(CircleShape)
        ) {
            AureoleLogoRing(
                progress = progress,
                modifier = shared.halo,
                size = AureoleDS.dimens.iconHuge,
                logoFraction = LOGO_RING_LOGO_FRACTION_LARGE,
                isProgressAnimated = false
            )
        }
    }
}

@AureolePreview
@Composable
private fun OnboardingPeekBubblePreview() {
    AureoleLauncherTheme {
        Row {
            listOf(0f, 0.5f, 1f).forEach { progress ->
                Box(modifier = Modifier.weight(1f)) {
                    OnboardingPeekBubble(progress = progress, glass = OnboardingGlass())
                }
            }
        }
    }
}
