package dev.mnascimentos.aureole.composable.reorder.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private const val VIEWPORT = 400f
private const val EDGE = 50f
private const val SPEED = 1000f
private const val FRAME = 0.016f
private const val DELTA = 0.001f

class ReorderAutoScrollMathTest {

    private fun delta(pointer: Float, viewport: Float = VIEWPORT, frame: Float = FRAME) =
        reorderAutoScrollDelta(pointer, viewport, EDGE, SPEED, frame)

    @Test
    fun `the centre of the viewport does not scroll`() {
        assertEquals(0f, delta(VIEWPORT / 2), DELTA)
        assertEquals(0f, delta(EDGE), DELTA)
        assertEquals(0f, delta(VIEWPORT - EDGE), DELTA)
    }

    @Test
    fun `the start band scrolls backwards, faster closer to the edge`() {
        val inner = delta(EDGE * 0.75f)
        val outer = delta(EDGE * 0.25f)

        assertTrue(inner < 0f)
        assertTrue(outer < inner)
        assertEquals(-SPEED * FRAME * 0.5f, delta(EDGE / 2), DELTA)
    }

    @Test
    fun `the end band scrolls forwards, faster closer to the edge`() {
        val inner = delta(VIEWPORT - EDGE * 0.75f)
        val outer = delta(VIEWPORT - EDGE * 0.25f)

        assertTrue(inner > 0f)
        assertTrue(outer > inner)
        assertEquals(SPEED * FRAME * 0.5f, delta(VIEWPORT - EDGE / 2), DELTA)
    }

    @Test
    fun `at the edges and beyond the speed is capped at the maximum`() {
        assertEquals(-SPEED * FRAME, delta(0f), DELTA)
        assertEquals(-SPEED * FRAME, delta(-200f), DELTA)
        assertEquals(SPEED * FRAME, delta(VIEWPORT), DELTA)
        assertEquals(SPEED * FRAME, delta(VIEWPORT + 200f), DELTA)
    }

    @Test
    fun `a long frame never scrolls more than a capped step`() {
        assertEquals(delta(VIEWPORT, frame = 0.05f), delta(VIEWPORT, frame = 2f), DELTA)
        assertEquals(0f, delta(VIEWPORT, frame = -1f), DELTA)
    }

    @Test
    fun `a viewport smaller than two bands splits in half`() {
        val small = 60f

        assertEquals(0f, delta(30f, viewport = small), DELTA)
        assertTrue(delta(10f, viewport = small) < 0f)
        assertTrue(delta(50f, viewport = small) > 0f)
        assertEquals(SPEED * FRAME, delta(small, viewport = small), DELTA)
    }

    @Test
    fun `an empty viewport, band or speed does not scroll`() {
        assertEquals(0f, delta(0f, viewport = 0f), DELTA)
        assertEquals(0f, reorderAutoScrollDelta(0f, VIEWPORT, 0f, SPEED, FRAME), DELTA)
        assertEquals(0f, reorderAutoScrollDelta(0f, VIEWPORT, EDGE, 0f, FRAME), DELTA)
    }

    @Test
    fun `a list that cannot scroll that way does not scroll`() {
        assertEquals(0f, reorderAllowedScroll(-5f, canScrollBackward = false, canScrollForward = true), DELTA)
        assertEquals(0f, reorderAllowedScroll(5f, canScrollBackward = true, canScrollForward = false), DELTA)
        assertEquals(0f, reorderAllowedScroll(5f, canScrollBackward = false, canScrollForward = false), DELTA)
        assertEquals(-5f, reorderAllowedScroll(-5f, canScrollBackward = true, canScrollForward = false), DELTA)
        assertEquals(5f, reorderAllowedScroll(5f, canScrollBackward = false, canScrollForward = true), DELTA)
    }

    @Test
    fun `the axis position counts from the start of the list`() {
        val size = IntSize(400, 300)
        val point = Offset(100f, 50f)

        assertEquals(50f, reorderAxisPosition(point, size, ReorderOrientation.Vertical, isRtl = true), DELTA)
        assertEquals(100f, reorderAxisPosition(point, size, ReorderOrientation.Horizontal, isRtl = false), DELTA)
        assertEquals(300f, reorderAxisPosition(point, size, ReorderOrientation.Horizontal, isRtl = true), DELTA)
        assertEquals(300f, reorderAxisSize(size, ReorderOrientation.Vertical), DELTA)
        assertEquals(400f, reorderAxisSize(size, ReorderOrientation.Horizontal), DELTA)
    }

    @Test
    fun `the axis delta follows the reading order`() {
        val amount = Offset(10f, 20f)

        assertEquals(20f, reorderAxisDelta(amount, ReorderOrientation.Vertical, isRtl = true), DELTA)
        assertEquals(10f, reorderAxisDelta(amount, ReorderOrientation.Horizontal, isRtl = false), DELTA)
        assertEquals(-10f, reorderAxisDelta(amount, ReorderOrientation.Horizontal, isRtl = true), DELTA)
    }

    @Test
    fun `scrolling under the held item keeps it under the finger and swaps once it passes a neighbour`() {
        val keys = listOf("a", "b", "c")
        val state = ReorderDragState().apply { keys.forEach { onMeasured(it, 100f) } }
        state.start("a", keys)

        assertNull(state.onScrolled(30f, 0f))
        assertEquals(30f, state.offset, DELTA)
        assertEquals(ReorderMove(0, 1), state.onScrolled(30f, 0f))
        assertEquals(-40f, state.offset, DELTA)
    }

    @Test
    fun `scrolling without a held item changes nothing`() {
        val state = ReorderDragState()

        assertNull(state.onScrolled(500f, 0f))
        assertEquals(0f, state.offset, DELTA)
    }
}
