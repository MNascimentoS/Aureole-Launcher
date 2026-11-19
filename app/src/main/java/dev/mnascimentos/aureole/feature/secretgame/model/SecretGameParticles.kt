package dev.mnascimentos.aureole.feature.secretgame.model

import kotlin.math.cos
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

internal fun newStreaks(aspect: Float, random: Random): List<SecretStreak> = List(T.STREAK_COUNT) {
    SecretStreak(random.nextFloat(), random.nextFloat() * aspect, streakLength(random))
}

internal fun SecretStreak.fall(distance: Float, aspect: Float, random: Random): SecretStreak {
    val y = y + distance
    return if (y > aspect) SecretStreak(random.nextFloat(), -length, length) else copy(y = y)
}

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
