package dev.mnascimentos.aureole.feature.secretgame.model

import dev.mnascimentos.aureole.composable.fireworks.model.FireworksState
import dev.mnascimentos.aureole.composable.fireworks.model.step
import kotlin.random.Random
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

/** Regras e timeline do jogo secreto, sem Android. A UI só chama estas funções e desenha [toFrame]. */
class SecretGameEngine(private val random: Random = Random.Default) {

    fun start(layout: SecretGameLayout, bestScore: Int): SecretGameState =
        SecretGameState(
            layout = layout,
            bestScore = bestScore,
            streaks = newStreaks(layout.aspect, random),
            stars = newSecretStars(layout.aspect, random),
        )

    fun step(state: SecretGameState, deltaMs: Long): SecretGameStep {
        if (state.phase == SecretGamePhase.PAUSED || state.phase == SecretGamePhase.DONE) return SecretGameStep(state)
        val dt = deltaMs.coerceIn(0L, T.MAX_STEP_MS)
        val (moved, bursts) = state.advanceMotion(dt).advanceFireworks(dt, random)
        val step = when (moved.phase) {
            SecretGamePhase.ENTER -> SecretGameStep(moved.stepEnter())
            SecretGamePhase.INTIMIDATE -> SecretGameStep(moved.stepIntimidate())
            SecretGamePhase.COUNTDOWN -> SecretGameStep(moved.stepCountdown())
            SecretGamePhase.PLAY -> moved.stepPlay(dt, random)
            SecretGamePhase.EXPLODE -> SecretGameStep(moved.stepExplode(random))
            SecretGamePhase.RESULT -> SecretGameStep(moved.stepResult())
            SecretGamePhase.EXIT -> SecretGameStep(moved.stepExit())
            SecretGamePhase.PAUSED, SecretGamePhase.DONE -> SecretGameStep(moved)
        }
        val extra = phaseEvents(state, step.state) + List(bursts) { SecretGameEvent.FireworkBurst }
        return if (extra.isEmpty()) step else step.copy(events = step.events + extra)
    }

    /** [direction] -1 = esquerda, 1 = direita. Fora do jogo não faz nada; na borda só anima o botão. */
    fun move(state: SecretGameState, direction: Int): SecretGameStep {
        if (!state.acceptsInput) return SecretGameStep(state)
        val pressed = if (direction < 0) state.copy(leftPressMs = 0L) else state.copy(rightPressMs = 0L)
        val target = (state.lane + direction).coerceIn(0, T.LANE_COUNT - 1)
        if (target == state.lane) return SecretGameStep(pressed)
        return SecretGameStep(pressed.copy(lane = target), listOf(SecretGameEvent.LaneTick))
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

private fun SecretGameState.advanceMotion(dt: Long): SecretGameState {
    val seconds = dt / MS_PER_SECOND
    val x = lerp(shipX, laneX(lane), secretShipFollow(seconds))
    val lean = (laneX(lane) - x) * T.LANE_COUNT * T.TILT_PER_LANE
    return copy(
        phaseMs = phaseMs + dt,
        clockMs = clockMs + dt,
        tilt = lerp(tilt, lean, secretTiltChase(seconds)),
        shipX = x,
        leftPressMs = (leftPressMs + dt).coerceAtMost(T.PRESS_IDLE_MS),
        rightPressMs = (rightPressMs + dt).coerceAtMost(T.PRESS_IDLE_MS),
        particles = particles.advance(seconds),
        levelFlashMs = (levelFlashMs - dt).coerceAtLeast(0L),
    )
}

/** Fogos andam no resultado e terminam de cair na saída. Devolve o estado e quantos estouraram. */
private fun SecretGameState.advanceFireworks(dt: Long, random: Random): Pair<SecretGameState, Int> {
    val active = phase == SecretGamePhase.RESULT || phase == SecretGamePhase.EXIT
    if (!active || fireworks.isIdle) return this to 0
    val step = fireworks.step(dt, layout.aspect, random)
    return copy(fireworks = step.fireworks) to step.bursts
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
    if (phaseMs < T.EXIT_MS) {
        this
    } else {
        copy(particles = emptyList(), fireworks = FireworksState()).to(SecretGamePhase.DONE)
    }

internal const val MS_PER_SECOND = 1000f
private const val FACE_WOBBLE_OFFSET = 3f
