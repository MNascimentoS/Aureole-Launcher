package dev.mnascimentos.aureole.feature.home.components.onboarding.model

/** Home settings and accessibility state that change what the gesture coach teaches. */
data class GestureCoachConfig(
    val isAlphabetScrubberDisabled: Boolean = false,
    val isLeftHandedMode: Boolean = false,
    /** TalkBack users cannot perform the demo gesture freely, so "Show me" appears right away. */
    val isTouchExplorationEnabled: Boolean = false
)
