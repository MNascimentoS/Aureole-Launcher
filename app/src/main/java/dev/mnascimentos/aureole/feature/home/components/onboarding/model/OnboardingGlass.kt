package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.chrisbanes.haze.HazeState

/** Frosted-glass settings shared by every onboarding surface; solid surfaces when Haze is off. */
data class OnboardingGlass(
    val hazeState: HazeState? = null,
    val isHazeEnabled: Boolean = false,
    val hazeOpacity: Float = DEFAULT_HAZE_OPACITY
) {
    companion object {
        const val DEFAULT_HAZE_OPACITY = 0.5f
    }
}
