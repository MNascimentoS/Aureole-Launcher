package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.drawEdgeScrub
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.drawPressAndHold
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.drawSwipeUp
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.rememberOnboardingLoopsEnabled
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec

private const val SETTLED_PHASE = 0.45f

/**
 * Ghost finger demonstrating [step]. Drawn on a plain canvas with no pointer input, so every real
 * touch passes through to the home underneath.
 */
@Composable
fun GestureCoachHandComponent(
    step: GestureCoachStep,
    isLeftHandedMode: Boolean,
    modifier: Modifier = Modifier
) {
    val loopsEnabled = rememberOnboardingLoopsEnabled()
    val loopMs = when (step) {
        GestureCoachStep.OPEN_APPS -> OnboardingMotionSpec.HAND_SWIPE_LOOP_MS
        GestureCoachStep.ALPHABET_SCRUB -> OnboardingMotionSpec.HAND_EDGE_LOOP_MS
        GestureCoachStep.CUSTOMIZE -> OnboardingMotionSpec.HAND_PRESS_LOOP_MS
    }
    val phase = rememberInfiniteTransition(label = "coachHand").animateFloat(
        initialValue = if (loopsEnabled) 0f else SETTLED_PHASE,
        targetValue = if (loopsEnabled) 1f else SETTLED_PHASE,
        animationSpec = infiniteRepeatable(tween(loopMs, easing = LinearEasing)),
        label = "coachHandPhase"
    )
    val color = AureoleDS.colors.onSurfaceHigh
    Canvas(modifier = modifier.fillMaxSize()) {
        when (step) {
            GestureCoachStep.OPEN_APPS -> drawSwipeUp(phase, loopMs, color)
            GestureCoachStep.ALPHABET_SCRUB -> drawEdgeScrub(phase, isLeftHandedMode, color)
            GestureCoachStep.CUSTOMIZE -> drawPressAndHold(phase, loopMs, color)
        }
    }
}

@AureolePreview
@Composable
private fun GestureCoachHandPreview() {
    AureoleLauncherTheme {
        GestureCoachHandComponent(step = GestureCoachStep.ALPHABET_SCRUB, isLeftHandedMode = false)
    }
}
