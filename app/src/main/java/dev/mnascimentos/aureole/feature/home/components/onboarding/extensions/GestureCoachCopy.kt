package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep

/** Title of the coach card; the edge scrub names the side the alphabet lives on. */
@StringRes
fun GestureCoachStep.coachTitleRes(isLeftHandedMode: Boolean): Int = when (this) {
    GestureCoachStep.OPEN_APPS -> R.string.home_onboarding_coach_apps_title
    GestureCoachStep.ALPHABET_SCRUB -> if (isLeftHandedMode) {
        R.string.home_onboarding_coach_scrub_title_left
    } else {
        R.string.home_onboarding_coach_scrub_title_right
    }
    GestureCoachStep.CUSTOMIZE -> R.string.home_onboarding_coach_customize_title
}

/** Body of the coach card: the instruction, or what to do next once the gesture succeeded. */
@StringRes
fun GestureCoachStep.coachBodyRes(succeeded: Boolean): Int = when (this) {
    GestureCoachStep.OPEN_APPS -> if (succeeded) {
        R.string.home_onboarding_coach_apps_done
    } else {
        R.string.home_onboarding_coach_apps_body
    }
    GestureCoachStep.ALPHABET_SCRUB -> if (succeeded) {
        R.string.home_onboarding_coach_scrub_done
    } else {
        R.string.home_onboarding_coach_scrub_body
    }
    GestureCoachStep.CUSTOMIZE -> if (succeeded) {
        R.string.home_onboarding_coach_customize_done
    } else {
        R.string.home_onboarding_coach_customize_body
    }
}
