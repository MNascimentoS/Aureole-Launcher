package dev.mnascimentos.aureole.feature.secretgame.model

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

/** Cores do jogo, todas vindas do tema: [ship] é a cor do logo de Settings e [face] ela clareada. */
data class SecretGameColors(val ship: Color, val face: Color)

/** Modificadores que a tela raiz aplica ao "Muito obrigado!" (gatilho) e ao logo (medir e esconder). */
data class SecretGameModifiers(val thankYou: Modifier = Modifier, val logo: Modifier = Modifier)

/** Where the Settings logo is, in px relative to the overlay, so the game can start from it. */
data class SecretLogoPlacement(val center: Offset, val widthPx: Float)

/** What the overlay reports: each frame, the final score and the end of the exit animation. */
data class SecretGameCallbacks(
    val onFrame: (SecretGameFrame) -> Unit,
    val onGameOver: (Int) -> Unit,
    val onFinished: () -> Unit,
)

/** A line of text on the canvas: [y] is its top, or its middle when [middle]; [size] is a width fraction. */
data class SecretText(val text: String, val y: Float, val size: Float, val color: Color, val middle: Boolean = false)

/** Medidas de desenho em larguras de tela (tela = 1f); os tempos e o mundo ficam em [SecretGameTuning]. */
object SecretGameDrawing {
    const val FACE_LIGHTEN = 0.35f
    const val FACE_ASPECT_CREATOR = 480f / 501f
    const val FACE_ASPECT_DREAMER = 474f / 459f
    const val RAD_TO_DEG = 57.29578f

    const val STREAK_ALPHA = 0.18f
    const val STREAK_WIDTH = 0.004f
    const val HUD_ALPHA = 0.45f
    const val HUD_TEXT = 0.06f
    const val HUD_TOP = 0.09f
    const val LEVEL_TEXT = 0.035f
    const val LEVEL_GAP = 1.5f
    const val BACK_HINT_ALPHA = 0.55f
    const val BACK_HINT_GAP = 0.16f
    const val SOUND_BUTTON_GAP = 0.22f

    const val TRAIL_HEAD_RISE = 0.32f
    const val TRAIL_BOTTOM_DROP = 0.18f
    const val TRAIL_CENTER_DROP = 0.08f
    const val TRAIL_RADIUS_X = 0.40f
    const val TRAIL_TIP_X = 0.05f
    const val TRAIL_CONTROL_X = 0.55f
    const val TRAIL_CONTROL_Y = 0.45f
    const val TRAIL_MID_STOP = 0.55f
    const val TRAIL_MID_ALPHA = 0.07f
    const val TRAIL_BODY_ALPHA = 0.17f
    const val TRAIL_LINE_ALPHA = 0.32f
    const val TRAIL_LINE_PEAK = 0.75f
    const val TRAIL_LINE_DP = 1f
    const val TRAIL_LINE_X = 0.24f
    const val TRAIL_LINE_END = 0.12f
    const val TRAIL_SIDE_LENGTH = 0.65f
    const val TRAIL_MID_LENGTH = 0.95f
    const val HALF_TURN_DEG = 180f
    const val FLAME_WIDTH = 0.07f
    const val FLAME_HEIGHT = 0.2f
    const val FLAME_ALPHA = 0.55f
    const val FLAME_OFFSET = 0.55f

    const val MIN_DRAW = 0.001f
    const val BUTTON_STROKE = 0.006f
    const val BUTTON_RADIUS = 0.025f
    const val BUTTON_ALPHA = 0.8f
    const val BUTTON_SHAFT = 0.18f
    const val BUTTON_HEAD = 0.15f
    const val PRESS_RING_STROKE_DP = 1.2f
    const val PRESS_START_RING = 0.5f
    const val BUTTON_TARGET_SCALE = 1.5f
    const val COLLAPSE_SCALE_MIN = 0.1f

    const val COUNT_TEXT = 0.3f
    const val GO_TEXT = 0.22f
    const val PAUSE_VEIL_ALPHA = 0.6f
    const val TITLE_TEXT = 0.1f
    const val SUB_TEXT = 0.05f
    const val BEST_TEXT = 0.04f
    const val RESULT_LINE_GAP = 0.09f
    const val RESULT_SUB_LINES = 2f
    const val RESULT_BELOW_CENTER = 0.0f
    const val HALF = 0.5f
}
