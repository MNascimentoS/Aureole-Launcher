package dev.mnascimentos.aureole.feature.secretgame.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameColors
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameDrawing
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameEngine
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameLayout
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGamePhase
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameState
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning
import dev.mnascimentos.aureole.feature.secretgame.model.toFrame
import kotlin.random.Random

/**
 * The game's opening, animated: the logo turning into the ship, the faces coming down and the countdown, until a
 * moment into the game; then it starts over. Runs the real [SecretGameEngine] (fixed seed), so it follows any
 * change to the timeline. Play it with the preview's interactive mode (▶) in Android Studio.
 */
@AureolePreview
@Composable
private fun SecretGameOpeningPreview() {
    AureoleLauncherTheme {
        val engine = remember { SecretGameEngine(Random(PREVIEW_SEED)) }
        val layout = remember {
            SecretGameLayout(aspect = PREVIEW_ASPECT, logoX = LOGO_X, logoY = LOGO_Y, logoWidth = LOGO_WIDTH)
        }
        var state by remember { mutableStateOf(engine.start(layout, PREVIEW_BEST_SCORE)) }
        LaunchedEffect(engine) {
            var last = withFrameNanos { it }
            while (true) {
                val now = withFrameNanos { it }
                val next = engine.advance(state, ((now - last) / NANOS_PER_MS).coerceAtMost(MAX_GAP_MS))
                state = if (next.openingIsOver()) engine.start(layout, PREVIEW_BEST_SCORE) else next
                last = now
            }
        }
        val colors = SecretGameColors(
            ship = AureoleDS.colors.onSurfaceHigh,
            face = lerp(AureoleDS.colors.onSurfaceHigh, AureoleDS.colors.surface, SecretGameDrawing.FACE_LIGHTEN)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f / PREVIEW_ASPECT)
                .background(AureoleDS.colors.surface)
        ) {
            SecretGameCanvas(frame = state.toFrame(), colors = colors, layout = layout)
        }
    }
}

/**
 * [elapsedMs] of game time in steps of at most [SecretGameTuning.MAX_STEP_MS]: the engine caps each step there, so
 * one big step per slow preview frame would lose time and the opening would never reach the game.
 */
private fun SecretGameEngine.advance(state: SecretGameState, elapsedMs: Long): SecretGameState {
    var s = state
    var left = elapsedMs
    while (left > 0 && !s.openingIsOver()) {
        val dt = minOf(left, SecretGameTuning.MAX_STEP_MS)
        s = step(s, dt).state
        left -= dt
    }
    return s
}

/** The game has run [PLAY_HOLD_MS], or it ended early (a face hit the ship). */
private fun SecretGameState.openingIsOver(): Boolean =
    phase > SecretGamePhase.PLAY || (phase == SecretGamePhase.PLAY && phaseMs >= PLAY_HOLD_MS)

private const val PREVIEW_ASPECT = 2.1f
private const val LOGO_X = 0.5f
private const val LOGO_Y = 0.25f
private const val LOGO_WIDTH = 0.33f
private const val PREVIEW_SEED = 1
private const val PREVIEW_BEST_SCORE = 100
private const val PLAY_HOLD_MS = 1500L
private const val NANOS_PER_MS = 1_000_000L

/** A longer gap (the preview was paused) does not jump the animation ahead. */
private const val MAX_GAP_MS = 250L
