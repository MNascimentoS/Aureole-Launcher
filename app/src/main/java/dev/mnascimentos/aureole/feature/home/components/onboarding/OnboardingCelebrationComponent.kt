package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleHaloProgress
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.rememberOnboardingLoopsEnabled
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

private const val HALO_START_PROGRESS = 0.86f
private const val TEXT_FADE_PORTION = 0.15f
private const val GOLDEN_ANGLE_RAD = 2.39996f
private const val PARTICLE_MIN_TRAVEL = 0.18f
private const val PARTICLE_TRAVEL_STEPS = 5
private const val PARTICLE_TRAVEL_STEP = 0.06f
private val PARTICLE_RADIUS = 3.dp

/**
 * "All set": the halo closes its circle, particles burst from the center and the message fades in.
 * Leaves by itself after [OnboardingMotionSpec.CELEBRATION_MS] and never takes touches.
 */
@Composable
fun OnboardingCelebrationComponent(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val loopsEnabled = rememberOnboardingLoopsEnabled()
    val halo = remember { Animatable(if (loopsEnabled) HALO_START_PROGRESS else 1f) }
    val burst = remember { Animatable(if (loopsEnabled) 0f else 1f) }
    LaunchedEffect(Unit) {
        halo.animateTo(1f, tween(OnboardingMotionSpec.HALO_CLOSE_MS))
    }
    LaunchedEffect(Unit) {
        burst.animateTo(1f, tween(OnboardingMotionSpec.CELEBRATION_MS.toInt(), easing = LinearEasing))
    }
    // Timed apart from the burst, so the message stays as long with animations off.
    LaunchedEffect(Unit) {
        delay(OnboardingMotionSpec.CELEBRATION_MS)
        onFinished()
    }
    val particleColor = AureoleDS.colors.onSurfaceHigh
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (loopsEnabled) {
            Canvas(modifier = Modifier.fillMaxSize()) { drawParticles(burst.value, particleColor) }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
            modifier = Modifier
                .semantics { liveRegion = LiveRegionMode.Polite }
                .graphicsLayer { alpha = (burst.value / TEXT_FADE_PORTION).coerceIn(0f, 1f) }
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
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge, textAlign = TextAlign.Center)
            )
        }
    }
}

/** Up to [OnboardingMotionSpec.MAX_PARTICLES] dots on a golden-angle spiral, flying out and fading. */
private fun DrawScope.drawParticles(progress: Float, color: Color) {
    val reach = size.minDimension
    repeat(OnboardingMotionSpec.MAX_PARTICLES) { index ->
        val angle = index * GOLDEN_ANGLE_RAD
        val travel = PARTICLE_MIN_TRAVEL + (index % PARTICLE_TRAVEL_STEPS) * PARTICLE_TRAVEL_STEP
        val distance = reach * travel * progress
        drawCircle(
            color = color.copy(alpha = 1f - progress),
            radius = PARTICLE_RADIUS.toPx(),
            center = center + Offset(cos(angle) * distance, sin(angle) * distance)
        )
    }
}

@AureolePreview
@Composable
private fun OnboardingCelebrationPreview() {
    AureoleLauncherTheme {
        OnboardingCelebrationComponent(onFinished = {})
    }
}
