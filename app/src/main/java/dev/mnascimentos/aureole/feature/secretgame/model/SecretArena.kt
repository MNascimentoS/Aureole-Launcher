package dev.mnascimentos.aureole.feature.secretgame.model

import dev.mnascimentos.aureole.composable.fireworks.model.FireworksState
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

/** Onde o jogo acontece dentro da janela, em px. Fora dela só ficam o fundo e, se couberem, os controles. */
data class SecretArena(val left: Float, val top: Float, val width: Float, val height: Float) {
    val aspect: Float get() = height / width
}

/**
 * Janela com proporção de celular em retrato → a arena é a janela inteira (como sempre foi).
 * Janela mais larga (dobrável aberto, tablet, paisagem) → coluna centrada com [T.REFERENCE_ASPECT].
 */
fun secretArenaOf(windowWidth: Float, windowHeight: Float): SecretArena {
    if (windowHeight / windowWidth >= T.PHONE_MIN_ASPECT) return SecretArena(0f, 0f, windowWidth, windowHeight)
    val width = windowHeight / T.REFERENCE_ASPECT
    return SecretArena((windowWidth - width) / 2f, 0f, width, windowHeight)
}

/**
 * O layout do jogo na [arena]; o logo de Settings vem em px da janela e vira coordenada da arena. As setas vão
 * para as laterais quando cada uma tem pelo menos [T.SIDE_CONTROL_MIN_DP] ([density] px por dp).
 */
fun secretLayoutOf(arena: SecretArena, logo: SecretLogoPlacement, density: Float = 1f): SecretGameLayout =
    SecretGameLayout(
        aspect = arena.aspect,
        logoX = (logo.center.x - arena.left) / arena.width,
        logoY = (logo.center.y - arena.top) / arena.width,
        logoWidth = logo.widthPx / arena.width,
        sideSpan = arena.left / arena.width,
        buttonsOnSides = arena.left >= T.SIDE_CONTROL_MIN_DP * density,
    )

/**
 * O mesmo jogo num [layout] novo (a janela mudou no meio da partida). Com outro aspect, as alturas são
 * reescaladas para a mesma fração da arena; com outras laterais, o fundo se espalha de uma lateral à outra.
 * Sem mudança de medida, só o layout é trocado.
 */
fun SecretGameState.rescaledTo(layout: SecretGameLayout): SecretGameState {
    val ratio = layout.aspect / this.layout.aspect
    val from = this.layout.sideSpan
    val to = layout.sideSpan
    if (ratio == 1f && from == to) return copy(layout = layout)
    val spread = { x: Float -> -to + (x + from) / (1f + 2f * from) * (1f + 2f * to) }
    return copy(
        layout = layout,
        faces = faces.map { it.copy(y = it.y * ratio) },
        particles = particles.map { it.copy(y = it.y * ratio, vy = it.vy * ratio) },
        streaks = streaks.map { it.copy(x = spread(it.x), y = it.y * ratio) },
        stars = stars.map { it.copy(x = spread(it.x), y = it.y * ratio) },
        fireworks = fireworks.rescaled(ratio),
    )
}

private fun FireworksState.rescaled(ratio: Float): FireworksState = copy(
    rockets = rockets.map { it.copy(y = it.y * ratio, targetY = it.targetY * ratio, vy = it.vy * ratio) },
    sparks = sparks.map { it.copy(y = it.y * ratio, vy = it.vy * ratio) },
)
