package dev.mnascimentos.aureole.feature.secretgame.components

import android.provider.Settings
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.model.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.secretgame.model.SecretShineState
import dev.mnascimentos.aureole.feature.secretgame.model.secretStarScale
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import dev.mnascimentos.aureole.feature.secretgame.model.SecretShineTuning as S

private const val STAR_DP = 16f
private const val STAR_GLOW_SCALE = 1.7f
private const val STAR_GLOW_ALPHA = 0.35f
private const val STAR_INSET = 0.25f
private const val HALF = 0.5f

/**
 * Brilho do "Muito obrigado!" para quem nunca jogou: [enabled] liga uma passada de realce e uma estrela, uma vez por
 * entrada, sem loop. Com "remover animações" do sistema não anima. O resultado muda com o toque via
 * [SecretShineState.cancel].
 */
@Composable
fun rememberSecretShine(enabled: Boolean): SecretShineState {
    val shine = remember { SecretShineState() }
    val resolver = LocalContext.current.contentResolver
    val animate = enabled && !shine.isCancelled &&
        Settings.Global.getFloat(resolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) != 0f
    LaunchedEffect(animate) {
        if (!animate) return@LaunchedEffect
        coroutineScope {
            launch { runShine(S.SWEEP_DELAY_MS, S.SWEEP_MS) { shine.sweep = it } }
            launch { runShine(S.STAR_DELAY_MS, S.STAR_MS) { shine.star = it } }
        }
    }
    return shine
}

private suspend fun runShine(delayMs: Int, durationMs: Int, onProgress: (Float) -> Unit) {
    delay(delayMs.toLong())
    Animatable(0f).animateTo(1f, tween(durationMs, easing = EaseInOut)) { onProgress(value) }
}

/** Realce claro que cruza o texto da esquerda para a direita; fora da passada, a cor base. */
private class SweepBrush(val progress: Float, val base: Color, val light: Color) : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        val band = size.width * S.HIGHLIGHT_WIDTH
        val center = -band + (size.width + 2 * band) * progress
        return LinearGradientShader(
            from = Offset(center - band * HALF, 0f),
            to = Offset(center + band * HALF, 0f),
            colors = listOf(base, light, base),
            colorStops = listOf(0f, HALF, 1f),
            tileMode = TileMode.Clamp
        )
    }
}

/** "Muito obrigado!" com o brilho de [shine]; não muda o tamanho nem a área de toque do texto. */
@Composable
fun SecretThankYou(shine: SecretShineState, modifier: Modifier = Modifier) {
    val base = AureoleDS.colors.onSurfaceMedium
    val light = AureoleDS.colors.onSurfaceHigh
    val sweeping = shine.sweep > 0f && shine.sweep < 1f
    val style = AureoleDS.typography.bodyMedium.let {
        if (sweeping) it.copy(brush = SweepBrush(shine.sweep, base, light)) else it
    }
    Box(
        modifier = modifier.drawWithContent {
            drawContent()
            drawStar(shine.star, light)
        }
    ) {
        AureoleText(
            text = stringResource(R.string.settings_root_thank_you),
            color = if (sweeping) Color.Unspecified else base,
            format = AureoleTextFormatParams(style = style, textAlign = TextAlign.Center)
        )
    }
}

private fun DrawScope.drawStar(progress: Float, color: Color) {
    val scale = secretStarScale(progress)
    if (scale <= 0f) return
    val radius = STAR_DP.dp.toPx() * HALF * scale
    val center = Offset(size.width - radius * STAR_INSET, radius * STAR_INSET)
    drawCircle(
        brush = Brush.radialGradient(
            listOf(color.copy(alpha = STAR_GLOW_ALPHA * scale.coerceAtMost(1f)), Color.Transparent),
            center = center,
            radius = radius * STAR_GLOW_SCALE
        ),
        radius = radius * STAR_GLOW_SCALE,
        center = center
    )
    rotate(S.STAR_TURN_DEGREES * progress, pivot = center) {
        drawPath(starPath(center, radius), color.copy(alpha = (scale / S.STAR_PEAK_SCALE).coerceIn(0f, 1f)))
    }
}

/** Estrela de 4 pontas com as laterais côncavas. */
private fun starPath(c: Offset, r: Float): Path = Path().apply {
    moveTo(c.x, c.y - r)
    quadraticTo(c.x, c.y, c.x + r, c.y)
    quadraticTo(c.x, c.y, c.x, c.y + r)
    quadraticTo(c.x, c.y, c.x - r, c.y)
    quadraticTo(c.x, c.y, c.x, c.y - r)
    close()
}
