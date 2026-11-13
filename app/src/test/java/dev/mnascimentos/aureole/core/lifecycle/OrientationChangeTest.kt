package dev.mnascimentos.aureole.core.lifecycle

import android.content.res.Configuration
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OrientationChangeTest {

    @Test
    fun `when the orientation is landscape, should report landscape`() {
        assertTrue(isLandscapeOrientation(Configuration.ORIENTATION_LANDSCAPE))
        assertFalse(isLandscapeOrientation(Configuration.ORIENTATION_PORTRAIT))
    }

    @Test
    fun `when portrait turns landscape, should return true`() {
        assertEquals(true, landscapeChange(false, Configuration.ORIENTATION_LANDSCAPE))
    }

    @Test
    fun `when landscape turns portrait, should return false`() {
        assertEquals(false, landscapeChange(true, Configuration.ORIENTATION_PORTRAIT))
    }

    @Test
    fun `when the orientation did not change, should return null`() {
        assertNull(landscapeChange(true, Configuration.ORIENTATION_LANDSCAPE))
        assertNull(landscapeChange(false, Configuration.ORIENTATION_PORTRAIT))
    }

    @Test
    fun `when the orientation is undefined, should return null`() {
        assertNull(landscapeChange(false, Configuration.ORIENTATION_UNDEFINED))
    }
}
