package dev.mnascimentos.aureole.feature.secretgame.model

import dev.mnascimentos.aureole.composable.fireworks.model.FireworksState
import dev.mnascimentos.aureole.feature.secretgame.audio.SecretSoundPlayer
enum class SecretGamePhase { ENTER, INTIMIDATE, COUNTDOWN, PLAY, PAUSED, EXPLODE, RESULT, EXIT, DONE }

enum class SecretFaceKind { CREATOR, DREAMER }

enum class SecretParticleKind { SHIP, FACE }

data class SecretFace(
    val id: Int,
    val lane: Int,
    val y: Float,
    val kind: SecretFaceKind,
    val wobble: Float,
)

data class SecretParticle(
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val life: Float,
    val radius: Float,
    val kind: SecretParticleKind,
)

data class SecretStreak(val x: Float, val y: Float, val length: Float)

/** Medidas da tela em larguras: [aspect] = altura / largura; o logo é o da tela de Settings. */
data class SecretGameLayout(
    val aspect: Float,
    val logoX: Float,
    val logoY: Float,
    val logoWidth: Float,
)

data class SecretGameState(
    val layout: SecretGameLayout,
    val phase: SecretGamePhase = SecretGamePhase.ENTER,
    val phaseMs: Long = 0L,
    val clockMs: Long = 0L,
    val playMs: Long = 0L,
    val lane: Int = 1,
    val shipX: Float = laneX(1),
    val tilt: Float = 0f,
    val faces: List<SecretFace> = emptyList(),
    val particles: List<SecretParticle> = emptyList(),
    val streaks: List<SecretStreak> = emptyList(),
    val score: Int = 0,
    val bestScore: Int = 0,
    val isNewRecord: Boolean = false,
    val spawnCooldownMs: Float = SecretGameTuning.FIRST_SPAWN_MS,
    val nextFaceId: Int = 0,
    val crashedFaceId: Int? = null,
    val hasExploded: Boolean = false,
    val lastSafeLane: Int = 1,
    val levelFlashMs: Long = 0L,
    val fireworks: FireworksState = FireworksState(),
) {
    val shipY: Float get() = layout.aspect - SecretGameTuning.SHIP_BOTTOM_OFFSET
    val isRunning: Boolean get() = phase != SecretGamePhase.DONE
    val acceptsInput: Boolean get() = phase == SecretGamePhase.PLAY
}

sealed interface SecretGameEvent {
    data object LaneTick : SecretGameEvent

    data object Crash : SecretGameEvent

    data class LevelUp(val level: Int) : SecretGameEvent

    data object FireworkBurst : SecretGameEvent

    /** Mudou o número da contagem (3, 2, 1, VAI). */
    data class CountdownTick(val label: SecretCountdownLabel) : SecretGameEvent

    /** Entrou na tela de pontos: recorde ou "mais sorte na próxima". */
    data class ResultShown(val isNewRecord: Boolean) : SecretGameEvent

    /** Fim da partida: salvar [score] como recorde se for maior. */
    data class GameOver(val score: Int) : SecretGameEvent
}

data class SecretGameStep(
    val state: SecretGameState,
    val events: List<SecretGameEvent> = emptyList(),
)

data class SecretTapResult(val counter: SecretTapCounter, val triggered: Boolean)

/** Conta toques seguidos no "Muito obrigado!"; o 5º dentro da janela abre o jogo. */
data class SecretTapCounter(val count: Int = 0, val lastTapMs: Long = NO_TAP) {
    fun register(nowMs: Long): SecretTapResult {
        val inWindow = lastTapMs != NO_TAP && nowMs - lastTapMs <= SecretGameTuning.TAP_WINDOW_MS
        val next = if (inWindow) count + 1 else 1
        return if (next >= SecretGameTuning.TAPS_TO_OPEN) {
            SecretTapResult(SecretTapCounter(), triggered = true)
        } else {
            SecretTapResult(SecretTapCounter(next, nowMs), triggered = false)
        }
    }

    private companion object {
        const val NO_TAP = -1L
    }
}

/** Centro da faixa [lane] em larguras de tela: 1/6, 1/2 e 5/6. */
fun laneX(lane: Int): Float = (lane + LANE_CENTER) / SecretGameTuning.LANE_COUNT

private const val LANE_CENTER = 0.5f

/** Where and how a burst of particles starts: [count] of [kind] from (x, y) with [power]. */
internal data class SecretBurst(
    val x: Float,
    val y: Float,
    val kind: SecretParticleKind,
    val count: Int,
    val power: Float
)

/** What Settings tells the game: the 5th tap opened it, the sound button of the pause was pressed. */
data class SecretGameActions(val onOpened: () -> Unit = {}, val onSoundToggled: () -> Unit = {})

/** The sound of the game: the [player], whether it is [isOn] (saved) and the [onToggle] of the pause button. */
data class SecretGameSound(val player: SecretSoundPlayer, val isOn: Boolean, val onToggle: () -> Unit)
