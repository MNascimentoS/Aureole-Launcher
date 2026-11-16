package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.runtime.State
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.EdgeScrubPath
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberDimensions

private const val FINGER_ALPHA = 0.9f
private const val TRAIL_ALPHA = 0.35f
private const val SWIPE_START_Y = 0.78f
private const val PRESS_Y = 0.6f
private const val FADE_OUT_START = 0.8f

private fun fadeFor(phase: Float, loopMs: Int): Float {
    val appear = OnboardingMotionSpec.HAND_APPEAR_MS.toFloat() / loopMs
    return when {
        phase < appear -> phase / appear
        phase > FADE_OUT_START -> ((1f - phase) / (1f - FADE_OUT_START)).coerceIn(0f, 1f)
        else -> 1f
    }
}

private fun DrawScope.drawFinger(center: Offset, alpha: Float, color: Color) {
    drawCircle(color.copy(alpha = FINGER_ALPHA * alpha), OnboardingMotionSpec.HAND_FINGER_RADIUS.toPx(), center)
}

internal fun DrawScope.drawSwipeUp(phase: State<Float>, loopMs: Int, color: Color) {
    val t = FastOutSlowInEasing.transform((phase.value / FADE_OUT_START).coerceIn(0f, 1f))
    val start = Offset(size.width / 2, size.height * SWIPE_START_Y)
    val finger = start.copy(y = start.y - size.height * OnboardingMotionSpec.HAND_SWIPE_TRAVEL * t)
    val alpha = fadeFor(phase.value, loopMs)
    val trailWidth = OnboardingMotionSpec.HAND_TRAIL_WIDTH.toPx()
    drawLine(color.copy(alpha = TRAIL_ALPHA * alpha), start, finger, trailWidth, StrokeCap.Round)
    drawFinger(finger, alpha, color)
}

/** Finger position of the edge scrub hint at [phase] (0..1): pull in from the edge, pause, then slide down. */
internal fun edgeScrubPoint(phase: Float, path: EdgeScrubPath): Offset {
    val pullRaw = phase / OnboardingMotionSpec.HAND_EDGE_PULL_END
    val pull = FastOutSlowInEasing.transform(pullRaw.coerceIn(0f, 1f))
    val slideSpan = OnboardingMotionSpec.HAND_EDGE_SLIDE_END - OnboardingMotionSpec.HAND_EDGE_SLIDE_START
    val slideRaw = (phase - OnboardingMotionSpec.HAND_EDGE_SLIDE_START) / slideSpan
    val slide = FastOutSlowInEasing.transform(slideRaw.coerceIn(0f, 1f))
    return Offset(
        x = path.edgeX + path.inwardSign * path.pullPx * pull,
        y = path.startY + (path.endY - path.startY) * slide
    )
}

internal fun DrawScope.drawEdgeScrub(phase: State<Float>, isLeftHandedMode: Boolean, color: Color) {
    val (startY, endY) = scrubberHintPath(size.height, ScrubberDimensions.PaddingBottom.toPx())
    val path = EdgeScrubPath(
        edgeX = if (isLeftHandedMode) 0f else size.width,
        inwardSign = if (isLeftHandedMode) 1f else -1f,
        pullPx = OnboardingMotionSpec.HAND_EDGE_PULL_TRAVEL.toPx(),
        startY = startY,
        endY = endY
    )
    val finger = edgeScrubPoint(phase.value, path)
    val alpha = fadeFor(phase.value, OnboardingMotionSpec.HAND_EDGE_LOOP_MS)
    val trailColor = color.copy(alpha = TRAIL_ALPHA * alpha)
    val trailWidth = OnboardingMotionSpec.HAND_TRAIL_WIDTH.toPx()
    // Two segments draw the whole "L": edge to the pulled point, then the pulled point to the finger.
    drawLine(trailColor, Offset(path.edgeX, startY), Offset(finger.x, startY), trailWidth, StrokeCap.Round)
    if (finger.y > startY) {
        drawLine(trailColor, Offset(path.pulledX, startY), finger, trailWidth, StrokeCap.Round)
    }
    drawFinger(finger, alpha, color)
}

internal fun DrawScope.drawPressAndHold(phase: State<Float>, loopMs: Int, color: Color) {
    val center = Offset(size.width / 2, size.height * PRESS_Y)
    val fillPortion = OnboardingMotionSpec.HAND_PRESS_FILL_MS.toFloat() / loopMs
    val pressed = (phase.value / fillPortion).coerceIn(0f, 1f)
    val alpha = fadeFor(phase.value, loopMs)
    val ringRadius = OnboardingMotionSpec.HAND_FINGER_RADIUS.toPx() + OnboardingMotionSpec.HAND_PRESS_RING_GAP.toPx()
    drawArc(
        color = color.copy(alpha = alpha),
        startAngle = -90f,
        sweepAngle = 360f * pressed,
        useCenter = false,
        topLeft = Offset(center.x - ringRadius, center.y - ringRadius),
        size = Size(ringRadius * 2, ringRadius * 2),
        style = Stroke(OnboardingMotionSpec.HAND_TRAIL_WIDTH.toPx(), cap = StrokeCap.Round)
    )
    drawFinger(center, alpha, color)
}
