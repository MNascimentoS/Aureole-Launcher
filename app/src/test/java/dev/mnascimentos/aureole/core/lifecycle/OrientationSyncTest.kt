package dev.mnascimentos.aureole.core.lifecycle

import android.content.res.Configuration
import org.junit.Assert.assertEquals
import org.junit.Test

class OrientationSyncTest {

    private val loads = mutableListOf<Boolean>()
    private val sync = OrientationSync { loads += it }

    @Test
    fun `an undefined orientation should not reload on a change and keep the last on resume`() {
        sync.check(Configuration.ORIENTATION_UNDEFINED, current = false)
        assertEquals(emptyList<Boolean>(), loads)

        sync.reload(Configuration.ORIENTATION_LANDSCAPE, current = false)
        sync.reload(Configuration.ORIENTATION_UNDEFINED, current = false)

        assertEquals(listOf(true, true), loads)
    }

    @Test
    fun `back from an app in landscape with the device in portrait, should return to portrait`() {
        // The change to landscape reached the home while it was behind the video.
        sync.check(Configuration.ORIENTATION_LANDSCAPE, current = false)
        // The rotation back happened while the home was stopped: no change arrives, onStart checks.
        sync.check(Configuration.ORIENTATION_PORTRAIT, current = true)

        assertEquals(listOf(true, false), loads)
    }

    @Test
    fun `a change should compare with the load asked for, not with the state that lags behind it`() {
        // Resume asked for landscape while the state still says portrait (the load is in flight).
        sync.reload(Configuration.ORIENTATION_LANDSCAPE, current = false)
        // The device is back in portrait: comparing with the state (portrait) would skip this reload.
        sync.check(Configuration.ORIENTATION_PORTRAIT, current = false)

        assertEquals(listOf(true, false), loads)
        assertEquals(false, sync.requested)
    }

    @Test
    fun `the same orientation twice should load once`() {
        sync.check(Configuration.ORIENTATION_LANDSCAPE, current = false)
        sync.check(Configuration.ORIENTATION_LANDSCAPE, current = false)

        assertEquals(listOf(true), loads)
    }
}
