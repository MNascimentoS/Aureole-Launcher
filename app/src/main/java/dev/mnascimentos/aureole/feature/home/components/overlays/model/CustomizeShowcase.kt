package dev.mnascimentos.aureole.feature.home.components.overlays.model

import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** How long the Customize showcase stays open before it closes on its own (D-01). */
const val CUSTOMIZE_SHOWCASE_MS = 3_000L

/**
 * The Customize sheet opened as the coach's customize step (by the long press or "Show me"): it opens as a showcase,
 * where nothing responds and it closes itself after [CUSTOMIZE_SHOWCASE_MS] (RF-01 to RF-03). Outside the tour, false.
 */
val MainUiState.isCustomizeShowcase: Boolean
    get() = showCustomizeBottomSheet &&
        onboarding.stage == OnboardingStage.GESTURES &&
        onboarding.succeededStep == GestureCoachStep.CUSTOMIZE

/** How full the showcase ring is [elapsedMs] after the sheet opened: linear from 0 to 1 over [durationMs]. */
fun customizeShowcaseProgress(elapsedMs: Long, durationMs: Long = CUSTOMIZE_SHOWCASE_MS): Float {
    if (durationMs <= 0L) return 1f
    return (elapsedMs.toFloat() / durationMs).coerceIn(0f, 1f)
}
