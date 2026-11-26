package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import dev.mnascimentos.aureole.core.data.model.GestureCoachStep
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GestureCoachConfigTest {

    @Test
    fun `when the drawer is open, home gestures should be hidden`() {
        val config = GestureCoachConfig(isAllAppsOpen = true)

        assertTrue(config.isHiddenOverDrawer(GestureCoachStep.CUSTOMIZE))
    }

    @Test
    fun `when the drawer is open, drawer gestures should still show`() {
        val config = GestureCoachConfig(isAllAppsOpen = true)

        assertFalse(config.isHiddenOverDrawer(GestureCoachStep.OPEN_APPS))
        assertFalse(config.isHiddenOverDrawer(GestureCoachStep.ALPHABET_SCRUB))
    }

    @Test
    fun `when the home is on screen, no gesture should be hidden`() {
        GestureCoachStep.entries.forEach { assertFalse(GestureCoachConfig().isHiddenOverDrawer(it)) }
    }
}
