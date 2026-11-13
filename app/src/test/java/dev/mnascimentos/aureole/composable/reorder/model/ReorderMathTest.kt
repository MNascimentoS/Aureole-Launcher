package dev.mnascimentos.aureole.composable.reorder.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

class ReorderMathTest {

    private val sizes = listOf(100f, 100f, 100f, 100f)
    private val spacing = 10f

    @Test
    fun `dragging down or right past half of the next slot moves one step towards the end`() {
        assertEquals(2, reorderTargetIndex(from = 1, offset = 56f, sizes = sizes, spacing = spacing))
    }

    @Test
    fun `dragging up or left past half of the previous slot moves one step towards the start`() {
        assertEquals(1, reorderTargetIndex(from = 2, offset = -56f, sizes = sizes, spacing = spacing))
    }

    @Test
    fun `an offset smaller than half a slot keeps the item in place`() {
        assertEquals(1, reorderTargetIndex(from = 1, offset = 54f, sizes = sizes, spacing = spacing))
        assertEquals(1, reorderTargetIndex(from = 1, offset = -54f, sizes = sizes, spacing = spacing))
        assertEquals(1, reorderTargetIndex(from = 1, offset = 0f, sizes = sizes, spacing = spacing))
    }

    @Test
    fun `a long drag passes several neighbours at once`() {
        assertEquals(3, reorderTargetIndex(from = 0, offset = 280f, sizes = sizes, spacing = spacing))
        assertEquals(0, reorderTargetIndex(from = 3, offset = -280f, sizes = sizes, spacing = spacing))
    }

    @Test
    fun `dragging past either end stops at the first or the last position`() {
        assertEquals(3, reorderTargetIndex(from = 2, offset = 5000f, sizes = sizes, spacing = spacing))
        assertEquals(0, reorderTargetIndex(from = 1, offset = -5000f, sizes = sizes, spacing = spacing))
        assertEquals(0, reorderTargetIndex(from = 0, offset = -80f, sizes = sizes, spacing = spacing))
        assertEquals(3, reorderTargetIndex(from = 3, offset = 80f, sizes = sizes, spacing = spacing))
    }

    @Test
    fun `a list of one item never moves`() {
        assertEquals(0, reorderTargetIndex(from = 0, offset = 500f, sizes = listOf(100f), spacing = spacing))
        assertEquals(0, reorderTargetIndex(from = 0, offset = -500f, sizes = listOf(100f), spacing = spacing))
    }

    @Test
    fun `an index outside the list is returned unchanged`() {
        assertEquals(7, reorderTargetIndex(from = 7, offset = 500f, sizes = sizes, spacing = spacing))
    }

    @Test
    fun `items of different sizes use the size of the neighbour being passed`() {
        val mixed = listOf(40f, 200f, 40f)
        assertEquals(0, reorderTargetIndex(from = 0, offset = 90f, sizes = mixed, spacing = 0f))
        assertEquals(1, reorderTargetIndex(from = 0, offset = 100f, sizes = mixed, spacing = 0f))
        // The leftover after passing the big slot points back and must not pass the small one.
        assertEquals(1, reorderTargetIndex(from = 0, offset = 110f, sizes = mixed, spacing = 0f))
    }

    @Test
    fun `shift is the sum of the slots jumped over, signed by direction`() {
        assertEquals(220f, reorderShift(from = 0, to = 2, sizes = sizes, spacing = spacing))
        assertEquals(-110f, reorderShift(from = 3, to = 2, sizes = sizes, spacing = spacing))
        assertEquals(0f, reorderShift(from = 1, to = 1, sizes = sizes, spacing = spacing))
        assertEquals(0f, reorderShift(from = 1, to = 9, sizes = sizes, spacing = spacing))
    }

    @Test
    fun `move takes the element out and puts it at the target index`() {
        assertEquals(listOf("b", "c", "a"), move(listOf("a", "b", "c"), 0, 2))
        assertEquals(listOf("c", "a", "b"), move(listOf("a", "b", "c"), 2, 0))
    }

    @Test
    fun `move with the same or an invalid index returns the same list`() {
        val list = listOf("a", "b")
        assertSame(list, move(list, 1, 1))
        assertSame(list, move(list, -1, 0))
        assertSame(list, move(list, 0, 2))
    }

    @Test
    fun `step target goes one place towards the chosen end and stops at the ends`() {
        assertEquals(0, reorderStepTarget(index = 1, size = 3, towardsStart = true))
        assertEquals(2, reorderStepTarget(index = 1, size = 3, towardsStart = false))
        assertNull(reorderStepTarget(index = 0, size = 3, towardsStart = true))
        assertNull(reorderStepTarget(index = 2, size = 3, towardsStart = false))
        assertNull(reorderStepTarget(index = 0, size = 1, towardsStart = false))
        assertNull(reorderStepTarget(index = 5, size = 3, towardsStart = true))
    }
}
