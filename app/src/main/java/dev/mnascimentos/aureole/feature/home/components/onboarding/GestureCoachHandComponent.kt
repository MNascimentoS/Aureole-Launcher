package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.rememberOnboardingLoopsEnabled
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.scrubberHintPath
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberDimensions

private const val SETTLED_PHASE = 0.45f
private const val FINGER_ALPHA = 0.9f
private const val TRAIL_ALPHA = 0.35f
private const val SWIPE_START_Y = 0.78f
private const val PRESS_Y = 0.6f
private const val FADE_OUT_START = 0.8f

// Edge scrub: the first third pulls in from the edge, the next half slides down.
private const val EDGE_PULL_PORTION = 1f / 3
private const val EDGE_SLIDE_PORTION = 0.5f
private val FINGER_RADIUS = 18.dp
private val EDGE_INSET = 6.dp
private val PRESS_RING_GAP = 10.dp
private val TRAIL_WIDTH = 4.dp

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

private fun fadeFor(phase: Float, loopMs: Int): Float {
    val appear = OnboardingMotionSpec.HAND_APPEAR_MS.toFloat() / loopMs
    return when {
        phase < appear -> phase / appear
        phase > FADE_OUT_START -> ((1f - phase) / (1f - FADE_OUT_START)).coerceIn(0f, 1f)
        else -> 1f
    }
}

private fun DrawScope.drawFinger(center: Offset, alpha: Float, color: Color) {
    drawCircle(color.copy(alpha = FINGER_ALPHA * alpha), FINGER_RADIUS.toPx(), center)
}

private fun DrawScope.drawSwipeUp(phase: State<Float>, loopMs: Int, color: Color) {
    val t = FastOutSlowInEasing.transform((phase.value / FADE_OUT_START).coerceIn(0f, 1f))
    val start = Offset(size.width / 2, size.height * SWIPE_START_Y)
    val finger = start.copy(y = start.y - size.height * OnboardingMotionSpec.HAND_SWIPE_TRAVEL * t)
    val alpha = fadeFor(phase.value, loopMs)
    drawLine(color.copy(alpha = TRAIL_ALPHA * alpha), start, finger, TRAIL_WIDTH.toPx(), StrokeCap.Round)
    drawFinger(finger, alpha, color)
}

private fun DrawScope.drawEdgeScrub(phase: State<Float>, isLeftHandedMode: Boolean, color: Color) {
    val edgeX = if (isLeftHandedMode) EDGE_INSET.toPx() else size.width - EDGE_INSET.toPx()
    val inward = if (isLeftHandedMode) 1f else -1f
    val pullPhase = (phase.value / EDGE_PULL_PORTION).coerceIn(0f, 1f)
    val slideRaw = (phase.value - EDGE_PULL_PORTION) / EDGE_SLIDE_PORTION
    val slidePhase = FastOutSlowInEasing.transform(slideRaw.coerceIn(0f, 1f))
    val (startY, endY) = scrubberHintPath(size.height, ScrubberDimensions.PaddingBottom.toPx())
    val x = edgeX + inward * ScrubberDimensions.EdgeGestureStartWidth.toPx() * pullPhase
    val y = startY + (endY - startY) * slidePhase
    val alpha = fadeFor(phase.value, OnboardingMotionSpec.HAND_EDGE_LOOP_MS)
    drawLine(
        color.copy(alpha = TRAIL_ALPHA * alpha),
        Offset(x, startY),
        Offset(x, y),
        TRAIL_WIDTH.toPx(),
        StrokeCap.Round
    )
    drawFinger(Offset(x, y), alpha, color)
}

private fun DrawScope.drawPressAndHold(phase: State<Float>, loopMs: Int, color: Color) {
    val center = Offset(size.width / 2, size.height * PRESS_Y)
    val fillPortion = OnboardingMotionSpec.HAND_PRESS_FILL_MS.toFloat() / loopMs
    val pressed = (phase.value / fillPortion).coerceIn(0f, 1f)
    val alpha = fadeFor(phase.value, loopMs)
    val ringRadius = FINGER_RADIUS.toPx() + PRESS_RING_GAP.toPx()
    drawArc(
        color = color.copy(alpha = alpha),
        startAngle = -90f,
        sweepAngle = 360f * pressed,
        useCenter = false,
        topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
        size = Size(ringRadius * 2, ringRadius * 2),
        style = Stroke(TRAIL_WIDTH.toPx(), cap = StrokeCap.Round)
    )
    drawFinger(center, alpha, color)
}

@AureolePreview
@Composable
private fun GestureCoachHandPreview() {
    AureoleLauncherTheme {
        GestureCoachHandComponent(step = GestureCoachStep.ALPHABET_SCRUB, isLeftHandedMode = false)
    }
}
