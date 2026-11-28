/**
 * Jogo secreto.
 *
 * @en Secret game
 * @area jogo
 * @ref P01CincoToques telas/ajustes/Padrao Cinco toques no logo / Five taps on the logo
 */
package dev.mnascimentos.aureole.fluxos.jogosecreto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkRocket
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkSpark
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkTone
import dev.mnascimentos.aureole.composable.fireworks.model.FireworksState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.secretgame.components.SecretBestScoreCard
import dev.mnascimentos.aureole.feature.secretgame.components.SecretGameCanvas
import dev.mnascimentos.aureole.feature.secretgame.components.SecretSoundButton
import dev.mnascimentos.aureole.feature.secretgame.model.SecretFace
import dev.mnascimentos.aureole.feature.secretgame.model.SecretFaceKind
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameColors
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameDrawing
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameLayout
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGamePhase
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameState
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning
import dev.mnascimentos.aureole.feature.secretgame.model.SecretParticle
import dev.mnascimentos.aureole.feature.secretgame.model.SecretParticleKind
import dev.mnascimentos.aureole.feature.secretgame.model.SecretStreak
import dev.mnascimentos.aureole.feature.secretgame.model.newSecretStars
import dev.mnascimentos.aureole.feature.secretgame.model.toFrame
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * O jogo na tela inteira (proporção de celular) com o quadro de [state]; [showSoundButton] põe o botão de som
 * onde o jogo o mostra na pausa.
 */
@Composable
private fun GameCase(state: SecretGameState, showSoundButton: Boolean = false) {
    AureoleLauncherTheme {
        val colors = SecretGameColors(
            ship = AureoleDS.colors.onSurfaceHigh,
            face = lerp(AureoleDS.colors.onSurfaceHigh, AureoleDS.colors.surface, SecretGameDrawing.FACE_LIGHTEN)
        )
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / SAMPLE_ASPECT)
                .background(AureoleDS.colors.surface)
        ) {
            SecretGameCanvas(frame = state.toFrame(), colors = colors, aspect = SAMPLE_ASPECT)
            if (showSoundButton) {
                val gap = SAMPLE_ASPECT * SecretGameTuning.CENTER_Y_FRACTION + SecretGameDrawing.SOUND_BUTTON_GAP
                SecretSoundButton(
                    isOn = true,
                    onToggle = {},
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset(y = maxWidth * gap + AureoleDS.dimens.medium)
                )
            }
        }
    }
}

/** O popup central do recorde (`jogo-secreto` D-06), na tela inteira. */
@Composable
private fun BestScoreCase() {
    AureoleLauncherTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / SAMPLE_ASPECT)
                .background(AureoleDS.colors.surface),
            contentAlignment = Alignment.Center
        ) {
            SecretBestScoreCard(bestScore = RECORD_SCORE, triggerText = stringResource(R.string.settings_root_thank_you))
        }
    }
}

/**
 * Início.
 *
 * @en Start
 */
@PreviewTest
@AureolePreview
@Composable
private fun P02InicioScreenshotTest() = GameCase(sampleState(SecretGamePhase.COUNTDOWN, COUNTDOWN_MS))

/**
 * Jogando.
 *
 * @en Playing
 */
@PreviewTest
@AureolePreview
@Composable
private fun P03JogandoScreenshotTest() =
    GameCase(sampleState(SecretGamePhase.PLAY, PLAY_MS, score = PLAY_SCORE))

/**
 * Pausa.
 *
 * @en Paused
 */
@PreviewTest
@AureolePreview
@Composable
private fun P04PausaScreenshotTest() =
    GameCase(sampleState(SecretGamePhase.PAUSED, PLAY_MS, score = PLAY_SCORE), showSoundButton = true)

/**
 * Fim.
 *
 * Fim com recorde novo e os fogos.
 *
 * @en Game over
 */
@PreviewTest
@AureolePreview
@Composable
private fun P05FimScreenshotTest() = GameCase(
    sampleState(SecretGamePhase.RESULT, RESULT_MS, score = RECORD_SCORE, record = true)
        .copy(fireworks = fireworksSample())
)

/**
 * Recorde.
 *
 * @en Best score
 */
@PreviewTest
@AureolePreview
@Composable
private fun P06RecordeScreenshotTest() = BestScoreCase()

/**
 * Fim sem recorde.
 *
 * Fim sem recorde: Best score pequeno acima de Your score e a mensagem abaixo.
 *
 * @en Game over, no record
 */
@PreviewTest
@AureolePreview
@Composable
private fun P07FimSemRecordeScreenshotTest() = GameCase(
    sampleState(SecretGamePhase.RESULT, RESULT_MS, score = LOSE_SCORE, bestScore = LOSE_BEST)
        .copy(particles = emptyList())
)

private val SampleLayout = SecretGameLayout(aspect = SAMPLE_ASPECT, logoX = 0.5f, logoY = 0.25f, logoWidth = 0.33f)

private val SampleFaces = listOf(
    SecretFace(0, 0, 0.35f, SecretFaceKind.CREATOR, 0f),
    SecretFace(1, 2, 0.8f, SecretFaceKind.DREAMER, 1f),
)

private fun sampleState(
    phase: SecretGamePhase,
    phaseMs: Long,
    score: Int = 0,
    record: Boolean = false,
    bestScore: Int = if (record) score else score + 1
) =
    SecretGameState(
        layout = SampleLayout,
        phase = phase,
        phaseMs = phaseMs,
        clockMs = phaseMs,
        faces = SampleFaces,
        stars = newSecretStars(SAMPLE_ASPECT, Random(1)),
        streaks = listOf(SecretStreak(0.2f, 0.5f, 0.1f), SecretStreak(0.7f, 1.2f, 0.12f)),
        particles = listOf(SecretParticle(0.5f, 1.0f, 0f, 0f, 0.8f, 0.01f, SecretParticleKind.SHIP)),
        score = score,
        isNewRecord = record,
        bestScore = bestScore,
    )

private fun fireworksSample() = FireworksState(
    rockets = listOf(FireworkRocket(0.3f, 0.7f, 0.4f, -1f, FireworkTone.PRIMARY)),
    sparks = List(FIREWORK_SPARKS) {
        val angle = it * FIREWORK_ANGLE_STEP
        FireworkSpark(
            x = 0.65f + cos(angle) * FIREWORK_RING,
            y = 0.55f + sin(angle) * FIREWORK_RING,
            vx = 0f,
            vy = 0f,
            life = 1f,
            radius = 0.006f,
            tone = FireworkTone.entries[it % FireworkTone.entries.size],
        )
    },
)

private const val SAMPLE_ASPECT = 2.1f
private const val COUNTDOWN_MS = 800L
private const val PLAY_MS = 4000L
private const val RESULT_MS = 1000L
private const val PLAY_SCORE = 120
private const val RECORD_SCORE = 290
private const val LOSE_SCORE = 10
private const val LOSE_BEST = 100
private const val FIREWORK_SPARKS = 40
private const val FIREWORK_ANGLE_STEP = 0.157f
private const val FIREWORK_RING = 0.2f
