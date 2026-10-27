package dev.mnascimentos.aureole.feature.home.components.onboarding.model

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
    const val HAND_EDGE_LOOP_MS = 1900
    val HAND_EDGE_PULL = 30.dp
    const val HAND_EDGE_TRAVEL = 0.2f
    const val HAND_PRESS_LOOP_MS = 1800
    const val HAND_PRESS_FILL_MS = 600
    const val SHOW_ME_DELAY_MS = 8_000L

    // Checklist (Figma "Animation" frame)
    const val CHECKLIST_DAMPING = 0.65f
    const val CHECKLIST_STIFFNESS = 180f
    const val CHECKLIST_STAGGER_MS = 60
    val CHECKLIST_ITEM_OFFSET = 16.dp
    const val NEXT_ITEM_BREATH_MS = 2200
    const val PILL_DAMPING = 0.8f
    val CHECKLIST_MAX_WIDTH = 420.dp

    // Celebration
    const val CELEBRATION_MS = 2600L
    const val HALO_CLOSE_MS = 700
    const val MAX_PARTICLES = 40

    // Default home success message before moving on
    const val DEFAULT_HOME_SUCCESS_MS = 1400L
}
