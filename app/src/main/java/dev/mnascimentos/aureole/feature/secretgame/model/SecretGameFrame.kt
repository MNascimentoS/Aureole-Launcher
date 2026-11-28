package dev.mnascimentos.aureole.feature.secretgame.model

import androidx.compose.ui.geometry.Offset
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkRocket
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkSpark
import kotlin.math.sin
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

/** Um sprite em larguras de tela: centro ([x], [y]), largura, rotação em radianos e alpha. */
data class SecretSprite(val x: Float, val y: Float, val width: Float, val rotation: Float, val alpha: Float)

/** [trail]: comprimento do rastro acima do rosto, em larguras; 0 = sem rastro (parado ou fora do jogo). */
data class SecretFaceSprite(val kind: SecretFaceKind, val sprite: SecretSprite, val trail: Float = 0f)

/** Brilho radial (troca logo → nave e explosão). */
data class SecretGlow(val x: Float, val y: Float, val radius: Float, val alpha: Float)

/**
 * [draw] desenha as molduras (0..1); [collapse] recolhe para os cantos na explosão (0..1);
 * [leftPress]/[rightPress]: efeito de toque, 1 no toque → 0 em [SecretGameTuning.PRESS_MS];
 * [leftSqueeze]/[rightSqueeze]: escala extra da moldura apertada (0 → [SecretGameTuning.PRESS_SCALE] → 0).
 */
data class SecretButtonsFrame(
    val draw: Float,
    val collapse: Float,
    val leftPress: Float = 0f,
    val rightPress: Float = 0f,
    val leftSqueeze: Float = 0f,
    val rightSqueeze: Float = 0f,
)

enum class SecretCountdownLabel { THREE, TWO, ONE, GO }

data class SecretCountdownFrame(val label: SecretCountdownLabel, val alpha: Float, val scale: Float)

data class SecretResultFrame(val score: Int, val bestScore: Int, val isNewRecord: Boolean, val alpha: Float)

/** Tudo que a tela desenha num quadro. Sem estado: só leitura de [SecretGameState]. */
data class SecretGameFrame(
    val settingsAlpha: Float,
    val settingsLogoVisible: Boolean,
    val logo: SecretSprite? = null,
    val ship: SecretSprite? = null,
    val flame: Float = 0f,
    val glow: SecretGlow? = null,
    val faces: List<SecretFaceSprite> = emptyList(),
    val buttons: SecretButtonsFrame? = null,
    val countdown: SecretCountdownFrame? = null,
    val streaks: List<SecretStreak> = emptyList(),
    val streaksAlpha: Float = 0f,
    val stars: List<SecretStarSprite> = emptyList(),
    val particles: List<SecretParticle> = emptyList(),
    val hudScore: Int? = null,
    val hudFlash: Float = 0f,
    val levelLabel: Int = 1,
    val rockets: List<FireworkRocket> = emptyList(),
    val sparks: List<FireworkSpark> = emptyList(),
    val fireworksAlpha: Float = 1f,
    val isPaused: Boolean = false,
    val result: SecretResultFrame? = null,
)

fun SecretGameState.toFrame(): SecretGameFrame {
    val base = SecretGameFrame(
        settingsAlpha = settingsAlpha(),
        settingsLogoVisible = phase == SecretGamePhase.EXIT || phase == SecretGamePhase.DONE,
        particles = particles,
        rockets = fireworks.rockets,
        sparks = fireworks.sparks,
        fireworksAlpha = fireworksAlpha(),
        stars = starSprites(),
        streaksAlpha = streaksAlpha(),
    )
    return when (phase) {
        SecretGamePhase.ENTER -> enterFrame(base)
        SecretGamePhase.INTIMIDATE,
        SecretGamePhase.COUNTDOWN,
        SecretGamePhase.PLAY,
        SecretGamePhase.PAUSED,
        -> arenaFrame(base)
        SecretGamePhase.EXPLODE -> explodeFrame(base)
        SecretGamePhase.RESULT -> base.copy(result = SecretResultFrame(score, bestScore, isNewRecord, resultAlpha()))
        SecretGamePhase.EXIT, SecretGamePhase.DONE -> base
    }
}

/** Na saída os fogos se apagam enquanto o Settings aparece (o inverso de [settingsAlpha]). */
private fun SecretGameState.fireworksAlpha(): Float =
    if (phase == SecretGamePhase.EXIT || phase == SecretGamePhase.DONE) 1f - settingsAlpha() else 1f

private fun SecretGameState.settingsAlpha(): Float = when (phase) {
    SecretGamePhase.ENTER -> 1f - progress(phaseMs, 0L, T.HIDE_MS)
    SecretGamePhase.EXIT -> progress(phaseMs, 0L, T.EXIT_MS)
    SecretGamePhase.DONE -> 1f
    else -> 0f
}

