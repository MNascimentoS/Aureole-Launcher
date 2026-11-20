package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import org.junit.Assert.assertEquals
import org.junit.Test

class GestureCoachCopyTest {

    @Test
    fun `title should follow the step and the alphabet side`() {
        assertEquals(R.string.home_onboarding_coach_apps_title, GestureCoachStep.OPEN_APPS.coachTitleRes(false))
        assertEquals(
            R.string.home_onboarding_coach_scrub_title_right,
            GestureCoachStep.ALPHABET_SCRUB.coachTitleRes(false)
        )
        assertEquals(
            R.string.home_onboarding_coach_scrub_title_left,
            GestureCoachStep.ALPHABET_SCRUB.coachTitleRes(true)
        )
        assertEquals(R.string.home_onboarding_coach_customize_title, GestureCoachStep.CUSTOMIZE.coachTitleRes(true))
        assertEquals(
            R.string.home_onboarding_coach_notifications_title,
            GestureCoachStep.NOTIFICATIONS.coachTitleRes(false)
        )
    }

    @Test
    fun `body should switch to the done copy once the gesture succeeded`() {
        val expected = mapOf(
            GestureCoachStep.OPEN_APPS to
                (R.string.home_onboarding_coach_apps_body to R.string.home_onboarding_coach_apps_done),
            GestureCoachStep.ALPHABET_SCRUB to
                (R.string.home_onboarding_coach_scrub_body to R.string.home_onboarding_coach_scrub_done),
            GestureCoachStep.NOTIFICATIONS to (
                R.string.home_onboarding_coach_notifications_body to
                    R.string.home_onboarding_coach_notifications_done
                ),
            GestureCoachStep.CUSTOMIZE to
                (R.string.home_onboarding_coach_customize_body to R.string.home_onboarding_coach_customize_done)
        )

        expected.forEach { (step, copy) ->
            assertEquals(copy.first, step.coachBodyRes(succeeded = false))
            assertEquals(copy.second, step.coachBodyRes(succeeded = true))
        }
    }
}
