package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D

/** Animated values of the welcome ignition. Readers access `.value` in draw or layer phases only. */
class WelcomeMotion(isSettled: Boolean) {
    private val start: (Float) -> Float = { initial -> if (isSettled) 1f else initial }

    val ring: Animatable<Float, AnimationVector1D> = Animatable(start(0f))
    val logo: Animatable<Float, AnimationVector1D> = Animatable(start(0f))
    val glow: Animatable<Float, AnimationVector1D> = Animatable(start(OnboardingMotionSpec.GLOW_START_SCALE))
    val texts: List<Animatable<Float, AnimationVector1D>> = List(TEXT_LINES) { Animatable(start(0f)) }
    val twinkle: Animatable<Float, AnimationVector1D> = Animatable(0f)
    val exit: Animatable<Float, AnimationVector1D> = Animatable(0f)

    val logoScale: Float
        get() = OnboardingMotionSpec.LOGO_START_SCALE + (1f - OnboardingMotionSpec.LOGO_START_SCALE) * logo.value

    val haloBurst: Float
        get() = 1f + (OnboardingMotionSpec.WELCOME_EXIT_SCALE - 1f) * exit.value

    companion object {
        /** Title, subtitle and buttons enter one after another. */
        const val TEXT_LINES = 3
    }
}
