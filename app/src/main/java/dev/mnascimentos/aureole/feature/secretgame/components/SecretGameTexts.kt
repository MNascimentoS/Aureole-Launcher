package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
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
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameFrame
import dev.mnascimentos.aureole.feature.secretgame.model.SecretResultFrame
import dev.mnascimentos.aureole.feature.secretgame.model.SecretText
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameDrawing as D
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
    val level = stringResource(R.string.secret_game_level, frame.levelLabel)
    val pause = PauseTexts(
        paused = stringResource(R.string.secret_game_paused),
        resume = stringResource(R.string.secret_game_tap_to_resume),
        backToExit = stringResource(R.string.secret_game_back_to_exit)
    )
    val result = frame.result?.let {
        ResultTexts(
            title = stringResource(R.string.secret_game_score, it.score),
            best = stringResource(R.string.secret_game_best_score, it.bestScore),
            sub = stringResource(
                if (it.isNewRecord) R.string.secret_game_new_record else R.string.secret_game_better_luck
            ),
            backToExit = pause.backToExit
        )
    }
    val veil = AureoleDS.colors.surface
    Canvas(modifier = modifier) {
        val u = size.width
        frame.hudScore?.let {
            val alpha = (D.HUD_ALPHA + T.HUD_FLASH_ALPHA * frame.hudFlash).coerceIn(0f, 1f)
            val size = D.HUD_TEXT * (1f + T.HUD_FLASH_SCALE * frame.hudFlash)
            drawCentered(pen, SecretText(it.toString(), u * D.HUD_TOP, size, colors.ship.copy(alpha = alpha)))
            if (frame.hudFlash > 0f) {
                val fade = minOf(1f, frame.hudFlash * T.LEVEL_LABEL_FADE) * T.LEVEL_LABEL_ALPHA
                val top = u * (D.HUD_TOP + D.HUD_TEXT * D.LEVEL_GAP)
                drawCentered(pen, SecretText(level, top, D.LEVEL_TEXT, colors.ship.copy(alpha = fade)))
            }
        }
        frame.countdown?.let {
            val isGo = it.label == SecretCountdownLabel.GO
            val label = if (isGo) go else (T.COUNT_STEPS - 1 - it.label.ordinal).toString()
            val text = if (isGo) D.GO_TEXT else D.COUNT_TEXT
            val spec =
                SecretText(
                    label,
                    u * aspect * T.CENTER_Y_FRACTION,
                    text * it.scale,
                    colors.ship.copy(alpha = it.alpha),
                    true
                )
            drawCentered(pen, spec)
        }
        val mid = u * aspect * T.CENTER_Y_FRACTION
        if (frame.isPaused) {
            drawRect(veil.copy(alpha = D.PAUSE_VEIL_ALPHA))
            drawPause(pen, pause, colors, mid)
        }
        frame.result?.let { shown -> result?.let { drawResult(pen, it, shown, colors, mid) } }
    }
}

private class PauseTexts(val paused: String, val resume: String, val backToExit: String)

private class ResultTexts(val title: String, val best: String, val sub: String, val backToExit: String)

private fun DrawScope.drawPause(pen: TextPen, texts: PauseTexts, colors: SecretGameColors, mid: Float) {
    val u = size.width
    drawCentered(pen, SecretText(texts.paused, mid, D.TITLE_TEXT, colors.ship, true))
    drawCentered(pen, SecretText(texts.resume, mid + u * D.RESULT_LINE_GAP, D.SUB_TEXT, colors.ship, true))
    val backColor = colors.ship.copy(alpha = D.BACK_HINT_ALPHA)
    drawCentered(pen, SecretText(texts.backToExit, mid + u * D.BACK_HINT_GAP, D.SUB_TEXT, backColor, true))
}

/** Your Score (big), Best Score (small, darker, only when it was not just beaten), the message and the exit hint. */
private fun DrawScope.drawResult(
    pen: TextPen,
    texts: ResultTexts,
    result: SecretResultFrame,
    colors: SecretGameColors,
    mid: Float
) {
    val u = size.width
    val color = colors.ship.copy(alpha = result.alpha)
    val dim = colors.ship.copy(alpha = result.alpha * D.BACK_HINT_ALPHA)
    drawCentered(pen, SecretText(texts.title, mid, D.TITLE_TEXT, color, true))
    var line = 1f
    if (!result.isNewRecord) {
        drawCentered(pen, SecretText(texts.best, mid + u * D.RESULT_LINE_GAP, D.BEST_TEXT, dim, true))
        line = D.RESULT_SUB_LINES
    }
    val sub = mid + u * D.RESULT_LINE_GAP * line
    drawCentered(pen, SecretText(texts.sub, sub, D.SUB_TEXT, color, true))
    drawCentered(pen, SecretText(texts.backToExit, sub + u * D.RESULT_LINE_GAP, D.SUB_TEXT, dim, true))
}

private class TextPen(val measurer: TextMeasurer, val style: TextStyle)

/** Draws [spec] horizontally centered on the canvas. */
private fun DrawScope.drawCentered(pen: TextPen, spec: SecretText) {
    val style = pen.style.copy(color = spec.color, fontSize = (size.width * spec.size).toSp())
    val layout: TextLayoutResult = pen.measurer.measure(spec.text, style)
    val top = if (spec.middle) spec.y - layout.size.height * D.HALF else spec.y
    drawText(layout, topLeft = Offset((size.width - layout.size.width) * D.HALF, top))
}
