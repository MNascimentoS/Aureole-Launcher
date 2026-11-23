package dev.mnascimentos.aureole.feature.home.components.clock.model

import dev.mnascimentos.aureole.core.data.model.ClockConfig
import dev.mnascimentos.aureole.core.data.model.OnboardingStage

/**
 * The look to put back when the clock editor closes (RF-D7, D-04): the [snapshot] taken when it opened, or null
 * when nothing changed (or there is no snapshot), so no "Undo" is offered.
 */
fun clockUndoTarget(snapshot: ClockConfig?, current: ClockConfig): ClockConfig? =
    snapshot?.takeIf { it != current }

/**
 * Whether the clock editor shows the "Drag down to close · already saved" notice (RF-D2, D-07): only while the
 * onboarding runs and until the user taps "Got it".
 */
fun showsClockSaveHint(stage: OnboardingStage, isDismissed: Boolean): Boolean =
    stage != OnboardingStage.COMPLETED && !isDismissed
