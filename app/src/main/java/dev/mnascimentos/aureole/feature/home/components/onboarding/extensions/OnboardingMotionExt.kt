package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import android.animation.ValueAnimator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode

/** How long the home shows alone after a task screen closes, before the checklist comes back. */
const val ONBOARDING_HOME_PEEK_MS = 900L

/** The shorter peek used when the system turned animations off. */
const val ONBOARDING_HOME_PEEK_REDUCED_MS = 300L

/** The peek length: the full one with animations on, the short one without. */
fun onboardingHomePeekMillis(areAnimatorsEnabled: Boolean = ValueAnimator.areAnimatorsEnabled()): Long =
    if (areAnimatorsEnabled) ONBOARDING_HOME_PEEK_MS else ONBOARDING_HOME_PEEK_REDUCED_MS

/**
 * False in previews and when the system turned animations off. Compose animations already follow the
 * system scale, but frame loops and particles must be skipped explicitly.
 */
@Composable
fun rememberOnboardingLoopsEnabled(): Boolean {
    val isInspection = LocalInspectionMode.current
    return remember(isInspection) { !isInspection && ValueAnimator.areAnimatorsEnabled() }
}
