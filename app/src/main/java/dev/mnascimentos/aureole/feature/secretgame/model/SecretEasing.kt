package dev.mnascimentos.aureole.feature.secretgame.model

import kotlin.math.exp
import kotlin.math.pow
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

private const val HALF = 0.5f
private const val CUBIC_IN = 4f
private const val CUBIC = 3f
private const val TWO = 2f

fun clamp01(value: Float): Float = value.coerceIn(0f, 1f)

fun lerp(from: Float, to: Float, fraction: Float): Float = from + (to - from) * fraction

/** Progresso 0..1 de [elapsedMs] dentro da janela que começa em [fromMs] e dura [durationMs]. */
fun progress(elapsedMs: Long, fromMs: Long, durationMs: Long): Float =
    clamp01((elapsedMs - fromMs).toFloat() / durationMs)

fun easeInOutCubic(value: Float): Float {
    val v = clamp01(value)
    return if (v < HALF) CUBIC_IN * v * v * v else 1f - (-TWO * v + TWO).pow(CUBIC) / TWO
}

fun easeOutCubic(value: Float): Float = 1f - (1f - clamp01(value)).pow(CUBIC)

/** Fração do caminho até a faixa feita em [seconds] (~90 ms para chegar). */
fun secretShipFollow(seconds: Float): Float = 1f - exp(-T.SHIP_FOLLOW_RATE * seconds)

fun secretTiltKeep(seconds: Float): Float = exp(-T.TILT_DECAY_RATE * seconds)
