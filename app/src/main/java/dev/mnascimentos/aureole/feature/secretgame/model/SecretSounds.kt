package dev.mnascimentos.aureole.feature.secretgame.model

import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

/** Efeitos sonoros do jogo; cada um é um `res/raw/secret_<nome>.ogg` (nome = [SecretSound.name] em minúsculas). */
enum class SecretSound { OPEN, LANE, COUNT, GO, LEVEL, CRASH, FIREWORK, RECORD, LOSE }

/** Som de cada evento do engine; null = evento sem som. `OPEN` toca no gatilho (5º toque), fora do engine. */
fun SecretGameEvent.sound(): SecretSound? = when (this) {
    SecretGameEvent.LaneTick -> SecretSound.LANE
    SecretGameEvent.Crash -> SecretSound.CRASH
    SecretGameEvent.FireworkBurst -> SecretSound.FIREWORK
    is SecretGameEvent.LevelUp -> SecretSound.LEVEL
    is SecretGameEvent.CountdownTick -> if (label == SecretCountdownLabel.GO) SecretSound.GO else SecretSound.COUNT
    is SecretGameEvent.ResultShown -> if (isNewRecord) SecretSound.RECORD else SecretSound.LOSE
    is SecretGameEvent.GameOver -> null
}

/** Sons a tocar para os [events] de um passo, respeitando o mudo salvo. */
fun secretSoundsFor(events: List<SecretGameEvent>, isSoundOn: Boolean): List<SecretSound> =
    if (isSoundOn) events.mapNotNull { it.sound() } else emptyList()

/**
 * Brilho do "Muito obrigado!" (só para quem nunca jogou): uma passada da esquerda para a direita e depois uma
 * estrela de 4 pontas pisca no fim do texto. Toca uma vez a cada abertura da tela raiz de Settings.
 */
object SecretShineTuning {
    const val SWEEP_DELAY_MS = 600
    const val SWEEP_MS = 1700
    const val STAR_DELAY_MS = 2100
    const val STAR_MS = 1400
    const val HIGHLIGHT_WIDTH = 0.32f
    const val STAR_PEAK_SCALE = 1.15f
    const val STAR_TURN_DEGREES = 110f
    const val STAR_PEAK_AT = 0.25f
    const val STAR_SETTLE_AT = 0.55f
    const val STAR_SETTLE_SCALE = 0.85f
}

fun shouldShowSecretShine(hasPlayed: Boolean): Boolean = !hasPlayed

/** Eventos de transição para som: cada número da contagem e a entrada na tela de pontos. */
internal fun phaseEvents(before: SecretGameState, after: SecretGameState): List<SecretGameEvent> = buildList {
    val label = after.countdownLabel()
    if (label != null && label != before.countdownLabel()) add(SecretGameEvent.CountdownTick(label))
    if (after.phase == SecretGamePhase.RESULT && before.phase != SecretGamePhase.RESULT) {
        add(SecretGameEvent.ResultShown(after.isNewRecord))
    }
}

/** Número da contagem em tela, ou null fora da contagem. */
fun SecretGameState.countdownLabel(): SecretCountdownLabel? {
    if (phase != SecretGamePhase.COUNTDOWN) return null
    val index = (phaseMs / T.COUNT_STEP_MS).toInt().coerceAtMost(T.COUNT_STEPS - 1)
    return SecretCountdownLabel.entries[index]
}
