package dev.mnascimentos.aureole.feature.home.grid.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GridGestureTrackerTest {

    @Test
    fun `should be gesturing while any cell has begun and not ended`() {
        val tracker = GridGestureTracker()
        assertFalse(tracker.isGesturing)

        tracker.begin()
        tracker.begin()
        tracker.end()
        assertTrue(tracker.isGesturing)

        tracker.end()
        assertFalse(tracker.isGesturing)
    }

    @Test
    fun `an extra end should not leave a negative count`() {
        val tracker = GridGestureTracker()

        tracker.end()
        tracker.begin()

        assertTrue(tracker.isGesturing)
    }
}
