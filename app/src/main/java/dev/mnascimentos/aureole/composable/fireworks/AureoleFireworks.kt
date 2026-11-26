package dev.mnascimentos.aureole.composable.fireworks

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkColors
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkRocket
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkSpark
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkTone
import dev.mnascimentos.aureole.composable.fireworks.model.FireworksState
import dev.mnascimentos.aureole.composable.fireworks.model.FireworksTuning
import dev.mnascimentos.aureole.composable.fireworks.model.fireworksShow
import dev.mnascimentos.aureole.composable.fireworks.model.step
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private const val NANOS_PER_MS = 1_000_000L
private const val ROCKET_STROKE_DP = 1.6f

/** Desenha foguetes (linha com gradiente) e faíscas (círculos); distâncias em larguras de tela; [alpha] apaga tudo. */
fun DrawScope.drawFireworks(
    rockets: List<FireworkRocket>,
    sparks: List<FireworkSpark>,
    colors: FireworkColors,
    alpha: Float = 1f
) {
    val u = size.width
    rockets.forEach {
        val color = colors.of(it.tone).copy(alpha = alpha)
        val top = Offset(it.x * u, it.y * u)
        val bottom = Offset(it.x * u, (it.y + FireworksTuning.TAIL_LENGTH) * u)
        drawLine(
            brush = Brush.verticalGradient(
                listOf(color, Color.Transparent),
                startY = top.y,
                endY = bottom.y
            ),
            start = top,
            end = bottom,
            strokeWidth = ROCKET_STROKE_DP.dp.toPx()
        )
    }
    sparks.forEach {
        drawCircle(
            color = colors.of(it.tone).copy(alpha = minOf(it.life, 1f) * alpha),
            radius = it.radius * u,
            center = Offset(it.x * u, it.y * u)
        )
    }
}

@Composable
@ReadOnlyComposable
private fun aureoleFireworkColors() = FireworkColors(
    primary = AureoleDS.colors.onSurfaceHigh,
    secondary = AureoleDS.colors.onSurfaceMedium,
    white = AureoleDS.colors.onSurfaceHigh
)

/** Show de fogos em tela cheia: anda sozinho e para quando acaba. [onBurst] é chamado a cada estouro. */
@Composable
fun AureoleFireworks(modifier: Modifier = Modifier, onBurst: () -> Unit = {}) {
    var state by remember { mutableStateOf(fireworksShow()) }
    var aspect by remember { mutableStateOf(1f) }
    val currentOnBurst by rememberUpdatedState(onBurst)
    val random = remember { Random.Default }
    LaunchedEffect(Unit) {
        var last = withFrameNanos { it }
        while (!state.isIdle) {
            val now = withFrameNanos { it }
            val step = state.step((now - last) / NANOS_PER_MS, aspect, random)
            state = step.fireworks
            repeat(step.bursts) { currentOnBurst() }
            last = now
        }
    }
    val colors = aureoleFireworkColors()
    Canvas(modifier = modifier.fillMaxSize()) {
        aspect = size.height / size.width
        drawFireworks(state.rockets, state.sparks, colors)
    }
}

@Composable
internal fun AureoleFireworksFrame(state: FireworksState, modifier: Modifier = Modifier) {
    val colors = aureoleFireworkColors()
    Canvas(modifier = modifier.fillMaxSize()) { drawFireworks(state.rockets, state.sparks, colors) }
}

@AureolePreview
@Composable
private fun AureoleFireworksPreview() {
    AureoleLauncherTheme {
        AureoleFireworksFrame(previewFireworks())
    }
}

private fun previewFireworks(): FireworksState = FireworksState(
    rockets = listOf(FireworkRocket(PREVIEW_X, PREVIEW_Y, PREVIEW_TARGET, -1f, FireworkTone.PRIMARY)),
    sparks = List(PREVIEW_SPARKS) {
        val angle = it * PREVIEW_ANGLE_STEP
        FireworkSpark(
            x = 0.6f + cos(angle) * PREVIEW_RING,
            y = 0.5f + sin(angle) * PREVIEW_RING,
            vx = 0f,
            vy = 0f,
            life = 1f,
            radius = 0.006f,
            tone = FireworkTone.entries[it % FireworkTone.entries.size]
        )
    }
)

private const val PREVIEW_SPARKS = 34
private const val PREVIEW_ANGLE_STEP = 0.185f
private const val PREVIEW_RING = 0.2f
private const val PREVIEW_X = 0.3f
private const val PREVIEW_Y = 0.6f
private const val PREVIEW_TARGET = 0.4f
