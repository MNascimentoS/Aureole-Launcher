package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.feature.secretgame.model.SecretSprite
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameDrawing as D

/** Rastro de um rosto caindo: cauda afunilada e três linhas, com base no topo do rosto. Só é chamado com trail > 0. */
internal fun DrawScope.drawTrail(sprite: SecretSprite, trail: Float, color: Color) {
    val u = size.width
    val x = sprite.x * u
    val faceWidth = sprite.width * u
    val baseY = sprite.y * u - D.TRAIL_BASE_RISE * faceWidth
    val topY = baseY - trail * u
    val half = faceWidth * D.HALF
    val baseHalf = half * D.TRAIL_BASE_WIDTH
    val tipHalf = half * D.TRAIL_TIP_WIDTH
    val midY = (baseY + topY) * D.HALF
    val tail = Path().apply {
        moveTo(x - baseHalf, baseY)
        quadraticTo(x - (baseHalf + tipHalf) * D.HALF, midY, x - tipHalf, topY)
        lineTo(x + tipHalf, topY)
        quadraticTo(x + (baseHalf + tipHalf) * D.HALF, midY, x + baseHalf, baseY)
        close()
    }
    val fade = { alpha: Float ->
        Brush.verticalGradient(
            listOf(Color.Transparent, color.copy(alpha = alpha * sprite.alpha)),
            startY = topY,
            endY = baseY
        )
    }
    drawPath(tail, fade(D.TRAIL_BODY_ALPHA))
    listOf(
        -D.TRAIL_LINE_X to D.TRAIL_SIDE_LENGTH,
        0f to D.TRAIL_MID_LENGTH,
        D.TRAIL_LINE_X to D.TRAIL_SIDE_LENGTH
    ).forEach { (offset, length) ->
        val lineX = x + offset * faceWidth
        val lineTop = baseY - trail * u * length
        drawLine(
            brush = Brush.verticalGradient(
                listOf(Color.Transparent, color.copy(alpha = D.TRAIL_LINE_ALPHA * sprite.alpha)),
                startY = lineTop,
                endY = baseY
            ),
            start = Offset(lineX, lineTop),
            end = Offset(lineX, baseY),
            strokeWidth = D.TRAIL_LINE_DP.dp.toPx()
        )
    }
}
