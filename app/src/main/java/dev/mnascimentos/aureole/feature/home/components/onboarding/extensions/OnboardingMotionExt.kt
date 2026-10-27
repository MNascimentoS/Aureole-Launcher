package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import android.animation.ValueAnimator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode

/**
 * False in previews and when the system turned animations off. Compose animations already follow the
 * system scale, but frame loops and particles must be skipped explicitly.
 */
@Composable
fun rememberOnboardingLoopsEnabled(): Boolean {
    val isInspection = LocalInspectionMode.current
    return remember(isInspection) { !isInspection && ValueAnimator.areAnimatorsEnabled() }
}
