package dev.mnascimentos.aureole.feature.home.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ActiveHomeGesturesTest {

    @Test
    fun `by default, should have no vertical swipe on`() {
        val active = MainUiState().activeHomeGestures()

        assertFalse(active.isSwipeDownOn)
        assertFalse(active.isSwipeUpOn)
        assertFalse(active.isAnyOn)
    }

    @Test
    fun `when only one toggle is on, should report just that swipe`() {
        val down = MainUiState(isSwipeDownNotificationsEnabled = true).activeHomeGestures()
        val up = MainUiState(isSwipeUpAllAppsEnabled = true).activeHomeGestures()

        assertEquals(ActiveHomeGestures(isSwipeDownOn = true, isSwipeUpOn = false), down)
        assertEquals(ActiveHomeGestures(isSwipeDownOn = false, isSwipeUpOn = true), up)
        assertTrue(down.isAnyOn && up.isAnyOn)
    }

    @Test
    fun `a touch on the side or bottom strips should belong to the system`() {
        fun zone(x: Float, y: Float) =
            isInSystemGestureZone(Offset(x, y), Size(1000f, 2000f), edgePx = 50f, bottomPx = 80f)

        assertTrue(zone(10f, 1000f))
        assertTrue(zone(990f, 1000f))
        assertTrue(zone(500f, 1950f))
        assertFalse(zone(500f, 1000f))
    }
}
