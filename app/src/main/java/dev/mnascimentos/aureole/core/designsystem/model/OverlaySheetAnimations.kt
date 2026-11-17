package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring

internal suspend fun OverlaySheetMotion.animateHeight(from: Float, to: Float) {
    animate(
        initialValue = from,
        targetValue = to,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        )
    ) { value, _ -> pinnedHeight = value }
    pinnedHeight = null
}

/** Goes up to [peak] when it is above [from], then springs back to [rest] with a small bounce. */
internal suspend fun OverlaySheetMotion.animateSpringBack(from: Float, peak: Float, rest: Float) {
    var start = from
    if (peak > from) {
        animate(
            initialValue = from,
            targetValue = peak,
            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessHigh)
        ) { value, _ -> pinnedHeight = value }
        start = peak
    }
    animate(
        initialValue = start,
        targetValue = rest,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    ) { value, _ -> pinnedHeight = value }
    pinnedHeight = null
}
