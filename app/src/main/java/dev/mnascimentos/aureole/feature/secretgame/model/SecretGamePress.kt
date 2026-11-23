package dev.mnascimentos.aureole.feature.secretgame.model

import kotlin.math.sin
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

/** Brilho do toque: 1 no toque, 0 depois de [T.PRESS_MS]. */
internal fun pressGlow(sinceMs: Long): Float = 1f - progress(sinceMs, 0L, T.PRESS_MS)

/** Aperto da moldura: 0 → [T.PRESS_SCALE] → 0 em [T.PRESS_SQUEEZE_MS]. */
internal fun pressSqueeze(sinceMs: Long): Float {
    if (sinceMs >= T.PRESS_SQUEEZE_MS) return 0f
    return T.PRESS_SCALE * sin(sinceMs.toFloat() / T.PRESS_SQUEEZE_MS * HALF_TURN)
}

private const val HALF_TURN = FULL_TURN / 2
