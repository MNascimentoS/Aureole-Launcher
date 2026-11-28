package dev.mnascimentos.aureole.feature.secretgame.model

/**
 * Timeline e regras do jogo secreto. Distâncias em larguras de tela (largura = 1f), tempos em ms.
 * Os valores vêm do protótipo `specs/jogo-secreto/prototipo-jogo-secreto.html` (tela de referência 360 x 772).
 */
object SecretGameTuning {
    const val LANE_COUNT = 3
    const val TAPS_TO_OPEN = 5
    const val TAP_WINDOW_MS = 1500L
    const val MAX_STEP_MS = 48L

    // Timeline
    const val HIDE_MS = 500L
    const val TO_CENTER_MS = 650L
    const val MORPH_MS = 900L
    const val DESCEND_MS = 700L
    const val FRAMES_MS = 450L
    const val ENTER_TAIL_MS = 150L
    const val ENTER_TOTAL_MS = HIDE_MS + TO_CENTER_MS + MORPH_MS + DESCEND_MS + ENTER_TAIL_MS
    const val INTIMIDATE_MS = 2200L
    const val INTIMIDATE_FADE_MS = 500L
    const val COUNT_STEP_MS = 750L
    const val COUNT_STEPS = 4
    const val COUNTDOWN_MS = COUNT_STEP_MS * COUNT_STEPS
    const val EXPLODE_MS = 750L
    const val COLLAPSE_MS = 420L
    const val COLLAPSE_LEAD_MS = 200L
    const val EXPLODE_TOTAL_MS = EXPLODE_MS + COLLAPSE_MS
    const val BOOM_AT_MS = 260L
    const val CRASH_FACE_FADE_FROM_MS = 240L
    const val CRASH_FACE_FADE_MS = 300L
    const val OTHER_FACES_FADE_MS = 500L
    const val FLASH_FROM_MS = 220L
    const val FLASH_MS = 200L
    const val RESULT_IN_MS = 400L
    const val RESULT_HOLD_MS = 2100L
    const val RESULT_OUT_MS = 400L
    const val RESULT_TOTAL_MS = RESULT_IN_MS + RESULT_HOLD_MS + RESULT_OUT_MS
    const val EXIT_MS = 600L

    // Mundo (larguras de tela)
    const val FACE_SIZE = 0.205f
    const val SHIP_WIDTH = 0.205f
    const val SHIP_ASPECT = 93f / 111f
    const val LOGO_ASPECT = 73f / 128f
    const val SHIP_BOTTOM_OFFSET = 0.583f
    const val BUTTON_SIZE = 0.161f
    const val BUTTON_BOTTOM_OFFSET = 0.378f
    const val BUTTON_SIDE_INSET = 0.061f
    const val CENTER_Y_FRACTION = 0.40f
    const val INTIMIDATE_FROM_Y = -0.144f
    const val INTIMIDATE_TO_Y = 0.178f
    const val PASS_MARGIN = FACE_SIZE

    // Arena: no celular é a tela toda; em tela mais larga, uma coluna com a proporção do protótipo (360 × 772)
    const val PHONE_MIN_ASPECT = 1.75f
    const val REFERENCE_ASPECT = 772f / 360f
    const val MAX_SPREAD_FACTOR = 3f
    const val SIDE_CONTROL_MIN_DP = 105f

    // Dificuldade: curvas por nível em SecretDifficulty
    const val FIRST_SPAWN_MS = 600f
    const val POINTS_PER_FACE = 10
    const val LEVEL_FLASH_MS = 900L

    // Rastro dos rostos caindo: comprimento = FACE_SIZE × (TRAIL_BASE + velocidade × TRAIL_SPEED)
    const val TRAIL_BASE = 0.5f
    const val TRAIL_SPEED = 1.6f

    // Placar e aviso de nível (alpha e escala do HUD)
    const val HUD_ALPHA = 0.35f
    const val HUD_FLASH_ALPHA = 0.6f
    const val HUD_FLASH_SCALE = 0.35f
    const val LEVEL_LABEL_FADE = 1.6f
    const val LEVEL_LABEL_ALPHA = 0.8f

