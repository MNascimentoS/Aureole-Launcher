package dev.mnascimentos.aureole.composable.fireworks.model

import dev.mnascimentos.aureole.composable.fireworks.model.FireworksTuning as F
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

/**
 * Fogos de artifício (jogo secreto e fim do onboarding): foguetes sobem e estouram em anel.
 * Distâncias em larguras de tela (largura = 1f), tempos em ms.
 */
object FireworksTuning {
    const val ROCKETS = 6
    const val FIRST_MS = 500f
    const val GAP_MIN_MS = 260f
    const val GAP_EXTRA_MS = 240f
    const val X_MIN = 0.11f
    const val X_SPAN = 0.78f
    const val TARGET_MIN_Y = 0.25f
    const val TARGET_SPAN_OF_ASPECT = 0.55f
    const val LAUNCH_BELOW = 0.028f
    const val CLIMB_S = 0.42f
    const val TAIL_LENGTH = 0.128f
    const val RING_SPARKS = 34
    const val RING_JITTER = 0.12f
    const val RING_POWER_MIN = 0.33f
    const val RING_POWER_EXTRA = 0.14f
    const val RING_SPEED_MIN = 0.85f
    const val RING_SPEED_EXTRA = 0.3f
    const val RING_LIFE = 1.25f
    const val RING_RADIUS_MIN = 0.004f
    const val RING_RADIUS_EXTRA = 0.004f
    const val CORE_SPARKS = 10
    const val CORE_SPEED = 0.167f
    const val CORE_LIFE = 0.7f
    const val CORE_RADIUS = 0.0067f
    const val DRAG = 1.6f
    const val GRAVITY = 0.25f
    const val FADE = 1.15f
}

/** Cor de cada estouro: a UI mapeia para tokens do `AureoleDS` (sem hex). */
enum class FireworkTone { PRIMARY, SECONDARY, WHITE }

data class FireworkRocket(val x: Float, val y: Float, val targetY: Float, val vy: Float, val tone: FireworkTone)

data class FireworkSpark(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val life: Float,
    val radius: Float,
    val tone: FireworkTone,
)

data class FireworksState(
    val rockets: List<FireworkRocket> = emptyList(),
    val sparks: List<FireworkSpark> = emptyList(),
    val left: Int = 0,
    val cooldownMs: Float = 0f,
) {
    val isIdle: Boolean get() = left == 0 && rockets.isEmpty() && sparks.isEmpty()
}

data class FireworksStep(val fireworks: FireworksState, val bursts: Int)

/** Show completo: [FireworksTuning.ROCKETS] foguetes, o primeiro após [FireworksTuning.FIRST_MS]. */
fun fireworksShow(): FireworksState =
    FireworksState(left = F.ROCKETS, cooldownMs = F.FIRST_MS)

fun FireworksState.step(deltaMs: Long, aspect: Float, random: Random): FireworksStep {
    val seconds = deltaMs / MS_IN_SECOND
    val launched = launchIfDue(deltaMs, aspect, random)
    val moved = launched.rockets.map { it.copy(y = it.y + it.vy * seconds) }
    val (arrived, flying) = moved.partition { it.y <= it.targetY }
    val burst = arrived.flatMap { ring(it.x, it.targetY, it.tone, random) }
    return FireworksStep(
        launched.copy(rockets = flying, sparks = launched.sparks.advanceSparks(seconds) + burst),
        bursts = arrived.size,
    )
}

private fun FireworksState.launchIfDue(deltaMs: Long, aspect: Float, random: Random): FireworksState {
    val cooldown = cooldownMs - deltaMs
    if (left == 0 || cooldown > 0f) return copy(cooldownMs = cooldown)
    return copy(
        rockets = rockets + rocket(aspect, random),
        left = left - 1,
        cooldownMs = F.GAP_MIN_MS + random.nextFloat() * F.GAP_EXTRA_MS,
    )
}

private fun rocket(aspect: Float, random: Random): FireworkRocket {
    val startY = aspect + F.LAUNCH_BELOW
    val targetY = F.TARGET_MIN_Y +
        random.nextFloat() * aspect * F.TARGET_SPAN_OF_ASPECT
    return FireworkRocket(
        x = F.X_MIN + random.nextFloat() * F.X_SPAN,
        y = startY,
        targetY = targetY,
        vy = -(startY - targetY) / F.CLIMB_S,
        tone = FireworkTone.entries.random(random),
    )
}

private fun ring(x: Float, y: Float, tone: FireworkTone, random: Random): List<FireworkSpark> {
    val power = F.RING_POWER_MIN + random.nextFloat() * F.RING_POWER_EXTRA
    val ring = List(F.RING_SPARKS) { i ->
        val angle = i * FULL_TURN / F.RING_SPARKS + random.nextFloat() * F.RING_JITTER
        val speed = power * (F.RING_SPEED_MIN + random.nextFloat() * F.RING_SPEED_EXTRA)
        FireworkSpark(
            x, y, cos(angle) * speed, sin(angle) * speed, F.RING_LIFE,
            F.RING_RADIUS_MIN + random.nextFloat() * F.RING_RADIUS_EXTRA, tone,
        )
    }
    val core = List(F.CORE_SPARKS) {
        FireworkSpark(
            x, y,
            (random.nextFloat() - HALF) * F.CORE_SPEED,
            (random.nextFloat() - HALF) * F.CORE_SPEED,
            F.CORE_LIFE, F.CORE_RADIUS, FireworkTone.WHITE,
        )
    }
    return ring + core
}

fun List<FireworkSpark>.advanceSparks(seconds: Float): List<FireworkSpark> {
    val keep = exp(-F.DRAG * seconds)
    return mapNotNull {
        val life = it.life - seconds * F.FADE
        if (life <= 0f) return@mapNotNull null
        val vx = it.vx * keep
        val vy = it.vy * keep + F.GRAVITY * seconds
        it.copy(x = it.x + vx * seconds, y = it.y + vy * seconds, vx = vx, vy = vy, life = life)
    }
}

private const val MS_IN_SECOND = 1000f
private const val FULL_TURN = (2 * PI).toFloat()
private const val HALF = 0.5f
