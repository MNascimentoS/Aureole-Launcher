package dev.mnascimentos.aureole.feature.home.components.clock.model

import dev.mnascimentos.aureole.core.data.model.ClockConfig
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Undo of the clock editor (RF-D7) and its onboarding notice (RF-D2). */
class ClockEditorSessionTest {

    private val opened = ClockConfig(alignment = "START")

    @Test
    fun `when the look changed, should offer to put back the snapshot`() {
        assertEquals(opened, clockUndoTarget(opened, opened.copy(alignment = "END")))
    }

    @Test
    fun `when the look is the same, should offer nothing`() {
        assertNull(clockUndoTarget(opened, opened.copy()))
    }

    @Test
    fun `when there is no snapshot, should offer nothing`() {
        assertNull(clockUndoTarget(null, opened))
    }

    @Test
    fun `when the onboarding runs and the notice was not dismissed, should show it`() {
        OnboardingStage.entries.filter { it != OnboardingStage.COMPLETED }.forEach { stage ->
            assertTrue(stage.name, showsClockSaveHint(stage, isDismissed = false))
        }
    }

    @Test
    fun `when the onboarding is over or the notice was dismissed, should hide it`() {
        assertFalse(showsClockSaveHint(OnboardingStage.COMPLETED, isDismissed = false))
        assertFalse(showsClockSaveHint(OnboardingStage.CHECKLIST, isDismissed = true))
    }
}