    // Hitbox (frações do tamanho do sprite)
    const val FACE_HIT = 0.36f
    const val SHIP_HIT_TOP = 0.32f
    const val SHIP_HIT_BOTTOM = 0.40f

    // Movimento e voo
    const val SHIP_FOLLOW_RATE = 48f

    /** Inclinação persegue a distância que falta até a faixa: pico ~5°, volta suave em ~280 ms. */
    const val TILT_PER_LANE = 0.9f
    const val TILT_CHASE_RATE = 10f

    // Toque nos botões: aperta, acende o fundo, solta um anel e a seta dá um passo para o lado
    const val PRESS_MS = 220L
    const val PRESS_SQUEEZE_MS = 140L
    const val PRESS_SCALE = 0.08f
    const val PRESS_FILL_ALPHA = 0.24f
    const val PRESS_RING_ALPHA = 0.45f
    const val PRESS_RING_GROWTH = 0.35f
    const val PRESS_NUDGE = 0.011f
    const val PRESS_IDLE_MS = 100_000L
    const val BOB_AMPLITUDE = 0.011f
    const val BOB_PERIOD_S = 1.7f
    const val SWAY_AMPLITUDE = 0.035f
    const val SWAY_PERIOD_S = 2.9f
    const val FLAME_BASE = 0.75f
    const val FLAME_FLICKER = 0.25f
    const val FLAME_FREQ_A = 31f
    const val FLAME_FREQ_B = 17f
    const val FACE_SWING = 0.06f
    const val FACE_SWING_FREQ = 2f
    const val FACE_HOVER = 0.008f
    const val FACE_HOVER_PERIOD_MS = 260f
    const val CRASH_SHAKE = 0.008f
    const val CRASH_SHAKE_FREQ = 0.9f
    const val STREAK_COUNT = 22
    const val STREAK_SPEED = 1.6f
    const val STREAK_MIN_LENGTH = 0.055f
    const val STREAK_EXTRA_LENGTH = 0.14f

    // Entrada: logo → nave
    const val TO_CENTER_LEAD = 0.4f
    const val SPIN_TO_CENTER = 0.6f
    const val SPIN_TURNS = 1.5f
    const val LOGO_FADE_END = 0.75f
    const val LOGO_MIN_SCALE = 0.55f
    const val GLOW_FROM = 0.35f
    const val GLOW_SPAN = 0.5f
    const val GLOW_MIN_RADIUS = 0.05f
    const val GLOW_RADIUS_GROWTH = 0.167f
    const val GLOW_ALPHA = 0.45f
    const val SHIP_IN_FROM = 0.4f
    const val SHIP_IN_SPAN = 0.4f
    const val SHIP_IN_MIN_SCALE = 0.5f
    const val SHIP_IN_TURNS = 0.75f
    const val FLAME_ON_DESCEND = 0.3f
    const val FRAMES_LEAD = 0.55f

    // Contagem
    const val COUNT_FADE_FROM = 0.75f
    const val COUNT_SCALE_FROM = 1.35f
    const val COUNT_SCALE_SPEED = 2.2f

    // Explosão e partículas
    const val SHIP_PARTICLES = 90
    const val FACE_PARTICLES = 60
    const val SHIP_BURST = 0.53f
    const val FACE_BURST = 0.42f
    const val BURST_MIN = 0.4f
    const val PARTICLE_LIFT = 0.11f
    const val GRAVITY = 0.72f
    const val PARTICLE_FADE = 1.15f
    const val PARTICLE_MIN_RADIUS = 0.003f
    const val PARTICLE_EXTRA_RADIUS = 0.007f
    const val FLASH_MIN_RADIUS = 0.055f
    const val FLASH_RADIUS_GROWTH = 0.39f
    const val FLASH_ALPHA = 0.7f
}
