package dev.mnascimentos.aureole.feature.secretgame.model

import dev.mnascimentos.aureole.composable.fireworks.model.fireworksShow
import kotlin.math.PI
import kotlin.random.Random
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

internal fun SecretGameState.stepPlay(dt: Long, random: Random): SecretGameStep {
    val level = secretLevel(score)
    val fall = secretFallSpeed(level) * layout.aspect * (dt / MS_PER_SECOND)
    val fallen = faces.map { it.copy(y = it.y + fall) }
    val kept = fallen.filter { it.y < layout.aspect + T.PASS_MARGIN }
    val newScore = score + (fallen.size - kept.size) * T.POINTS_PER_FACE
    val levelUp = secretLevel(newScore) > level
    val scored = copy(
        playMs = playMs + dt,
        faces = kept,
        score = newScore,
        levelFlashMs = if (levelUp) T.LEVEL_FLASH_MS else levelFlashMs,
        streaks = streaks.map { it.fall(fall * T.STREAK_SPEED, layout, random) },
        spawnCooldownMs = spawnCooldownMs - dt,
    ).spawnIfDue(random)
    val levelEvents = if (levelUp) listOf(SecretGameEvent.LevelUp(secretLevel(newScore))) else emptyList()
    val hit = scored.faces.firstOrNull { scored.hits(it) } ?: return SecretGameStep(scored, levelEvents)
    val crashed = scored.finishRun().copy(crashedFaceId = hit.id).to(SecretGamePhase.EXPLODE)
    return SecretGameStep(
        if (crashed.isNewRecord) crashed.copy(fireworks = fireworksShow()) else crashed,
        levelEvents + listOf(SecretGameEvent.Crash, SecretGameEvent.GameOver(scored.score)),
    )
}

internal fun SecretGameState.hits(face: SecretFace): Boolean {
    val shipHeight = T.SHIP_WIDTH * T.SHIP_ASPECT
    val reach = T.FACE_SIZE * T.FACE_HIT
    return face.lane == lane &&
        face.y + reach > shipY - shipHeight * T.SHIP_HIT_TOP &&
        face.y - reach < shipY + shipHeight * T.SHIP_HIT_BOTTOM
}

private fun SecretGameState.spawnIfDue(random: Random): SecretGameState {
    if (spawnCooldownMs > 0f) return this
    val level = secretLevel(score)
    val wave = nextSecretWave(lastSafeLane, level, random)
    val spawned = wave.blocked.mapIndexed { i, lane ->
        val kind = if (random.nextBoolean()) SecretFaceKind.CREATOR else SecretFaceKind.DREAMER
        SecretFace(nextFaceId + i, lane, -T.FACE_SIZE, kind, wobble = random.nextFloat() * FULL_TURN)
    }
    return copy(
        faces = faces + spawned,
        nextFaceId = nextFaceId + spawned.size,
        lastSafeLane = wave.safeLane,
        spawnCooldownMs = secretSpawnInterval(level),
    )
}

internal fun SecretGameState.explode(random: Random): SecretGameState {
    val crashed = faces.firstOrNull { it.id == crashedFaceId }
    val shipBits = burst(SecretBurst(shipX, shipY, SecretParticleKind.SHIP, T.SHIP_PARTICLES, T.SHIP_BURST), random)
    val faceBits = crashed?.let {
        burst(SecretBurst(laneX(it.lane), it.y, SecretParticleKind.FACE, T.FACE_PARTICLES, T.FACE_BURST), random)
    }
    return copy(hasExploded = true, particles = particles + shipBits + faceBits.orEmpty())
}

internal const val FULL_TURN = (2 * PI).toFloat()
