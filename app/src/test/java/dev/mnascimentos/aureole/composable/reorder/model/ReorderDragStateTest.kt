package dev.mnascimentos.aureole.composable.reorder.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ReorderDragStateTest {

    private val keys = listOf("a", "b", "c")

    private fun measuredState() = ReorderDragState().apply { keys.forEach { onMeasured(it, 100f) } }

    @Test
    fun `starting an unknown key does nothing`() {
        val state = measuredState()

        assertFalse(state.start("z", keys))
        assertNull(state.activeKey)
        assertFalse(state.isDragging)
    }

    @Test
    fun `dragging without a started item reports nothing`() {
        assertNull(measuredState().drag(500f, 0f))
    }

    @Test
    fun `a short drag follows the finger without swapping`() {
        val state = measuredState()
        state.start("a", keys)

        assertNull(state.drag(30f, 10f))
        assertEquals(30f, state.offset)
        assertFalse(state.release())
    }

    @Test
    fun `passing a neighbour reports the swap and keeps the item under the finger`() {
        val state = measuredState()
        state.start("a", keys)

        assertEquals(ReorderMove(0, 1), state.drag(60f, 10f))
        assertEquals(-50f, state.offset)
        assertEquals(ReorderMove(1, 2), state.drag(110f, 10f))
        assertNull(state.drag(500f, 10f))
        assertTrue(state.release())
        assertFalse(state.isDragging)
    }

    @Test
    fun `dragging back towards the start reports the swap in that direction`() {
        val state = measuredState()
        state.start("c", keys)

        assertEquals(ReorderMove(2, 0), state.drag(-200f, 0f))
    }

    @Test
    fun `an unmeasured neighbour takes the size of the dragged item`() {
        val state = ReorderDragState().apply { onMeasured("a", 100f) }
        state.start("a", keys)

        assertEquals(ReorderMove(0, 1), state.drag(55f, 0f))
    }

    @Test
    fun `settling moves the released item and clear leaves the list at rest`() {
        val state = measuredState()
        state.start("a", keys)
        state.drag(30f, 0f)
        state.release()

        state.settle("a", 12f)
        assertEquals(12f, state.offset)
        state.settle("b", 99f)
        assertEquals(12f, state.offset)
        state.clear("b")
        assertEquals("a", state.activeKey)

        state.clear("a")
        assertNull(state.activeKey)
        assertEquals(0f, state.offset)
    }

    @Test
    fun `a new drag is not disturbed by the previous item settling`() {
        val state = measuredState()
        state.start("a", keys)
        state.release()
        state.start("b", keys)
        state.drag(20f, 0f)

        state.settle("b", 99f)
        state.clear("b")

        assertEquals(20f, state.offset)
        assertEquals("b", state.activeKey)
        assertFalse(ReorderDragState().release())
    }
}
