package dev.mnascimentos.aureole.feature.secretgame.components

import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameCallbacks
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameColors
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameEngine
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameEvent
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameLayout
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGamePhase
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameStep
import dev.mnascimentos.aureole.feature.secretgame.model.SecretLogoPlacement
import dev.mnascimentos.aureole.feature.secretgame.model.toFrame
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameDrawing as D
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

private const val NANOS_PER_MS = 1_000_000L

/**
 * The whole hidden game on top of Settings: runs the engine on every frame, handles input, Back, vibration and the
 * app going to the background. [logo] places the Settings logo.
 */
@Composable
fun SecretGameOverlay(
    logo: SecretLogoPlacement,
    bestScore: Int,
    callbacks: SecretGameCallbacks,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val widthPx = constraints.maxWidth.toFloat()
        val layout = remember(widthPx, logo) {
            SecretGameLayout(
                aspect = constraints.maxHeight / widthPx,
                logoX = logo.center.x / widthPx,
                logoY = logo.center.y / widthPx,
                logoWidth = logo.widthPx / widthPx
            )
        }
        val engine = remember { SecretGameEngine() }
        var state by remember { mutableStateOf(engine.start(layout, bestScore)) }
        val haptic = LocalHapticFeedback.current
        val currentOnFrame by rememberUpdatedState(callbacks.onFrame)
        val currentOnGameOver by rememberUpdatedState(callbacks.onGameOver)
        val currentOnFinished by rememberUpdatedState(callbacks.onFinished)
        val apply = { step: SecretGameStep ->
            state = step.state
            step.events.forEach { handleEvent(it, haptic, currentOnGameOver) }
        }

        LaunchedEffect(engine) {
            var last = withFrameNanos { it }
            while (state.phase != SecretGamePhase.DONE) {
                val now = withFrameNanos { it }
                apply(engine.step(state, (now - last) / NANOS_PER_MS))
                last = now
                currentOnFrame(state.toFrame())
            }
            currentOnFinished()
        }
        BackHandler { apply(engine.onBack(state)) }
        KeepAwakeInPortrait()
        PauseOnBackground { state = engine.onLifecyclePause(state) }

        val colors = SecretGameColors(
            ship = AureoleDS.colors.onSurfaceHigh,
            face = lerp(AureoleDS.colors.onSurfaceHigh, AureoleDS.colors.surface, D.FACE_LIGHTEN)
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    state = engine.onTap(state)
                }
        ) {
            SecretGameCanvas(frame = state.toFrame(), colors = colors, aspect = layout.aspect)
        }
        MoveTargets(layout, enabled = state.acceptsInput) { direction -> apply(engine.move(state, direction)) }
    }
}

private fun handleEvent(event: SecretGameEvent, haptic: HapticFeedback, onGameOver: (Int) -> Unit) {
    when (event) {
        SecretGameEvent.LaneTick, SecretGameEvent.FireworkBurst, is SecretGameEvent.LevelUp ->
            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        SecretGameEvent.Crash -> haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        is SecretGameEvent.GameOver -> onGameOver(event.score)
        is SecretGameEvent.CountdownTick, is SecretGameEvent.ResultShown -> Unit // só som
    }
}

/** While the game is open the screen stays on and the Activity is locked in portrait; both are restored after. */
@Composable
private fun KeepAwakeInPortrait() {
    val view = LocalView.current
    val activity = LocalActivity.current
    DisposableEffect(view, activity) {
        val previous = activity?.requestedOrientation
        view.keepScreenOn = true
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
        onDispose {
            view.keepScreenOn = false
            if (previous != null) activity.requestedOrientation = previous
        }
    }
}

@Composable
private fun PauseOnBackground(onPause: () -> Unit) {
    val owner = LocalLifecycleOwner.current
    val current by rememberUpdatedState(onPause)
    DisposableEffect(owner) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_PAUSE) current() }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
}

/** Transparent touch targets over the ← → frames (bigger than the frames themselves). */
@Composable
private fun BoxWithConstraintsScope.MoveTargets(
    layout: SecretGameLayout,
    enabled: Boolean,
    onMove: (Int) -> Unit
) {
    if (!enabled) return
    val target = maxWidth * (T.BUTTON_SIZE * D.BUTTON_TARGET_SCALE)
    listOf(true, false).forEach { left ->
        val center = buttonTopLeft(left, layout.aspect) + Offset(T.BUTTON_SIZE * D.HALF, T.BUTTON_SIZE * D.HALF)
        val description = stringResource(if (left) R.string.secret_game_move_left else R.string.secret_game_move_right)
        Box(
            modifier = Modifier
                .offset(maxWidth * center.x - target * D.HALF, maxWidth * center.y - target * D.HALF)
                .size(target)
                .semantics { contentDescription = description }
                .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                    onMove(if (left) -1 else 1)
                }
        )
    }
}
