package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameDrawing as D
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

private const val SIDES = 4

/** Top-left of the frame of the [left] or right button, in width fractions (the frame is [T.BUTTON_SIZE] wide). */
internal fun buttonTopLeft(left: Boolean, aspect: Float): Offset {
    val x = if (left) T.BUTTON_SIDE_INSET else 1f - T.BUTTON_SIDE_INSET - T.BUTTON_SIZE
    return Offset(x, aspect - T.BUTTON_BOTTOM_OFFSET - T.BUTTON_SIZE * D.HALF)
}

/** The two arrow frames: [draw] traces the outline (0..1), [collapse] shrinks them into the bottom corners. */
internal fun DrawScope.drawButtons(draw: Float, collapse: Float, color: Color) {
    val aspect = size.height / size.width
    drawButton(left = true, aspect = aspect, draw = draw, collapse = collapse, color = color)
    drawButton(left = false, aspect = aspect, draw = draw, collapse = collapse, color = color)
}

private fun DrawScope.drawButton(left: Boolean, aspect: Float, draw: Float, collapse: Float, color: Color) {
    val u = size.width
    val side = T.BUTTON_SIZE * u
    val origin = buttonTopLeft(left, aspect) * u
    val corner = Offset(if (left) 0f else u, size.height)
    val scale = 1f - collapse * (1f - D.COLLAPSE_SCALE_MIN)
    val perimeter = side * SIDES
    val stroke = Stroke(
        width = D.BUTTON_STROKE * u,
        cap = StrokeCap.Round,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(perimeter * draw, perimeter))
    )
    val tint = color.copy(alpha = D.BUTTON_ALPHA * (1f - collapse))
    withTransform({
        scale(scale, scale, pivot = corner)
        translate(origin.x, origin.y)
    }) {
        drawRoundRect(tint, Offset.Zero, Size(side, side), CornerRadius(D.BUTTON_RADIUS * u), stroke)
        if (draw > 0f) drawArrow(left, side, tint, draw)
    }
}

private fun DrawScope.drawArrow(left: Boolean, side: Float, color: Color, draw: Float) {
    val half = side * D.HALF
    val reach = side * D.BUTTON_ARROW
    val dir = if (left) -1f else 1f
    val path = Path().apply {
        moveTo(half - dir * reach * D.HALF, half - reach)
        lineTo(half + dir * reach * D.HALF, half)
        lineTo(half - dir * reach * D.HALF, half + reach)
    }
    val style = Stroke(D.BUTTON_STROKE * size.width, cap = StrokeCap.Round)
    drawPath(path, color.copy(alpha = color.alpha * draw), style = style)
}
