package dev.mnascimentos.aureole.core.designsystem.utils

import android.animation.ValueAnimator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode

/**
 * False in previews and when the system turned animations off ("Remove animations"). Compose animations already
 * follow the system scale, but they still jump to the end: what must not move at all (a press pose, a spinning
 * ring) checks this and stays still.
 */
@Composable
fun rememberSystemAnimationsEnabled(): Boolean {
    val isInspection = LocalInspectionMode.current
    return remember(isInspection) { !isInspection && ValueAnimator.areAnimatorsEnabled() }
}
