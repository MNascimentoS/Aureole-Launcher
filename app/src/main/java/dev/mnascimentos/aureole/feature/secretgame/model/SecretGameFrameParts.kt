package dev.mnascimentos.aureole.feature.secretgame.model

import kotlin.math.sin
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

internal fun SecretGameState.flyingShip(shake: Float) =
    SecretSprite(shipX + shake, shipY + bob(), T.SHIP_WIDTH, sway() + tilt, 1f)

internal fun SecretGameState.faceSprite(face: SecretFace, alpha: Float): SecretFaceSprite {
    val hover = if (phase == SecretGamePhase.COUNTDOWN) {
        sin(phaseMs / T.FACE_HOVER_PERIOD_MS + face.wobble) * T.FACE_HOVER
    } else {
        0f
    }
    val swing = sin(clockMs / MS_PER_SECOND * T.FACE_SWING_FREQ + face.wobble) * T.FACE_SWING
    val sprite = SecretSprite(laneX(face.lane), face.y + hover, T.FACE_SIZE, swing, alpha)
    return SecretFaceSprite(face.kind, sprite, trail = faceTrail())
}

/** Rastro só com o rosto caindo; cresce com a velocidade do nível. */
internal fun SecretGameState.faceTrail(): Float {
    val falling = phase == SecretGamePhase.PLAY || phase == SecretGamePhase.PAUSED
    if (!falling) return 0f
    return T.FACE_SIZE * (T.TRAIL_BASE + secretFallSpeed(secretLevel(score)) * T.TRAIL_SPEED)
}

internal fun SecretGameState.countdownFrame(): SecretCountdownFrame {
    val index = (phaseMs / T.COUNT_STEP_MS).toInt().coerceAtMost(T.COUNT_STEPS - 1)
    val k = (phaseMs % T.COUNT_STEP_MS).toFloat() / T.COUNT_STEP_MS
    val alpha = if (k < T.COUNT_FADE_FROM) 1f else 1f - (k - T.COUNT_FADE_FROM) / (1f - T.COUNT_FADE_FROM)
    val scale = T.COUNT_SCALE_FROM - (T.COUNT_SCALE_FROM - 1f) * easeOutCubic(k * T.COUNT_SCALE_SPEED)
    return SecretCountdownFrame(SecretCountdownLabel.entries[index], alpha, scale)
}

internal fun SecretGameState.resultAlpha(): Float = when {
    phaseMs < T.RESULT_IN_MS -> progress(phaseMs, 0L, T.RESULT_IN_MS)
    phaseMs < T.RESULT_IN_MS + T.RESULT_HOLD_MS -> 1f
    else -> 1f - progress(phaseMs, T.RESULT_IN_MS + T.RESULT_HOLD_MS, T.RESULT_OUT_MS)
}

internal fun SecretGameState.bob(): Float = wave(T.BOB_PERIOD_S) * T.BOB_AMPLITUDE

internal fun SecretGameState.sway(): Float = wave(T.SWAY_PERIOD_S) * T.SWAY_AMPLITUDE

internal fun SecretGameState.wave(periodSeconds: Float): Float =
    sin(clockMs / MS_PER_SECOND * FULL_TURN / periodSeconds)

internal fun SecretGameState.flame(): Float {
    val s = clockMs / MS_PER_SECOND
    return T.FLAME_BASE + T.FLAME_FLICKER * sin(s * T.FLAME_FREQ_A) * sin(s * T.FLAME_FREQ_B)
}
