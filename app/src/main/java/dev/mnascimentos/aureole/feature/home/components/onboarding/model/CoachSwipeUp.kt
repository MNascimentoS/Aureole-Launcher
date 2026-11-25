package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.core.data.model.OnboardingStage

/**
 * The coach teaches "Swipe up", which only works with Settings › Behavior › "Swipe up opens all apps" on, and that
 * setting is off by default. True when the tour has just reached the gestures and the setting is still off, so it
 * is switched on for the user (only this one: the swipe-down notifications setting is left alone).
 */
fun coachNeedsSwipeUp(previous: OnboardingStage, current: OnboardingStage, isSwipeUpEnabled: Boolean): Boolean =
    previous != OnboardingStage.GESTURES && current == OnboardingStage.GESTURES && !isSwipeUpEnabled
