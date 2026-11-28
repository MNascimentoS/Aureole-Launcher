package dev.mnascimentos.aureole.feature.secretgame.model

import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

internal fun List<SecretParticle>.advance(seconds: Float): List<SecretParticle> = mapNotNull {
    val life = it.life - seconds * T.PARTICLE_FADE
    if (life <= 0f) {
        null
    } else {
        val vy = it.vy + T.GRAVITY * seconds
        it.copy(x = it.x + it.vx * seconds, y = it.y + vy * seconds, vy = vy, life = life)
    }
}

internal fun newStreaks(aspect: Float, random: Random, sideSpan: Float = 0f): List<SecretStreak> =
    List(spreadCount(T.STREAK_COUNT, sideSpan)) {
        SecretStreak(spreadX(random, sideSpan), random.nextFloat() * aspect, streakLength(random))
    }

internal fun SecretStreak.fall(distance: Float, layout: SecretGameLayout, random: Random): SecretStreak {
    val y = y + distance
    return if (y > layout.aspect) SecretStreak(spreadX(random, layout.sideSpan), -length, length) else copy(y = y)
}

/** Quantos pontos de fundo cabem na arena mais as laterais, com a densidade da arena (teto em [T.MAX_SPREAD_FACTOR]). */
internal fun spreadCount(count: Int, sideSpan: Float): Int =
    (count * minOf(1f + 2f * sideSpan, T.MAX_SPREAD_FACTOR)).roundToInt()

/** x sorteado de uma lateral à outra: [-sideSpan, 1 + sideSpan] (0..1 no celular). */
internal fun spreadX(random: Random, sideSpan: Float): Float = -sideSpan + random.nextFloat() * (1f + 2f * sideSpan)

private fun streakLength(random: Random): Float = T.STREAK_MIN_LENGTH + random.nextFloat() * T.STREAK_EXTRA_LENGTH

internal fun burst(spec: SecretBurst, random: Random): List<SecretParticle> = List(spec.count) {
    val angle = random.nextFloat() * FULL_TURN
    val speed = (T.BURST_MIN + random.nextFloat()) * spec.power
    SecretParticle(
        x = spec.x,
        y = spec.y,
        vx = cos(angle) * speed,
        vy = sin(angle) * speed - T.PARTICLE_LIFT,
        life = 1f,
        radius = T.PARTICLE_MIN_RADIUS + random.nextFloat() * T.PARTICLE_EXTRA_RADIUS,
        kind = spec.kind,
    )
}
