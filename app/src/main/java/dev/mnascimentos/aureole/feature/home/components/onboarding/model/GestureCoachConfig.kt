package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.core.data.model.GestureCoachStep

/** Home settings and accessibility state that change what the gesture coach teaches. */
data class GestureCoachConfig(
    val isAlphabetScrubberDisabled: Boolean = false,
    val isLeftHandedMode: Boolean = false,
    /** TalkBack users cannot perform the demo gesture freely, so "Show me" appears right away. */
    val isTouchExplorationEnabled: Boolean = false,
    val isLandscape: Boolean = false,
    /** No letters yet (apps still loading): the scrub step waits for them. */
    val isAlphabetLoading: Boolean = false,
    /** The all-apps drawer is open: the steps that belong to the home wait for the user to come back. */
    val isAllAppsOpen: Boolean = false
)

/** The scrub step has nothing to scrub until the apps load: the card waits and hides "Show me". */
fun GestureCoachConfig.isWaitingForAlphabet(step: GestureCoachStep): Boolean =
    step == GestureCoachStep.ALPHABET_SCRUB && isAlphabetLoading

/** Touch and hold is a home gesture: it never shows over the all-apps drawer (RF-02). */
fun GestureCoachConfig.isHiddenOverDrawer(step: GestureCoachStep): Boolean =
    isAllAppsOpen && step == GestureCoachStep.CUSTOMIZE
