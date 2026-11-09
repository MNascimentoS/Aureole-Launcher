package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import kotlin.math.cos
import kotlin.math.sin

private const val GOLDEN_ANGLE_RAD = 2.39996f
private const val PARTICLE_MIN_TRAVEL = 0.18f
private const val PARTICLE_TRAVEL_STEPS = 5
private const val PARTICLE_TRAVEL_STEP = 0.06f

/** Up to [OnboardingMotionSpec.MAX_PARTICLES] dots on a golden-angle spiral, flying out and fading. */
internal fun DrawScope.drawParticles(progress: Float, color: Color) {
    val reach = size.minDimension
    repeat(OnboardingMotionSpec.MAX_PARTICLES) { index ->
        val angle = index * GOLDEN_ANGLE_RAD
        val travel = PARTICLE_MIN_TRAVEL + (index % PARTICLE_TRAVEL_STEPS) * PARTICLE_TRAVEL_STEP
        val distance = reach * travel * progress
        drawCircle(
            color = color.copy(alpha = 1f - progress),
            radius = OnboardingMotionSpec.PARTICLE_RADIUS.toPx(),
            center = center + Offset(cos(angle) * distance, sin(angle) * distance)
        )
    }
}
