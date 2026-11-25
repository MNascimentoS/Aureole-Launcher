package dev.mnascimentos.aureole.feature.home.model

import org.junit.Assert.assertEquals
import org.junit.Test

class GestureLockTest {

    @Test
    fun `before the touch slop, should stay undecided`() {
        assertEquals(GestureLock.UNDECIDED, gestureLockOf(2f, 5f, slopPx = 8f))
    }

    @Test
    fun `past the slop going mostly down or up, should lock vertical`() {
        assertEquals(GestureLock.VERTICAL, gestureLockOf(3f, 20f, slopPx = 8f))
        assertEquals(GestureLock.VERTICAL, gestureLockOf(-3f, -20f, slopPx = 8f))
    }

    @Test
    fun `past the slop going sideways or diagonal, should lock horizontal`() {
        assertEquals(GestureLock.HORIZONTAL, gestureLockOf(20f, 3f, slopPx = 8f))
        assertEquals(GestureLock.HORIZONTAL, gestureLockOf(15f, 15f, slopPx = 8f))
    }
}
