package dev.mnascimentos.aureole.feature.home.components.containers.folder.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DragSelectionTest {

    @Test
    fun `start on an unmarked app marks it and every app reached`() {
        val selected = mutableListOf("a")
        val drag = DragSelection(selected)

        assertTrue(drag.start("b", isSelected = false))
        assertEquals(DragSelectionMode.Mark, drag.mode)
        assertTrue(drag.enter("c"))
        assertTrue(drag.enter("a"))

        assertEquals(listOf("a", "b", "c"), selected)
        assertEquals(2, drag.end())
    }

    @Test
    fun `start on a marked app unmarks it and every app reached`() {
        val selected = mutableListOf("a", "b", "c")
        val drag = DragSelection(selected)

        drag.start("a", isSelected = true)
        assertEquals(DragSelectionMode.Unmark, drag.mode)
        drag.enter("c")
        drag.enter("z")

        assertEquals(listOf("b"), selected)
        assertEquals(2, drag.end())
    }

    @Test
    fun `each app is applied and ticks only once per gesture`() {
        val selected = mutableListOf<String>()
        val drag = DragSelection(selected)

        drag.start("a", isSelected = false)
        assertTrue(drag.enter("b"))
        assertFalse(drag.enter("b"))
        assertFalse(drag.enter("a"))

        assertEquals(listOf("a", "b"), selected)
    }

    @Test
    fun `enter without a gesture changes nothing`() {
        val selected = mutableListOf<String>()
        val drag = DragSelection(selected)

        assertFalse(drag.isActive)
        assertFalse(drag.enter("a"))
        assertEquals(0, drag.end())
        assertTrue(selected.isEmpty())
    }

    @Test
    fun `end resets the gesture so the next one starts fresh`() {
        val selected = mutableListOf<String>()
        val drag = DragSelection(selected)

        drag.start("a", isSelected = false)
        assertTrue(drag.isActive)
        assertEquals(1, drag.end())
        assertFalse(drag.isActive)
        assertNull(drag.mode)

        drag.start("a", isSelected = true)
        assertEquals(1, drag.end())
        assertTrue(selected.isEmpty())
    }
}
