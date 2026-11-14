package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleHaloProgress
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.drawParticles
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.rememberOnboardingLoopsEnabled
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingGlass
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import kotlinx.coroutines.delay

private const val HALO_START_PROGRESS = 0.86f
private const val TEXT_FADE_PORTION = 0.15f

private const val SCRIM_ALPHA = 0.45f

/**
 * "All set": over a scrim, the halo closes its circle inside a glass card with the message, and particles burst
 * from the center. The card keeps the text readable over any home. Leaves after
 * [OnboardingMotionSpec.CELEBRATION_MS], or at a tap anywhere.
 */
@Composable
fun OnboardingCelebrationComponent(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    glass: OnboardingGlass = OnboardingGlass()
) {
    val loopsEnabled = rememberOnboardingLoopsEnabled()
    val halo = remember { Animatable(if (loopsEnabled) HALO_START_PROGRESS else 1f) }
    val burst = remember { Animatable(if (loopsEnabled) 0f else 1f) }
    LaunchedEffect(Unit) {
        halo.animateTo(1f, tween(OnboardingMotionSpec.HALO_CLOSE_MS))
    }
    LaunchedEffect(Unit) {
        burst.animateTo(1f, tween(OnboardingMotionSpec.CELEBRATION_BURST_MS, easing = LinearEasing))
    }
    // Timed apart from the burst, so the message stays as long with animations off.
    LaunchedEffect(Unit) {
        delay(OnboardingMotionSpec.CELEBRATION_MS)
        onFinished()
    }
    val particleColor = AureoleDS.colors.onSurfaceHigh
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AureoleDS.colors.background.copy(alpha = SCRIM_ALPHA))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                onFinished()
            },
        contentAlignment = Alignment.Center
    ) {
        if (loopsEnabled) {
            Canvas(modifier = Modifier.fillMaxSize()) { drawParticles(burst.value, particleColor) }
        }
        OnboardingGlassCard(
            glass = glass,
            modifier = Modifier
                .widthIn(max = OnboardingMotionSpec.CHECKLIST_MAX_WIDTH)
                .padding(horizontal = AureoleDS.dimens.xLarge)
                .semantics { liveRegion = LiveRegionMode.Polite }
                .graphicsLayer { alpha = (burst.value / TEXT_FADE_PORTION).coerceIn(0f, 1f) }
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
                modifier = Modifier.fillMaxWidth()
            ) {
                AureoleHaloProgress(progress = halo.value)
                AureoleText(
                    text = stringResource(R.string.home_onboarding_done_title),
                    color = AureoleDS.colors.onSurfaceHigh,
                    format = AureoleTextFormatParams(style = AureoleDS.typography.titleLarge)
                )
                AureoleText(
                    text = stringResource(R.string.home_onboarding_done_subtitle),
                    color = AureoleDS.colors.onSurfaceMedium,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}

@AureolePreview
@Composable
private fun OnboardingCelebrationPreview() {
    AureoleLauncherTheme {
        OnboardingCelebrationComponent(onFinished = {})
    }
}