private fun SecretGameState.enterFrame(base: SecretGameFrame): SecretGameFrame {
    val morphFrom = T.HIDE_MS + T.TO_CENTER_MS
    val descendFrom = morphFrom + T.MORPH_MS
    val lead = (T.HIDE_MS * T.TO_CENTER_LEAD).toLong()
    val toCenter = easeInOutCubic(progress(phaseMs, lead, T.TO_CENTER_MS + T.HIDE_MS - lead))
    val morph = easeInOutCubic(progress(phaseMs, morphFrom, T.MORPH_MS))
    val descend = easeInOutCubic(progress(phaseMs, descendFrom, T.DESCEND_MS))
    val y = lerp(lerp(layout.logoY, layout.aspect * T.CENTER_Y_FRACTION, toCenter), shipY + bob(), descend)
    val spin = toCenter * T.SPIN_TO_CENTER + morph * morph * FULL_TURN * T.SPIN_TURNS
    val shipIn = easeOutCubic((morph - T.SHIP_IN_FROM) / T.SHIP_IN_SPAN)
    val framesFrom = descendFrom + (T.DESCEND_MS * T.FRAMES_LEAD).toLong()
    return base.copy(
        logo = enterLogo(Offset(lerp(layout.logoX, HALF_WIDTH, toCenter), y), spin, morph),
        glow = morphGlow(y, morph),
        ship = if (shipIn > 0f) {
            SecretSprite(
                HALF_WIDTH,
                y,
                lerp(T.SHIP_WIDTH * T.SHIP_IN_MIN_SCALE, T.SHIP_WIDTH, shipIn),
                (1f - shipIn) * -FULL_TURN * T.SHIP_IN_TURNS + sway() * descend,
                shipIn
            )
        } else {
            null
        },
        flame = if (descend > T.FLAME_ON_DESCEND) flame() else 0f,
        buttons = SecretButtonsFrame(easeInOutCubic(progress(phaseMs, framesFrom, T.FRAMES_MS)), collapse = 0f),
    )
}

/** The Settings logo flying to the center of the arena, where the ship is born (also sideways on wide windows). */
private fun SecretGameState.enterLogo(at: Offset, spin: Float, morph: Float): SecretSprite? {
    if (morph >= T.LOGO_FADE_END) return null
    val keep = 1f - morph / T.LOGO_FADE_END
    return SecretSprite(at.x, at.y, layout.logoWidth * lerp(T.LOGO_MIN_SCALE, 1f, keep), spin, keep)
}

private fun morphGlow(y: Float, morph: Float): SecretGlow? {
    val k = (morph - T.GLOW_FROM) / T.GLOW_SPAN
    if (k <= 0f || k >= 1f) return null
    return SecretGlow(HALF_WIDTH, y, T.GLOW_MIN_RADIUS + T.GLOW_RADIUS_GROWTH * k, T.GLOW_ALPHA * (1f - k))
}

private fun SecretGameState.arenaFrame(base: SecretGameFrame): SecretGameFrame {
    val inGame = phase == SecretGamePhase.PLAY || phase == SecretGamePhase.PAUSED
    val facesAlpha = if (phase == SecretGamePhase.INTIMIDATE) progress(phaseMs, 0L, T.INTIMIDATE_FADE_MS) else 1f
    return base.copy(
        ship = flyingShip(shake = 0f),
        flame = flame(),
        faces = faces.map { faceSprite(it, facesAlpha) },
        buttons = SecretButtonsFrame(
            draw = 1f,
            collapse = 0f,
            leftPress = pressGlow(leftPressMs),
            rightPress = pressGlow(rightPressMs),
            leftSqueeze = pressSqueeze(leftPressMs),
            rightSqueeze = pressSqueeze(rightPressMs),
        ),
        countdown = if (phase == SecretGamePhase.COUNTDOWN) countdownFrame() else null,
        streaks = if (inGame) streaks else emptyList(),
        hudScore = if (inGame) score else null,
        hudFlash = levelFlashMs.toFloat() / T.LEVEL_FLASH_MS,
        levelLabel = secretLevel(score) + 1,
        isPaused = phase == SecretGamePhase.PAUSED,
    )
}

private fun SecretGameState.explodeFrame(base: SecretGameFrame): SecretGameFrame {
    val flashK = progress(phaseMs, T.FLASH_FROM_MS, T.FLASH_MS)
    val flashing = phaseMs in T.FLASH_FROM_MS until T.FLASH_FROM_MS + T.FLASH_MS
    val othersAlpha = 1f - progress(phaseMs, 0L, T.OTHER_FACES_FADE_MS)
    val crashedAlpha = 1f - progress(phaseMs, T.CRASH_FACE_FADE_FROM_MS, T.CRASH_FACE_FADE_MS)
    val alive = phaseMs <= T.BOOM_AT_MS
    val collapse = easeInOutCubic(progress(phaseMs, T.EXPLODE_MS - T.COLLAPSE_LEAD_MS, T.COLLAPSE_MS))
    return base.copy(
        ship = if (alive) flyingShip(shake = sin(clockMs * T.CRASH_SHAKE_FREQ) * T.CRASH_SHAKE) else null,
        flame = if (alive) flame() else 0f,
        faces = faces.map { faceSprite(it, if (it.id == crashedFaceId) crashedAlpha else othersAlpha) },
        buttons = SecretButtonsFrame(draw = 1f, collapse = collapse),
        glow = if (flashing) {
            SecretGlow(shipX, shipY, T.FLASH_MIN_RADIUS + T.FLASH_RADIUS_GROWTH * flashK, T.FLASH_ALPHA * (1f - flashK))
        } else {
            null
        },
    )
}

private const val HALF_WIDTH = 0.5f
