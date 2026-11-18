package dev.mnascimentos.aureole.feature.secretgame.model

import kotlin.math.pow
import kotlin.random.Random
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

/** Regras e timeline do jogo secreto, sem Android. A UI só chama estas funções e desenha [toFrame]. */
class SecretGameEngine(private val random: Random = Random.Default) {

    fun start(layout: SecretGameLayout, bestScore: Int): SecretGameState =
        SecretGameState(layout = layout, bestScore = bestScore, streaks = newStreaks(layout.aspect, random))

    fun step(state: SecretGameState, deltaMs: Long): SecretGameStep {
        if (state.phase == SecretGamePhase.PAUSED || state.phase == SecretGamePhase.DONE) return SecretGameStep(state)
        val dt = deltaMs.coerceIn(0L, T.MAX_STEP_MS)
        val moved = state.advanceMotion(dt, random)
        return when (moved.phase) {
            SecretGamePhase.ENTER -> SecretGameStep(moved.stepEnter())
            SecretGamePhase.INTIMIDATE -> SecretGameStep(moved.stepIntimidate())
            SecretGamePhase.COUNTDOWN -> SecretGameStep(moved.stepCountdown())
            SecretGamePhase.PLAY -> moved.stepPlay(dt, random)
            SecretGamePhase.EXPLODE -> SecretGameStep(moved.stepExplode(random))
            SecretGamePhase.RESULT -> SecretGameStep(moved.stepResult())
            SecretGamePhase.EXIT -> SecretGameStep(moved.stepExit())
            SecretGamePhase.PAUSED, SecretGamePhase.DONE -> SecretGameStep(moved)
        }
    }

    /** [direction] -1 = esquerda, 1 = direita. Fora do jogo, ou na borda, não faz nada. */
    fun move(state: SecretGameState, direction: Int): SecretGameStep {
        val target = (state.lane + direction).coerceIn(0, T.LANE_COUNT - 1)
        if (!state.acceptsInput || target == state.lane) return SecretGameStep(state)
        val moved = state.copy(lane = target, tilt = direction * T.TILT_ON_MOVE)
        return SecretGameStep(moved, listOf(SecretGameEvent.LaneTick))
    }

    /** Back: jogando pausa; pausado sai salvando o recorde; nas animações é consumido sem efeito. */
    fun onBack(state: SecretGameState): SecretGameStep = when (state.phase) {
        SecretGamePhase.PLAY -> SecretGameStep(state.to(SecretGamePhase.PAUSED))
        SecretGamePhase.PAUSED -> SecretGameStep(
            state.finishRun().copy(faces = emptyList()).to(SecretGamePhase.EXIT),
            listOf(SecretGameEvent.GameOver(state.score)),
        )
        else -> SecretGameStep(state)
    }

    /** Toque na tela: pausado retoma. */
    fun onTap(state: SecretGameState): SecretGameState =
        if (state.phase == SecretGamePhase.PAUSED) state.to(SecretGamePhase.PLAY) else state

    /** App foi para o fundo (ON_PAUSE): jogando pausa. */
    fun onLifecyclePause(state: SecretGameState): SecretGameState =
        if (state.phase == SecretGamePhase.PLAY) state.to(SecretGamePhase.PAUSED) else state
}

internal fun SecretGameState.to(phase: SecretGamePhase): SecretGameState = copy(phase = phase, phaseMs = 0L)

internal fun SecretGameState.finishRun(): SecretGameState {
    val record = score > bestScore
    return copy(isNewRecord = record, bestScore = maxOf(score, bestScore))
}

private fun SecretGameState.advanceMotion(dt: Long, random: Random): SecretGameState {
    val seconds = dt / MS_PER_SECOND
    val follow = 1f - T.LANE_FOLLOW_REST.pow(seconds)
    return copy(
        phaseMs = phaseMs + dt,
        clockMs = clockMs + dt,
        tilt = tilt * T.TILT_DECAY_REST.pow(seconds),
        shipX = lerp(shipX, laneX(lane), follow),
        particles = particles.advance(seconds),
    ).maybeSparkle(seconds, random)
}

private fun SecretGameState.stepEnter(): SecretGameState {
    if (phaseMs < T.ENTER_TOTAL_MS) return this
    val first = listOf(
        SecretFace(0, 0, T.INTIMIDATE_FROM_Y, SecretFaceKind.CREATOR, wobble = 0f),
        SecretFace(1, T.LANE_COUNT - 1, T.INTIMIDATE_FROM_Y, SecretFaceKind.DREAMER, wobble = FACE_WOBBLE_OFFSET),
    )
    return copy(faces = first, nextFaceId = first.size).to(SecretGamePhase.INTIMIDATE)
}

private fun SecretGameState.stepIntimidate(): SecretGameState {
    val y = lerp(T.INTIMIDATE_FROM_Y, T.INTIMIDATE_TO_Y, easeOutCubic(phaseMs.toFloat() / T.INTIMIDATE_MS))
    val placed = copy(faces = faces.map { it.copy(y = y) })
    return if (phaseMs >= T.INTIMIDATE_MS) placed.to(SecretGamePhase.COUNTDOWN) else placed
}

private fun SecretGameState.stepCountdown(): SecretGameState =
    if (phaseMs >= T.COUNTDOWN_MS) copy(spawnCooldownMs = T.FIRST_SPAWN_MS).to(SecretGamePhase.PLAY) else this

private fun SecretGameState.stepExplode(random: Random): SecretGameState {
    val boomed = if (!hasExploded && phaseMs >= T.BOOM_AT_MS) explode(random) else this
    return if (phaseMs >= T.EXPLODE_TOTAL_MS) boomed.copy(faces = emptyList()).to(SecretGamePhase.RESULT) else boomed
}

private fun SecretGameState.stepResult(): SecretGameState =
    if (phaseMs >= T.RESULT_TOTAL_MS) to(SecretGamePhase.EXIT) else this

private fun SecretGameState.stepExit(): SecretGameState =
    if (phaseMs >= T.EXIT_MS) copy(particles = emptyList()).to(SecretGamePhase.DONE) else this

internal const val MS_PER_SECOND = 1000f
private const val FACE_WOBBLE_OFFSET = 3f
