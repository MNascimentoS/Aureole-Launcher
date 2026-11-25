package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.ui.unit.dp

/** Onboarding animation values. Layout tokens are never used for motion, so these stay as named constants. */
object OnboardingMotionSpec {
    // Welcome: halo ignition
    const val RING_DRAW_MS = 1000
    const val RING_DELAY_MS = 200
    const val LOGO_DELAY_MS = 500
    const val LOGO_START_SCALE = 0.85f
    const val LOGO_DAMPING = 0.6f
    const val TEXT_ENTER_MS = 500
    const val TITLE_DELAY_MS = 900
    const val SUBTITLE_DELAY_MS = 1050
    const val BUTTONS_DELAY_MS = 1250
    val TEXT_ENTER_OFFSET = 18.dp
    const val GLOW_START_SCALE = 0.6f
    const val GLOW_PULSE_SCALE = 1.12f
    const val GLOW_PULSE_MS = 3200
    const val WELCOME_EXIT_MS = 550
    const val WELCOME_EXIT_SCALE = 7f

    // Cards (default home, coach)
    const val CARD_DAMPING = 0.8f
    const val CARD_STIFFNESS = 300f

    // Ghost hand loops
    const val HAND_SWIPE_LOOP_MS = 1800
    const val HAND_APPEAR_MS = 150
    const val HAND_SWIPE_TRAVEL = 0.3f
    const val HAND_EDGE_LOOP_MS = 2300

    // Edge scrub phases: pull in from the edge, short pause, then slide down.
    const val HAND_EDGE_PULL_END = 0.30f
    const val HAND_EDGE_SLIDE_START = 0.38f
    const val HAND_EDGE_SLIDE_END = 0.83f
    const val HAND_PRESS_LOOP_MS = 1800
    const val HAND_PRESS_FILL_MS = 600
    // After the right gesture, "Nice!" stays this long and then shrinks into the "Go back" chip (RF-E2, D-04).
    const val COACH_SUCCESS_CARD_MS = 2_000L
    val HAND_FINGER_RADIUS = 18.dp
    val HAND_EDGE_PULL_TRAVEL = 56.dp
    val HAND_PRESS_RING_GAP = 10.dp
    val HAND_TRAIL_WIDTH = 4.dp

    // Checklist (Figma "Animation" frame)
    // Minimizing: rows, title and footer fade out this fast, before the card shrinks into the pill.
    const val CHECKLIST_LIST_FADE_MS = 120
    const val CHECKLIST_DAMPING = 0.65f
    const val CHECKLIST_STIFFNESS = 180f
    const val CHECKLIST_STAGGER_MS = 60
    val CHECKLIST_ITEM_OFFSET = 16.dp
    const val NEXT_ITEM_BREATH_MS = 2200
    // Pill ↔ card (D-19): one container transform of ~300 ms with the emphasized easing; the glass blur is off while
    // it moves and the task's screen only rises once it is over (RF-B3, RF-B5).
    const val CHECKLIST_MORPH_MS = 300
    val CHECKLIST_MORPH_EASING = CubicBezierEasing(0.2f, 0f, 0f, 1f)
    val CHECKLIST_MAX_WIDTH = 420.dp

    // New item highlight (onboarding-redesenho RF-D1, D-01): a dashed outline ~6 dp outside the item draws itself in
    // ~500 ms and then marches (one dash cycle per second); the chip sits on its top-start corner.
    const val NEW_ITEM_DRAW_MS = 500
    const val NEW_ITEM_MARCH_MS = 1000
    val NEW_ITEM_MARGIN = 6.dp

    // Celebration ("All set", RF-D5, D-18): ~2.4 s on a light scrim, then a fade.
    const val CELEBRATION_MS = 2400L
    const val CELEBRATION_SCRIM_ALPHA = 0.25f
    const val CELEBRATION_BURST_MS = 2000
    const val HALO_CLOSE_MS = 700
    const val MAX_PARTICLES = 40
    val PARTICLE_RADIUS = 3.dp

    // Default home success message before moving on
    const val DEFAULT_HOME_SUCCESS_MS = 1400L
}
