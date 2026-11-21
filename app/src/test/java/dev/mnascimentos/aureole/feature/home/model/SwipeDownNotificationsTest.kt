package dev.mnascimentos.aureole.feature.home.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SwipeDownNotificationsTest {

    private val threshold = 100f

    // region verticalSwipeOf

    @Test
    fun `a short swipe should be no swipe yet`() {
        assertEquals(VerticalSwipe.NONE, verticalSwipeOf(totalDx = 0f, totalDy = 90f, thresholdPx = threshold))
    }

    @Test
    fun `a long mostly vertical swipe should be up or down`() {
        assertEquals(VerticalSwipe.DOWN, verticalSwipeOf(totalDx = 20f, totalDy = 150f, thresholdPx = threshold))
        assertEquals(VerticalSwipe.UP, verticalSwipeOf(totalDx = 20f, totalDy = -150f, thresholdPx = threshold))
    }

    @Test
    fun `a diagonal swipe should not count as vertical`() {
        assertEquals(VerticalSwipe.NONE, verticalSwipeOf(totalDx = 120f, totalDy = 150f, thresholdPx = threshold))
    }

    // endregion

    // region conditions

    @Test
    fun `with the toggle on and the plain home, should open the notifications`() {
        assertTrue(SwipeDownConditions(isEnabled = true).opensNotifications())
    }

    @Test
    fun `with the toggle off, should never open`() {
        assertFalse(SwipeDownConditions(isEnabled = false).opensNotifications())
    }

    @Test
    fun `when scrollable content moved during the gesture, should not open`() {
        assertFalse(SwipeDownConditions(isEnabled = true, hasScrolledContent = true).opensNotifications())
    }

    @Test
    fun `with a sheet, folder or the drawer in front, should not open`() {
        assertFalse(SwipeDownConditions(isEnabled = true, isOverlayActive = true).opensNotifications())
    }

    @Test
    fun `in edit mode, should not open`() {
        assertFalse(SwipeDownConditions(isEnabled = true, isEditing = true).opensNotifications())
    }

    @Test
    fun `when the finger started on the alphabet edge, should not open`() {
        assertFalse(SwipeDownConditions(isEnabled = true, isEdgeGesture = true).opensNotifications())
    }

    // endregion

    // region nested scroll tracker

    @Test
    fun `a list at its top should open once the leftover passes the threshold`() {
        val tracker = SwipeScrollTracker(threshold)

        assertFalse(tracker.onPostScroll(consumedY = 0f, availableY = 60f))
        assertTrue(tracker.onPostScroll(consumedY = 0f, availableY = 60f))
        assertFalse(tracker.hasScrolledContent)
    }

    @Test
    fun `with nothing scrollable under the finger, no nested scroll arrives and the swipe counts as at the top`() {
        val tracker = SwipeScrollTracker(threshold)
        tracker.reset()

        val conditions = SwipeDownConditions(isEnabled = true, hasScrolledContent = tracker.hasScrolledContent)

        assertTrue(conditions.opensNotifications())
    }

    @Test
    fun `with nothing scrollable under the finger and the toggle off, should not open`() {
        val tracker = SwipeScrollTracker(threshold)

        val conditions = SwipeDownConditions(isEnabled = false, hasScrolledContent = tracker.hasScrolledContent)

        assertFalse(conditions.opensNotifications())
    }

    @Test
    fun `a list that scrolled during the gesture should block the open in the conditions too`() {
        val tracker = SwipeScrollTracker(threshold)
        tracker.onPostScroll(consumedY = 40f, availableY = 0f)

        val conditions = SwipeDownConditions(isEnabled = true, hasScrolledContent = tracker.hasScrolledContent)

        assertFalse(conditions.opensNotifications())
    }

    @Test
    fun `should open only once per gesture`() {
        val tracker = SwipeScrollTracker(threshold)

        assertTrue(tracker.onPostScroll(consumedY = 0f, availableY = 150f))
        assertFalse(tracker.onPostScroll(consumedY = 0f, availableY = 150f))
    }

    @Test
    fun `a list that could still scroll up should keep the gesture, even after reaching its top`() {
        val tracker = SwipeScrollTracker(threshold)

        assertFalse(tracker.onPostScroll(consumedY = 40f, availableY = 0f))
        assertFalse(tracker.onPostScroll(consumedY = 0f, availableY = 200f))
        assertTrue(tracker.hasScrolledContent)
    }

    @Test
    fun `an upward leftover should never open`() {
        val tracker = SwipeScrollTracker(threshold)

        assertFalse(tracker.onPostScroll(consumedY = 0f, availableY = -300f))
    }

    @Test
    fun `reset should start a new gesture`() {
        val tracker = SwipeScrollTracker(threshold)
        tracker.onPostScroll(consumedY = 40f, availableY = 0f)

        tracker.reset()

        assertFalse(tracker.hasScrolledContent)
        assertTrue(tracker.onPostScroll(consumedY = 0f, availableY = 150f))
    }

    // endregion

    // region opensHome

    @Test
    fun `an up swipe should open only with the swipe up on, whatever the swipe down is`() {
        assertTrue(VerticalSwipe.UP.opensHome(isUpEnabled = true, isDownEnabled = false))
        assertFalse(VerticalSwipe.UP.opensHome(isUpEnabled = false, isDownEnabled = true))
    }

    @Test
    fun `a down swipe should open only with the swipe down on, whatever the swipe up is`() {
        assertTrue(VerticalSwipe.DOWN.opensHome(isUpEnabled = false, isDownEnabled = true))
        assertFalse(VerticalSwipe.DOWN.opensHome(isUpEnabled = true, isDownEnabled = false))
    }

    @Test
    fun `no swipe yet should never open anything`() {
        assertFalse(VerticalSwipe.NONE.opensHome(isUpEnabled = true, isDownEnabled = true))
    }

    // endregion

    // region swipe up (all apps)

    @Test
    fun `a swipe up tracker should open on the delta left by a scrollable already at its bottom`() {
        val tracker = SwipeScrollTracker(threshold, VerticalSwipe.UP)

        assertFalse(tracker.onPostScroll(consumedY = 0f, availableY = -60f))
        assertTrue(tracker.onPostScroll(consumedY = 0f, availableY = -60f))
    }

    @Test
    fun `a swipe up tracker should open with nothing scrollable under the finger`() {
        assertTrue(SwipeScrollTracker(threshold, VerticalSwipe.UP).onPostScroll(consumedY = 0f, availableY = -150f))
    }

    @Test
    fun `a swipe up tracker should not open when the scrollable scrolled first`() {
        val tracker = SwipeScrollTracker(threshold, VerticalSwipe.UP)

        assertFalse(tracker.onPostScroll(consumedY = -40f, availableY = -10f))
        assertFalse(tracker.onPostScroll(consumedY = 0f, availableY = -200f))
        assertTrue(tracker.hasScrolledContent)
    }

    @Test
    fun `a swipe up tracker should ignore a downward delta`() {
        assertFalse(SwipeScrollTracker(threshold, VerticalSwipe.UP).onPostScroll(consumedY = 0f, availableY = 150f))
    }

    @Test
    fun `a swipe down tracker should ignore an upward delta`() {
        assertFalse(SwipeScrollTracker(threshold).onPostScroll(consumedY = 0f, availableY = -150f))
    }

    @Test
    fun `the swipe up should open the drawer only with its toggle on and nothing in front`() {
        assertTrue(SwipeUpConditions(isEnabled = true).opensAllApps())
        assertFalse(SwipeUpConditions(isEnabled = false).opensAllApps())
        assertFalse(SwipeUpConditions(isEnabled = true, hasScrolledContent = true).opensAllApps())
        assertFalse(SwipeUpConditions(isEnabled = true, isOverlayActive = true).opensAllApps())
        assertFalse(SwipeUpConditions(isEnabled = true, isEditing = true).opensAllApps())
        assertFalse(SwipeUpConditions(isEnabled = true, isEdgeGesture = true).opensAllApps())
    }

    // endregion
}
