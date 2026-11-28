package dev.mnascimentos.aureole.feature.secretgame.model

import kotlin.math.pow
import kotlin.math.sin
import kotlin.random.Random

/**
 * Estrelas de fundo: acendem quando as molduras dos botões começam a aparecer, piscam na intimidação e na
 * contagem e, no "VAI", aceleram para baixo e se esticam até virar as linhas de velocidade (warp).
 * Distâncias em larguras de tela.
 */
object SecretStarsTuning {
    const val COUNT = 46
    const val IN_MS = 700L
    const val WARP_MS = 1600L
    const val WARP_ALPHA = 0.45f
    const val WARP_DISTANCE_OF_ASPECT = 1.25f
    const val WARP_POWER = 2.2f
    const val WARP_NEAR_MIN = 0.7f
    const val WARP_NEAR_SPAN = 0.6f
    const val WARP_TAIL = 0.45f
    const val WARP_TAIL_MIN = 0.0056f
    const val WARP_TAIL_MAX = 0.42f
    const val WARP_DIM = 0.45f
    const val WARP_FADE_FROM = 0.8f
    const val WARP_STROKE = 1.4f
    const val STREAKS_FROM = 0.45f
    const val TOP = 0.11f
    const val ABOVE_BUTTONS = 0.19f
    const val MIN_RADIUS = 0.0017f
    const val EXTRA_RADIUS = 0.0036f
    const val MIN_SPEED = 1.2f
    const val EXTRA_SPEED = 2.6f
    const val TWINKLE_FLOOR = 0.45f
    const val ALPHA = 0.55f
    const val GLOW_FROM_RADIUS = 0.0042f
    const val GLOW_SCALE = 3f
    const val GLOW_ALPHA = 0.12f
}

data class SecretStar(val x: Float, val y: Float, val radius: Float, val speed: Float, val phase: Float)

/**
 * Estrela pronta para desenhar; [glowAlpha] > 0 só nas maiores (halo de [SecretStarsTuning.GLOW_SCALE] × raio).
 * No warp, [tail] > 0: risco de (x, y − tail) até (x, y), espessura `radius × WARP_STROKE`, gradiente do
 * transparente (topo) à cor (ponta).
 */
data class SecretStarSprite(
    val x: Float,
    val y: Float,
    val radius: Float,
    val alpha: Float,
    val glowAlpha: Float,
    val tail: Float = 0f,
)

/** Estrelas da arena e, com [sideSpan] > 0, também das laterais (mesma densidade, com teto). */
fun newSecretStars(aspect: Float, random: Random, sideSpan: Float = 0f): List<SecretStar> {
    val bottom = aspect - SecretGameTuning.BUTTON_BOTTOM_OFFSET - SecretStarsTuning.ABOVE_BUTTONS
    return List(spreadCount(SecretStarsTuning.COUNT, sideSpan)) {
        SecretStar(
            x = spreadX(random, sideSpan),
            y = SecretStarsTuning.TOP + random.nextFloat() * (bottom - SecretStarsTuning.TOP),
            radius = SecretStarsTuning.MIN_RADIUS + random.nextFloat() * SecretStarsTuning.EXTRA_RADIUS,
            speed = SecretStarsTuning.MIN_SPEED + random.nextFloat() * SecretStarsTuning.EXTRA_SPEED,
            phase = random.nextFloat() * FULL_TURN,
        )
    }
}

/** Brilho geral das estrelas na fase atual (0 = apagadas). */
fun SecretGameState.starsAlpha(): Float = when (phase) {
    SecretGamePhase.ENTER -> progress(phaseMs, starsFromMs(), SecretStarsTuning.IN_MS)
    SecretGamePhase.INTIMIDATE, SecretGamePhase.COUNTDOWN -> 1f
    SecretGamePhase.PLAY, SecretGamePhase.PAUSED -> if (warpProgress() < 1f) 1f else 0f
    else -> 0f
}

/** Progresso do warp (0..1) no começo do jogo; para junto com a pausa. */
fun SecretGameState.warpProgress(): Float = progress(playMs, 0L, SecretStarsTuning.WARP_MS)

/** Linhas de velocidade entram no meio do warp, quando as estrelas já viraram riscos. */
fun SecretGameState.streaksAlpha(): Float = when (phase) {
    SecretGamePhase.PLAY, SecretGamePhase.PAUSED -> {
        val from = (SecretStarsTuning.WARP_MS * SecretStarsTuning.STREAKS_FROM).toLong()
        progress(playMs, from, SecretStarsTuning.WARP_MS - from)
    }
    else -> 0f
}

fun SecretGameState.starSprites(): List<SecretStarSprite> {
    val global = starsAlpha()
    val inGame = phase == SecretGamePhase.PLAY || phase == SecretGamePhase.PAUSED
    return when {
        global <= 0f -> emptyList()
        inGame -> warpSprites(warpProgress())
        else -> twinklingSprites(global)
    }
}

private fun SecretGameState.twinklingSprites(global: Float): List<SecretStarSprite> {
    val seconds = clockMs / MS_PER_SECOND
    return stars.map {
        val twinkle = SecretStarsTuning.TWINKLE_FLOOR +
            (1f - SecretStarsTuning.TWINKLE_FLOOR) * (HALF + HALF * sin(seconds * it.speed + it.phase))
        val alpha = global * twinkle * SecretStarsTuning.ALPHA
        val isBig = it.radius > SecretStarsTuning.GLOW_FROM_RADIUS
        val glow = if (isBig) global * twinkle * SecretStarsTuning.GLOW_ALPHA else 0f
        SecretStarSprite(it.x, it.y, it.radius, alpha, glow)
    }
}

private fun SecretGameState.warpSprites(warp: Float): List<SecretStarSprite> {
    val dim = 1f - SecretStarsTuning.WARP_DIM * warp
    val fade = if (warp < SecretStarsTuning.WARP_FADE_FROM) {
        dim
    } else {
        dim * (1f - (warp - SecretStarsTuning.WARP_FADE_FROM) / (1f - SecretStarsTuning.WARP_FADE_FROM))
    }
    val travel = layout.aspect * SecretStarsTuning.WARP_DISTANCE_OF_ASPECT * warp.pow(SecretStarsTuning.WARP_POWER)
    return stars.map {
        val near = (it.radius - SecretStarsTuning.MIN_RADIUS) / SecretStarsTuning.EXTRA_RADIUS
        val distance = travel * (SecretStarsTuning.WARP_NEAR_MIN + SecretStarsTuning.WARP_NEAR_SPAN * near)
        val stretch = distance * SecretStarsTuning.WARP_TAIL + SecretStarsTuning.WARP_TAIL_MIN
        val tail = minOf(stretch, SecretStarsTuning.WARP_TAIL_MAX)
        SecretStarSprite(
            it.x,
            it.y + distance,
            it.radius,
            fade * SecretStarsTuning.WARP_ALPHA,
            glowAlpha = 0f,
            tail = tail
        )
    }
}

/** Mesmo instante em que as molduras começam a se desenhar. */
private fun starsFromMs(): Long =
    SecretGameTuning.HIDE_MS + SecretGameTuning.TO_CENTER_MS + SecretGameTuning.MORPH_MS +
        (SecretGameTuning.DESCEND_MS * SecretGameTuning.FRAMES_LEAD).toLong()

private const val HALF = 0.5f
