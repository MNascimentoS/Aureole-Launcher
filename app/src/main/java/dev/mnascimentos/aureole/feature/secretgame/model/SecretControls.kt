package dev.mnascimentos.aureole.feature.secretgame.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.key.Key
import dev.mnascimentos.aureole.feature.secretgame.model.SecretGameTuning as T

/**
 * Top-left of the ← or → frame in arena widths (the frame is [T.BUTTON_SIZE] wide). Same height as always; on
 * the sides (wide windows) each frame is centered on its side, outside the arena.
 */
fun secretButtonTopLeft(left: Boolean, layout: SecretGameLayout): Offset {
    val x = when {
        layout.buttonsOnSides && left -> -layout.sideSpan * HALF - T.BUTTON_SIZE * HALF
        layout.buttonsOnSides -> 1f + layout.sideSpan * HALF - T.BUTTON_SIZE * HALF
        left -> T.BUTTON_SIDE_INSET
        else -> 1f - T.BUTTON_SIDE_INSET - T.BUTTON_SIZE
    }
    return Offset(x, layout.aspect - T.BUTTON_BOTTOM_OFFSET - T.BUTTON_SIZE * HALF)
}

/** The window corner the frames collapse into, in arena widths (x) and arena heights (y = 1). */
fun secretButtonCornerX(left: Boolean, layout: SecretGameLayout): Float {
    val side = if (layout.buttonsOnSides) layout.sideSpan else 0f
    return if (left) -side else 1f + side
}

enum class SecretKeyAction { LEFT, RIGHT, PAUSE }

/** Keyboard of a foldable, tablet or Chromebook: arrows and A/D move, Space pauses or resumes. */
fun secretKeyAction(key: Key): SecretKeyAction? = when (key) {
    Key.DirectionLeft, Key.A -> SecretKeyAction.LEFT
    Key.DirectionRight, Key.D -> SecretKeyAction.RIGHT
    Key.Spacebar -> SecretKeyAction.PAUSE
    else -> null
}

/** What a key does to the game: moves like the frames, or pauses while playing and resumes while paused. */
fun SecretGameEngine.onKey(state: SecretGameState, action: SecretKeyAction): SecretGameStep = when (action) {
    SecretKeyAction.LEFT -> move(state, -1)
    SecretKeyAction.RIGHT -> move(state, 1)
    SecretKeyAction.PAUSE -> SecretGameStep(
        if (state.phase == SecretGamePhase.PAUSED) onTap(state) else onLifecyclePause(state)
    )
}

private const val HALF = 0.5f
