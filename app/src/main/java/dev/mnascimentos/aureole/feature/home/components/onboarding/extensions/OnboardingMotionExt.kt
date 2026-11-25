package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import android.animation.ValueAnimator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec

/**
 * How long the home shows alone after a task screen closes, before the checklist comes back: time to see the item
 * and the bubble's ring fill (D-04).
 */
const val ONBOARDING_HOME_PEEK_MS = 1_400L

/** The shorter peek used when the system turned animations off. */
const val ONBOARDING_HOME_PEEK_REDUCED_MS = 300L

/** The peek length: the full one with animations on, the short one without. */
fun onboardingHomePeekMillis(areAnimatorsEnabled: Boolean = ValueAnimator.areAnimatorsEnabled()): Long =
    if (areAnimatorsEnabled) ONBOARDING_HOME_PEEK_MS else ONBOARDING_HOME_PEEK_REDUCED_MS

/**
 * How long a tapped step waits before its screen rises: the card first shrinks back into the pill (RF-B5), so the
 * two never move at once. No wait with animations off.
 */
fun checklistCollapseDelayMillis(areAnimatorsEnabled: Boolean = ValueAnimator.areAnimatorsEnabled()): Long =
    if (areAnimatorsEnabled) OnboardingMotionSpec.CHECKLIST_MORPH_MS.toLong() else 0L

/**
 * False in previews and when the system turned animations off. Compose animations already follow the
 * system scale, but frame loops and particles must be skipped explicitly.
 */
@Composable
fun rememberOnboardingLoopsEnabled(): Boolean {
    val isInspection = LocalInspectionMode.current
    return remember(isInspection) { !isInspection && ValueAnimator.areAnimatorsEnabled() }
}
