package dev.mnascimentos.aureole.core.designsystem.model

import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private val BOUNDS = SheetBounds(restMaxPx = 600f, expandedPx = 900f)

/** The sheet tells when a release would close it (relogio-home-sheet RF-D8). */
class SheetDismissFeedbackTest {

    private var armedCount = 0
    private val events = OverlaySheetEvents(
        onExpandedChanged = {},
        onDismiss = {},
        onTick = {},
        onDismissArmed = { armedCount++ }
    )

    private fun controller(motion: OverlaySheetMotion) =
        OverlaySheetController(motion, BOUNDS, isExpanded = false, events = events, scope = TestScope(), density = 1f)

    @Test
    fun `when the sheet is not held, should not be armed`() {
        val motion = OverlaySheetMotion().apply { restHeight = 400f }

        assertFalse(motion.isDismissArmed(BOUNDS))
    }

    @Test
    fun `when held under the close height, should be armed and above it, not`() {
        val motion = OverlaySheetMotion().apply { restHeight = 400f }

        motion.dragBy(delta = 100f, isExpanded = false, bounds = BOUNDS)
        assertFalse(motion.isDismissArmed(BOUNDS))
        motion.dragBy(delta = 30f, isExpanded = false, bounds = BOUNDS)
        assertTrue(motion.isDismissArmed(BOUNDS))
    }

    @Test
    fun `when a drag crosses the close height, should report it once on the way down`() {
        val motion = OverlaySheetMotion().apply { restHeight = 400f }
        val controller = controller(motion)

        controller.onDrag(150f)
        controller.onDrag(20f)
        assertEquals(1, armedCount)

        controller.onDrag(-100f)
        assertFalse(motion.isDismissArmed(BOUNDS))
        controller.onDrag(100f)
        assertEquals(2, armedCount)
    }

    @Test
    fun `when released under the close height, should close`() {
        assertTrue(isBelowDismiss(height = 270f, rest = 400f))
        assertFalse(isBelowDismiss(height = 290f, rest = 400f))
        assertEquals(SheetDetentTarget.Dismiss, resolveDetent(height = 270f, rest = 400f, expanded = 900f, velocity = 0f))
    }
}
