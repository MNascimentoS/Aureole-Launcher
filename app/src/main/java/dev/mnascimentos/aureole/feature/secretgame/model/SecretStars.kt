package dev.mnascimentos.aureole.feature.secretgame.model

import kotlin.math.sin
import kotlin.random.Random

/**
 * Estrelas de fundo: acendem quando as molduras dos botões começam a aparecer, piscam na intimidação e na
 * contagem e somem no início do jogo, antes das linhas de velocidade. Distâncias em larguras de tela.
 */
object SecretStarsTuning {
    const val COUNT = 46
    const val IN_MS = 700L
    const val OUT_MS = 350L
    const val STREAKS_IN_MS = 450L
    const val TOP = 0.11f
    const val ABOVE_BUTTONS = 0.19f
    const val MIN_RADIUS = 0.0017f
    const val EXTRA_RADIUS = 0.0036f
    const val MIN_SPEED = 1.2f
    const val EXTRA_SPEED = 2.6f
    const val TWINKLE_FLOOR = 0.45f
    const val ALPHA = 0.85f
    const val GLOW_FROM_RADIUS = 0.0042f
    const val GLOW_SCALE = 3f
    const val GLOW_ALPHA = 0.18f
}

data class SecretStar(val x: Float, val y: Float, val radius: Float, val speed: Float, val phase: Float)

/** Estrela pronta para desenhar; [glowAlpha] > 0 só nas maiores (halo de [SecretStarsTuning.GLOW_SCALE] × raio). */
data class SecretStarSprite(val x: Float, val y: Float, val radius: Float, val alpha: Float, val glowAlpha: Float)

fun newSecretStars(aspect: Float, random: Random): List<SecretStar> {
    val bottom = aspect - SecretGameTuning.BUTTON_BOTTOM_OFFSET - SecretStarsTuning.ABOVE_BUTTONS
    return List(SecretStarsTuning.COUNT) {
        SecretStar(
            x = random.nextFloat(),
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
    SecretGamePhase.PLAY -> 1f - progress(playMs, 0L, SecretStarsTuning.OUT_MS)
    else -> 0f
}

/** Linhas de velocidade entram só depois que as estrelas somem. */
fun SecretGameState.streaksAlpha(): Float = when (phase) {
    SecretGamePhase.PLAY -> progress(playMs, SecretStarsTuning.OUT_MS, SecretStarsTuning.STREAKS_IN_MS)
    SecretGamePhase.PAUSED -> 1f
    else -> 0f
}

fun SecretGameState.starSprites(): List<SecretStarSprite> {
    val global = starsAlpha()
    if (global <= 0f) return emptyList()
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

/** Mesmo instante em que as molduras começam a se desenhar. */
private fun starsFromMs(): Long =
    SecretGameTuning.HIDE_MS + SecretGameTuning.TO_CENTER_MS + SecretGameTuning.MORPH_MS +
        (SecretGameTuning.DESCEND_MS * SecretGameTuning.FRAMES_LEAD).toLong()

private const val HALF = 0.5f
