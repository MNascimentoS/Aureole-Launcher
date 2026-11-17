package dev.mnascimentos.aureole.feature.settings.model

import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LockScreenOptionTest {

    @Test
    fun `turning on the first time asks for the warning`() {
        assertEquals(LockScreenDialog.FIRST_TIME, lockScreenDialogFor(turnOn = true, isNoticeSeen = false))
    }

    @Test
    fun `turning on after the warning was seen changes right away`() {
        assertNull(lockScreenDialogFor(turnOn = true, isNoticeSeen = true))
    }

    @Test
    fun `turning off always asks what to do with the lock screen`() {
        assertEquals(LockScreenDialog.TURN_OFF, lockScreenDialogFor(turnOn = false, isNoticeSeen = true))
        assertEquals(LockScreenDialog.TURN_OFF, lockScreenDialogFor(turnOn = false, isNoticeSeen = false))
    }

    @Test
    fun `a failure shows the lock screen message and a success shows nothing`() {
        assertNull(lockScreenErrorOf(applied = true))
        assertEquals(TextResource.fromResId(R.string.settings_lock_screen_error), lockScreenErrorOf(applied = false))
    }
}
