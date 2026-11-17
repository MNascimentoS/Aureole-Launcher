package dev.mnascimentos.aureole.core.designsystem.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HeaderLogoPressTest {

    @Test
    fun `when pressed with animations on, should shrink, tilt and show the circle`() {
        val pose = headerLogoPose(isPressed = true, animationsEnabled = true)

        assertEquals(0.86f, pose.scale, 0f)
        assertEquals(-18f, pose.rotation, 0f)
        assertEquals(1f, pose.haloAlpha, 0f)
    }

    @Test
    fun `when released, should go back to rest`() {
        assertEquals(HeaderLogoRestPose, headerLogoPose(isPressed = false, animationsEnabled = true))
    }

    @Test
    fun `when the system animations are off, should never move`() {
        assertEquals(HeaderLogoRestPose, headerLogoPose(isPressed = true, animationsEnabled = false))
    }

    @Test
    fun `header action should be drawn only when allowed and it does something or has its own icon`() {
        assertTrue(hasHeaderAction(showHeaderAction = true, hasClick = true, hasCustomIcon = false))
        assertTrue(hasHeaderAction(showHeaderAction = true, hasClick = false, hasCustomIcon = true))
        assertFalse(hasHeaderAction(showHeaderAction = true, hasClick = false, hasCustomIcon = false))
        assertFalse(hasHeaderAction(showHeaderAction = false, hasClick = true, hasCustomIcon = true))
    }
}
