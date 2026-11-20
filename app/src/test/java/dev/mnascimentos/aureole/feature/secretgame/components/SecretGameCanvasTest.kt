package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkRocket
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkSpark
import dev.mnascimentos.aureole.composable.fireworks.model.FireworkTone
import dev.mnascimentos.aureole.composable.fireworks.model.FireworksState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.secretgame.model.SecretFace
import dev.mnascimentos.aureole.feature.secretgame.model.SecretFaceKind
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameColors
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameFrame
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameLayout
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGamePhase
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameState
import dev.mnascimentos.aureole.feature.secretgame.model.SecretParticle
import dev.mnascimentos.aureole.feature.secretgame.model.SecretParticleKind
import dev.mnascimentos.aureole.feature.secretgame.model.SecretStreak
import dev.mnascimentos.aureole.feature.secretgame.model.toFrame
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.math.cos
import kotlin.math.sin

@RunWith(RobolectricTestRunner::class)
class SecretGameCanvasTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `when drawing each frame of the game, should render without failing`() {
        val frames = sampleFrames()
        var index by mutableStateOf(0)
        rule.setContent {
            AureoleLauncherTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    SecretGameCanvas(frame = frames[index], colors = SampleColors, aspect = SAMPLE_ASPECT)
                }
            }
        }
        frames.indices.forEach {
            index = it
            rule.waitForIdle()
        }
    }

    @Test
    fun `when showing the best score, should show the number and the hint`() {
        rule.setContent {
            AureoleLauncherTheme {
                SecretBestScoreCard(bestScore = 290, triggerText = "Muito obrigado!")
            }
        }

        rule.onNodeWithText("290").assertExists()
    }

    private companion object {
        val SampleColors = SecretGameColors(ship = Color.White, face = Color.LightGray)
    }
}

private val SampleLayout = SecretGameLayout(aspect = SAMPLE_ASPECT, logoX = 0.5f, logoY = 0.25f, logoWidth = 0.33f)

private val SampleFaces = listOf(
    SecretFace(0, 0, 0.35f, SecretFaceKind.CREATOR, 0f),
    SecretFace(1, 2, 0.8f, SecretFaceKind.DREAMER, 1f),
)

private fun sampleState(phase: SecretGamePhase, phaseMs: Long, score: Int = 0, record: Boolean = false) =
    SecretGameState(
        layout = SampleLayout,
        phase = phase,
        phaseMs = phaseMs,
        clockMs = phaseMs,
        faces = SampleFaces,
        streaks = listOf(SecretStreak(0.2f, 0.5f, 0.1f), SecretStreak(0.7f, 1.2f, 0.12f)),
        particles = listOf(SecretParticle(0.5f, 1.0f, 0f, 0f, 0.8f, 0.01f, SecretParticleKind.SHIP)),
        score = score,
        isNewRecord = record,
    )

/** Five representative frames: entry halfway, countdown, playing, paused and the result with a new record. */
private fun sampleFrames(): List<SecretGameFrame> = listOf(
    sampleState(SecretGamePhase.ENTER, ENTER_MID_MS).toFrame(),
    sampleState(SecretGamePhase.COUNTDOWN, COUNTDOWN_MS).toFrame(),
    sampleState(SecretGamePhase.PLAY, PLAY_MS, score = 120).toFrame(),
    sampleState(SecretGamePhase.PAUSED, PLAY_MS, score = 120).toFrame(),
    sampleState(SecretGamePhase.RESULT, RESULT_MS, score = 290, record = true).toFrame(),
    sampleState(SecretGamePhase.PLAY, PLAY_MS, score = 300).copy(levelFlashMs = LEVEL_FLASH_SAMPLE_MS).toFrame(),
    sampleState(SecretGamePhase.RESULT, RESULT_MS, score = 290, record = true)
        .copy(fireworks = fireworksSample()).toFrame(),
)

private const val SAMPLE_ASPECT = 2.1f
private const val ENTER_MID_MS = 1400L
private const val COUNTDOWN_MS = 800L
private const val PLAY_MS = 4000L
private const val RESULT_MS = 1000L

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

private const val FIREWORK_SPARKS = 40
private const val FIREWORK_ANGLE_STEP = 0.157f
private const val FIREWORK_RING = 0.2f
private const val LEVEL_FLASH_SAMPLE_MS = 600L
