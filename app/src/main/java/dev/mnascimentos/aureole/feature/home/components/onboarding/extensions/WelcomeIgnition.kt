package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.WelcomeMotion
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Halo ignition: ring, logo, glow pulse, twinkle and the staggered texts, all started together. */
internal suspend fun playWelcomeIgnition(motion: WelcomeMotion) = coroutineScope {
    val delays = listOf(
        OnboardingMotionSpec.TITLE_DELAY_MS,
        OnboardingMotionSpec.SUBTITLE_DELAY_MS,
        OnboardingMotionSpec.BUTTONS_DELAY_MS
    )
    launch {
        val ringSpec = tween<Float>(
            OnboardingMotionSpec.RING_DRAW_MS,
            OnboardingMotionSpec.RING_DELAY_MS,
            FastOutSlowInEasing
        )
        motion.ring.animateTo(1f, ringSpec)
    }
    launch {
        delay(OnboardingMotionSpec.LOGO_DELAY_MS.toLong())
        motion.logo.animateTo(1f, spring(OnboardingMotionSpec.LOGO_DAMPING, Spring.StiffnessMediumLow))
    }
    launch {
        delay(OnboardingMotionSpec.LOGO_DELAY_MS.toLong())
        motion.glow.animateTo(1f, tween(OnboardingMotionSpec.GLOW_PULSE_MS / 2))
        motion.glow.animateTo(
            OnboardingMotionSpec.GLOW_PULSE_SCALE,
            infiniteRepeatable(tween(OnboardingMotionSpec.GLOW_PULSE_MS / 2), RepeatMode.Reverse)
        )
    }
    launch {
        val twinkleSpec = tween<Float>(OnboardingMotionSpec.GLOW_PULSE_MS, easing = LinearEasing)
        motion.twinkle.animateTo(1f, infiniteRepeatable(twinkleSpec))
    }
    motion.texts.forEachIndexed { index, text ->
        launch { text.animateTo(1f, tween(OnboardingMotionSpec.TEXT_ENTER_MS, delays[index])) }
    }
}
