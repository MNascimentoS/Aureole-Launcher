package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.feature.secretgame.model.SecretSprite
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameDrawing as D

/** Medidas do rastro de um rosto, em px: nasce por trás da cabeça e some para cima; sem nenhuma borda reta. */
private class TrailBox(val x: Float, val head: Float, val top: Float, val bottom: Float, val w: Float, val h: Float)

/**
 * Rastro de um rosto caindo, desenhado antes dele. [trail] é o comprimento em larguras de tela e [faceAspect] a
 * altura ÷ largura do rosto. Só é chamado com trail > 0.
 */
internal fun DrawScope.drawTrail(sprite: SecretSprite, trail: Float, faceAspect: Float, color: Color) {
    val u = size.width
    val w = sprite.width * u
    val h = w * faceAspect
    val head = sprite.y * u - D.TRAIL_HEAD_RISE * h
    val box = TrailBox(sprite.x * u, head, head - trail * u, sprite.y * u + D.TRAIL_BOTTOM_DROP * h, w, h)
    val k = (box.head - box.top) / (box.bottom - box.top)
    val body = Brush.verticalGradient(
        colorStops = arrayOf(
            0f to color.copy(alpha = 0f),
            D.TRAIL_MID_STOP * k to color.copy(alpha = D.TRAIL_MID_ALPHA * sprite.alpha),
            k to color.copy(alpha = D.TRAIL_BODY_ALPHA * sprite.alpha),
            1f to color.copy(alpha = 0f),
        ),
        startY = box.top,
        endY = box.bottom
    )
    drawPath(trailBody(box), body)
    val lines = listOf(
        -D.TRAIL_LINE_X to D.TRAIL_SIDE_LENGTH,
        0f to D.TRAIL_MID_LENGTH,
        D.TRAIL_LINE_X to D.TRAIL_SIDE_LENGTH
    )
    lines.forEach { (offset, length) ->
        drawTrailLine(
            box,
            offset,
            length * trail * u,
            color.copy(alpha = sprite.alpha)
        )
    }
}

/** Cauda que sai do topo, desce pelos lados e fecha numa meia elipse embaixo da cabeça. */
private fun trailBody(box: TrailBox): Path {
    val rx = D.TRAIL_RADIUS_X * box.w
    val cy = box.head + D.TRAIL_CENTER_DROP * box.h
    val controlY = box.head - D.TRAIL_CONTROL_Y * (box.head - box.top)
    val controlX = D.TRAIL_CONTROL_X * rx
    val tip = D.TRAIL_TIP_X * box.w
    return Path().apply {
        moveTo(box.x - tip, box.top)
        quadraticTo(box.x - controlX, controlY, box.x - rx, cy)
        arcTo(
            Rect(box.x - rx, cy - (box.bottom - cy), box.x + rx, box.bottom),
            D.HALF_TURN_DEG,
            -D.HALF_TURN_DEG,
            false
        )
        quadraticTo(box.x + controlX, controlY, box.x + tip, box.top)
        close()
    }
}

private fun DrawScope.drawTrailLine(box: TrailBox, offset: Float, length: Float, color: Color) {
    val lineX = box.x + offset * box.w
    val end = box.head + D.TRAIL_LINE_END * box.h
    val start = box.head - length
    drawLine(
        brush = Brush.verticalGradient(
            colorStops = arrayOf(
                0f to color.copy(alpha = 0f),
                D.TRAIL_LINE_PEAK to color.copy(alpha = D.TRAIL_LINE_ALPHA * color.alpha),
                1f to color.copy(alpha = 0f),
            ),
            startY = start,
            endY = end
        ),
        start = Offset(lineX, start),
        end = Offset(lineX, end),
        strokeWidth = D.TRAIL_LINE_DP.dp.toPx()
    )
}
