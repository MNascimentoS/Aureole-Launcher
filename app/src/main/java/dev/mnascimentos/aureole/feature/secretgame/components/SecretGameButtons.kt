package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.feature.secretgame.model.SecretButtonsFrame
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameLayout
import dev.mnascimentos.aureole.feature.secretgame.model.secretButtonCornerX
import dev.mnascimentos.aureole.feature.secretgame.model.secretButtonTopLeft
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameDrawing as D
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

/**
 * The two arrow frames, under the arena or on its sides ([layout]): [frame] traces the outline, collapses them
 * into the window corners and shows the touch effect.
 */
internal fun DrawScope.drawButtons(frame: SecretButtonsFrame, layout: SecretGameLayout, color: Color) {
    drawButton(true, layout, frame, color)
    drawButton(false, layout, frame, color)
}

private fun DrawScope.drawButton(left: Boolean, layout: SecretGameLayout, frame: SecretButtonsFrame, color: Color) {
    val draw = frame.draw
    if (draw <= D.MIN_DRAW) return
    val u = size.width
    val side = T.BUTTON_SIZE * u
    val press = if (left) frame.leftPress else frame.rightPress
    val squeeze = if (left) frame.leftSqueeze else frame.rightSqueeze
    val origin = secretButtonTopLeft(left, layout) * u
    val corner = Offset(secretButtonCornerX(left, layout) * u, size.height)
    val scale = 1f - frame.collapse * (1f - D.COLLAPSE_SCALE_MIN)
    val tint = color.copy(alpha = D.BUTTON_ALPHA * (1f - frame.collapse))
    withTransform({
        scale(scale, scale, pivot = corner)
        translate(origin.x, origin.y)
    }) {
        if (press > 0f) drawPressEffect(side, press, color.copy(alpha = 1f - frame.collapse))
        withTransform({ scale(1f - squeeze, 1f - squeeze, pivot = Offset(side * D.HALF, side * D.HALF)) }) {
            val stroke = Stroke(width = D.BUTTON_STROKE * u, cap = StrokeCap.Round)
            val radius = D.BUTTON_RADIUS * u
            drawPath(partialOutline(side, radius, draw), tint.copy(alpha = tint.alpha * draw), style = stroke)
            val nudge = (if (left) -1f else 1f) * T.BUTTON_SIZE * T.PRESS_NUDGE * u * press
            translate(nudge, 0f) { drawArrow(left, side, tint, draw) }
        }
    }
}

/** Touch effect: soft fill and a ring that closes in on the frame, both fading with [press] (1 → 0). */
private fun DrawScope.drawPressEffect(side: Float, press: Float, color: Color) {
    val radius = CornerRadius(D.BUTTON_RADIUS * size.width)
    drawRoundRect(color.copy(alpha = color.alpha * T.PRESS_FILL_ALPHA * press), Offset.Zero, Size(side, side), radius)
    val half = side * (D.PRESS_START_RING + T.PRESS_RING_GROWTH * (1f - press))
    val center = side * D.HALF
    drawRoundRect(
        color = color.copy(alpha = color.alpha * T.PRESS_RING_ALPHA * press),
        topLeft = Offset(center - half, center - half),
        size = Size(half * 2f, half * 2f),
        cornerRadius = radius,
        style = Stroke(D.PRESS_RING_STROKE_DP.dp.toPx())
    )
}

/** The first [draw] (0..1] of the frame's outline: a measured segment, so no zero-length dash can leave a dot. */
private fun partialOutline(side: Float, radius: Float, draw: Float): Path {
    val outline = Path().apply { addRoundRect(RoundRect(0f, 0f, side, side, CornerRadius(radius))) }
    val measure = PathMeasure().apply { setPath(outline, false) }
    return Path().also { measure.getSegment(0f, measure.length * draw.coerceAtMost(1f), it, true) }
}

/** ← or →: a shaft [D.BUTTON_SHAFT] of the side to each side of the center and a head [D.BUTTON_HEAD] long. */
private fun DrawScope.drawArrow(left: Boolean, side: Float, color: Color, draw: Float) {
    val center = side * D.HALF
    val dir = if (left) -1f else 1f
    val shaft = side * D.BUTTON_SHAFT
    val head = side * D.BUTTON_HEAD
    val tipX = center + dir * shaft
    val path = Path().apply {
        moveTo(center - dir * shaft, center)
        lineTo(tipX, center)
        moveTo(tipX - dir * head, center - head)
        lineTo(tipX, center)
        lineTo(tipX - dir * head, center + head)
    }
    val style = Stroke(D.BUTTON_STROKE * size.width, cap = StrokeCap.Round, join = StrokeJoin.Round)
    drawPath(path, color.copy(alpha = color.alpha * draw), style = style)
}
