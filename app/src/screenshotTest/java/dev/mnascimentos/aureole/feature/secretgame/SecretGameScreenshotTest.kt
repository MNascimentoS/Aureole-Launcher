package dev.mnascimentos.aureole.feature.secretgame

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
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
import dev.mnascimentos.aureole.feature.secretgame.components.SecretThankYou
import dev.mnascimentos.aureole.feature.secretgame.model.SecretShineState
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.lerp
import dev.mnascimentos.aureole.feature.secretgame.model.SecretButtonsFrame
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
import dev.mnascimentos.aureole.feature.secretgame.model.newSecretStars
import dev.mnascimentos.aureole.feature.secretgame.model.toFrame
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameDrawing
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
private fun FrameCase(index: Int) {
    AureoleLauncherTheme {
        val colors = SecretGameColors(
            ship = AureoleDS.colors.onSurfaceHigh,
            face = lerp(AureoleDS.colors.onSurfaceHigh, AureoleDS.colors.surface, SecretGameDrawing.FACE_LIGHTEN)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / SAMPLE_ASPECT)
                .background(AureoleDS.colors.surface)
        ) {
            SecretGameCanvas(frame = sampleFrames()[index], colors = colors, aspect = SAMPLE_ASPECT)
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
fun SecretGameEnterScreenshotTest() = FrameCase(0)

@PreviewTest
@AureolePreview
@Composable
fun SecretGameCountdownScreenshotTest() = FrameCase(1)

@PreviewTest
@AureolePreview
@Composable
fun SecretGamePlayScreenshotTest() = FrameCase(2)

@PreviewTest
@AureolePreview
@Composable
fun SecretGamePausedScreenshotTest() = FrameCase(3)

@PreviewTest
@AureolePreview
@Composable
fun SecretGameResultScreenshotTest() = FrameCase(4)

@PreviewTest
@AureolePreview
@Composable
fun SecretGameLevelUpScreenshotTest() = FrameCase(5)

@PreviewTest
@AureolePreview
@Composable
fun SecretGameFireworksScreenshotTest() = FrameCase(6)

@PreviewTest
@AureolePreview
@Composable
fun SecretGameButtonsHiddenScreenshotTest() = FrameCase(7)

@PreviewTest
@AureolePreview
@Composable
fun SecretSoundButtonOnScreenshotTest() {
    AureoleLauncherTheme { SecretSoundButton(isOn = true, onToggle = {}) }
}

@PreviewTest
@AureolePreview
@Composable
fun SecretSoundButtonOffScreenshotTest() {
    AureoleLauncherTheme { SecretSoundButton(isOn = false, onToggle = {}) }
}

@PreviewTest
@AureolePreview
@Composable
fun SecretThankYouSweepScreenshotTest() {
    AureoleLauncherTheme { SecretThankYou(shine = SecretShineState(sweep = 0.5f)) }
}

@PreviewTest
@AureolePreview
@Composable
fun SecretThankYouStarScreenshotTest() {
    AureoleLauncherTheme { SecretThankYou(shine = SecretShineState(star = 0.25f), modifier = Modifier.padding(24.dp)) }
}

@PreviewTest
@AureolePreview
@Composable
fun SecretBestScoreEmptyScreenshotTest() {
    AureoleLauncherTheme { SecretBestScoreCard(bestScore = 0, triggerText = "Muito obrigado!") }
}

@PreviewTest
@AureolePreview
@Composable
fun SecretBestScoreRecordScreenshotTest() {
    AureoleLauncherTheme { SecretBestScoreCard(bestScore = 290, triggerText = "Muito obrigado!") }
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
        stars = newSecretStars(SAMPLE_ASPECT, Random(1)),
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
    sampleState(SecretGamePhase.ENTER, ENTER_MID_MS).toFrame().copy(buttons = SecretButtonsFrame(0f, 0f)),
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
