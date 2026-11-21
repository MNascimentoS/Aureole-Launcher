package dev.mnascimentos.aureole.feature.secretgame.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.mnascimentos.aureole.feature.secretgame.model.SecretShineTuning as S

/**
 * Progresso do brilho do "Muito obrigado!": [sweep] é a passada do realce e [star] a estrela, ambos em 0..1.
 * 0 e 1 significam "não aparece"; só entre os dois há desenho.
 */
class SecretShineState(sweep: Float = 0f, star: Float = 0f) {
    var sweep by mutableFloatStateOf(sweep)
    var star by mutableFloatStateOf(star)
    var isCancelled by mutableStateOf(false)
        private set

    /** Toque ou long press no texto: some na hora e não volta nesta abertura. */
    fun cancel() {
        isCancelled = true
        sweep = 0f
        star = 0f
    }
}

/** Escala da estrela em [progress] (0..1): 0 → pico → assenta → 0. */
fun secretStarScale(progress: Float): Float = when {
    progress <= 0f || progress >= 1f -> 0f
    progress < S.STAR_PEAK_AT -> S.STAR_PEAK_SCALE * progress / S.STAR_PEAK_AT
    progress < S.STAR_SETTLE_AT -> {
        val k = (progress - S.STAR_PEAK_AT) / (S.STAR_SETTLE_AT - S.STAR_PEAK_AT)
        S.STAR_PEAK_SCALE + (S.STAR_SETTLE_SCALE - S.STAR_PEAK_SCALE) * k
    }
    else -> S.STAR_SETTLE_SCALE * (1f - (progress - S.STAR_SETTLE_AT) / (1f - S.STAR_SETTLE_AT))
}
