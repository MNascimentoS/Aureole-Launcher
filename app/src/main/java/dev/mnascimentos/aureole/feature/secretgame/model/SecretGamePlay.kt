package dev.mnascimentos.aureole.feature.secretgame.model

import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/** Velocidade de queda em alturas de tela por segundo, após [seconds] de jogo. */
fun secretFallSpeed(seconds: Float): Float = minOf(T.SPEED_BASE + T.SPEED_GROWTH * seconds, T.SPEED_MAX)

/** Intervalo entre levas de rostos, em ms. */
fun secretSpawnInterval(seconds: Float): Float = maxOf(T.SPAWN_BASE_MS - T.SPAWN_GROWTH_MS * seconds, T.SPAWN_MIN_MS)

/** Chance de uma leva trazer 2 rostos. Nunca 3: sempre sobra uma faixa livre. */
fun secretDoubleChance(seconds: Float): Float {
    if (seconds < T.DOUBLE_FROM_S) return 0f
    return minOf(T.DOUBLE_BASE + T.DOUBLE_GROWTH * (seconds - T.DOUBLE_FROM_S), T.DOUBLE_MAX)
}

internal fun SecretGameState.stepPlay(dt: Long, random: Random): SecretGameStep {
    val playSeconds = (playMs + dt) / MS_PER_SECOND
    val fall = secretFallSpeed(playSeconds) * layout.aspect * (dt / MS_PER_SECOND)
    val fallen = faces.map { it.copy(y = it.y + fall) }
    val kept = fallen.filter { it.y < layout.aspect + T.PASS_MARGIN }
    val scored = copy(
        playMs = playMs + dt,
        faces = kept,
        score = score + (fallen.size - kept.size) * T.POINTS_PER_FACE,
        streaks = streaks.map { it.fall(fall * T.STREAK_SPEED, layout.aspect, random) },
        spawnCooldownMs = spawnCooldownMs - dt,
    ).spawnIfDue(playSeconds, random)
    val hit = scored.faces.firstOrNull { scored.hits(it) } ?: return SecretGameStep(scored)
    return SecretGameStep(
        scored.finishRun().copy(crashedFaceId = hit.id).to(SecretGamePhase.EXPLODE),
        listOf(SecretGameEvent.Crash, SecretGameEvent.GameOver(scored.score)),
    )
}

internal fun SecretGameState.hits(face: SecretFace): Boolean {
    val shipHeight = T.SHIP_WIDTH * T.SHIP_ASPECT
    val reach = T.FACE_SIZE * T.FACE_HIT
    return face.lane == lane &&
        face.y + reach > shipY - shipHeight * T.SHIP_HIT_TOP &&
        face.y - reach < shipY + shipHeight * T.SHIP_HIT_BOTTOM
}

private fun SecretGameState.spawnIfDue(seconds: Float, random: Random): SecretGameState {
    if (spawnCooldownMs > 0f) return this
    val count = if (random.nextFloat() < secretDoubleChance(seconds)) 2 else 1
    val lanes = (0 until T.LANE_COUNT).shuffled(random).take(count)
    val spawned = lanes.mapIndexed { i, lane ->
        val kind = if (random.nextBoolean()) SecretFaceKind.CREATOR else SecretFaceKind.DREAMER
        SecretFace(nextFaceId + i, lane, -T.FACE_SIZE, kind, wobble = random.nextFloat() * FULL_TURN)
    }
    return copy(
        faces = faces + spawned,
        nextFaceId = nextFaceId + count,
        spawnCooldownMs = secretSpawnInterval(seconds),
    )
}

internal fun SecretGameState.explode(random: Random): SecretGameState {
    val crashed = faces.firstOrNull { it.id == crashedFaceId }
    val shipBits = burst(shipX, shipY, T.SHIP_PARTICLES, T.SHIP_BURST, SecretParticleKind.SHIP, random)
    val faceBits = crashed?.let {
        burst(laneX(it.lane), it.y, T.FACE_PARTICLES, T.FACE_BURST, SecretParticleKind.FACE, random)
    }
    return copy(hasExploded = true, particles = particles + shipBits + faceBits.orEmpty())
}

/** Faíscas de comemoração enquanto o "Novo recorde!" está na tela. */
internal fun SecretGameState.maybeSparkle(seconds: Float, random: Random): SecretGameState {
    val showing = phase == SecretGamePhase.RESULT && isNewRecord && phaseMs < T.RESULT_IN_MS + T.RESULT_HOLD_MS
    if (!showing || random.nextFloat() >= T.SPARKS_PER_SECOND * seconds) return this
    val x = HALF_WIDTH + (random.nextFloat() - HALF) * T.SPARK_SPREAD
    val y = layout.aspect * T.CENTER_Y_FRACTION - T.SPARK_RISE * random.nextFloat()
    val kind = if (random.nextBoolean()) SecretParticleKind.SHIP else SecretParticleKind.FACE
    return copy(particles = particles + burst(x, y, 1, T.SPARK_BURST, kind, random))
}

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

private fun SecretStreak.fall(distance: Float, aspect: Float, random: Random): SecretStreak {
    val y = y + distance
    return if (y > aspect) SecretStreak(random.nextFloat(), -length, length) else copy(y = y)
}

private fun streakLength(random: Random): Float = T.STREAK_MIN_LENGTH + random.nextFloat() * T.STREAK_EXTRA_LENGTH

private fun burst(
    x: Float,
    y: Float,
    count: Int,
    power: Float,
    kind: SecretParticleKind,
    random: Random,
): List<SecretParticle> = List(count) {
    val angle = random.nextFloat() * FULL_TURN
    val speed = (T.BURST_MIN + random.nextFloat()) * power
    SecretParticle(
        x = x,
        y = y,
        vx = cos(angle) * speed,
        vy = sin(angle) * speed - T.PARTICLE_LIFT,
        life = 1f,
        radius = T.PARTICLE_MIN_RADIUS + random.nextFloat() * T.PARTICLE_EXTRA_RADIUS,
        kind = kind,
    )
}

internal const val FULL_TURN = (2 * PI).toFloat()
private const val HALF = 0.5f
private const val HALF_WIDTH = 0.5f
