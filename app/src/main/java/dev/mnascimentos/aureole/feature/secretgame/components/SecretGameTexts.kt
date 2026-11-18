package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.secretgame.model.SecretCountdownLabel
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameColors
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameDrawing as D
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameFrame
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

/** The texts of a frame: score on top, countdown, pause veil and result. */
@Composable
internal fun SecretGameTexts(
    frame: SecretGameFrame,
    colors: SecretGameColors,
    aspect: Float,
    modifier: Modifier = Modifier
) {
    val pen = TextPen(rememberTextMeasurer(), AureoleDS.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
    val go = stringResource(R.string.secret_game_go)
    val paused = stringResource(R.string.secret_game_paused)
    val resume = stringResource(R.string.secret_game_tap_to_resume)
    val resultTitle = frame.result?.let { stringResource(R.string.secret_game_score, it.score) }
    val resultSub = frame.result?.let {
        stringResource(if (it.isNewRecord) R.string.secret_game_new_record else R.string.secret_game_better_luck)
    }
    val veil = AureoleDS.colors.surface
    Canvas(modifier = modifier) {
        val u = size.width
        frame.hudScore?.let {
            drawCentered(pen, it.toString(), u * D.HUD_TOP, D.HUD_TEXT, colors.ship.copy(alpha = D.HUD_ALPHA))
        }
        frame.countdown?.let {
            val isGo = it.label == SecretCountdownLabel.GO
            val label = if (isGo) go else (T.COUNT_STEPS - 1 - it.label.ordinal).toString()
            val text = if (isGo) D.GO_TEXT else D.COUNT_TEXT
            drawCentered(
                pen, label, u * aspect * T.CENTER_Y_FRACTION, text * it.scale,
                colors.ship.copy(alpha = it.alpha), middle = true
            )
        }
        if (frame.isPaused) {
            drawRect(veil.copy(alpha = D.PAUSE_VEIL_ALPHA))
            val mid = u * aspect * T.CENTER_Y_FRACTION
            drawCentered(pen, paused, mid, D.TITLE_TEXT, colors.ship, middle = true)
            drawCentered(pen, resume, mid + u * D.RESULT_LINE_GAP, D.SUB_TEXT, colors.ship, middle = true)
        }
        frame.result?.let {
            val mid = u * aspect * T.CENTER_Y_FRACTION
            val color = colors.ship.copy(alpha = it.alpha)
            drawCentered(pen, resultTitle.orEmpty(), mid, D.TITLE_TEXT, color, middle = true)
            drawCentered(pen, resultSub.orEmpty(), mid + u * D.RESULT_LINE_GAP, D.SUB_TEXT, color, middle = true)
        }
    }
}

private class TextPen(val measurer: TextMeasurer, val style: TextStyle)

/** Draws [text] horizontally centered at [y] (its top, or its middle when [middle]); [size] is a width fraction. */
private fun DrawScope.drawCentered(
    pen: TextPen,
    text: String,
    y: Float,
    sizeFraction: Float,
    color: Color,
    middle: Boolean = false
) {
    val style = pen.style.copy(color = color, fontSize = (size.width * sizeFraction).toSp())
    val layout: TextLayoutResult = pen.measurer.measure(text, style)
    val top = if (middle) y - layout.size.height * D.HALF else y
    drawText(layout, topLeft = Offset((size.width - layout.size.width) * D.HALF, top))
}
