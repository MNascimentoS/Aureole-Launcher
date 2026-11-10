package dev.mnascimentos.aureole.feature.home.components.containers.folder.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FolderPickerGridGeometryTest {

    private val cells = listOf(
        PickerCellBounds(key = "header_selected", x = 0, y = 0, width = 400, height = 40),
        PickerCellBounds(key = selectedRowKey("sel.app"), x = 0, y = 40, width = 400, height = 60),
        PickerCellBounds(key = remainingCellKey("a"), x = 0, y = 100, width = 100, height = 90),
        PickerCellBounds(key = remainingCellKey("b"), x = 104, y = 100, width = 100, height = 90),
        PickerCellBounds(key = 42, x = 208, y = 100, width = 100, height = 90)
    )

    @Test
    fun `hit test finds the app cell or row under the finger`() {
        assertEquals("a", hitTestCell(50f, 150f, cells))
        assertEquals("b", hitTestCell(104f, 100f, cells))
        assertEquals("sel.app", hitTestCell(300f, 60f, cells))
    }

    @Test
    fun `hit test ignores headers, gaps, foreign keys and points outside`() {
        assertNull(hitTestCell(10f, 10f, cells))
        assertNull(hitTestCell(102f, 150f, cells))
        assertNull(hitTestCell(250f, 150f, cells))
        assertNull(hitTestCell(50f, 500f, cells))
        assertNull(hitTestCell(50f, 150f, emptyList()))
    }

    @Test
    fun `picker keys map back to the package`() {
        assertEquals("x.y", packageOfPickerKey(selectedRowKey("x.y")))
        assertEquals("x.y", packageOfPickerKey(remainingCellKey("x.y")))
        assertNull(packageOfPickerKey("header_remaining"))
        assertNull(packageOfPickerKey(7))
    }

    @Test
    fun `auto scroll goes up near the top, down near the bottom and stops in the middle`() {
        assertEquals(-10f, edgeAutoScrollDelta(0f, 1000f, 100f, 10f), 0.001f)
        assertEquals(-5f, edgeAutoScrollDelta(50f, 1000f, 100f, 10f), 0.001f)
        assertEquals(0f, edgeAutoScrollDelta(500f, 1000f, 100f, 10f), 0.001f)
        assertEquals(5f, edgeAutoScrollDelta(950f, 1000f, 100f, 10f), 0.001f)
        assertEquals(10f, edgeAutoScrollDelta(1200f, 1000f, 100f, 10f), 0.001f)
        assertEquals(-10f, edgeAutoScrollDelta(-80f, 1000f, 100f, 10f), 0.001f)
    }

    @Test
    fun `auto scroll is off without a viewport or an edge`() {
        assertEquals(0f, edgeAutoScrollDelta(0f, 0f, 100f, 10f), 0.001f)
        assertEquals(0f, edgeAutoScrollDelta(0f, 1000f, 0f, 10f), 0.001f)
    }

    @Test
    fun `scrubber letter strips accents and buckets non letters`() {
        assertEquals('C', scrubberLetterOf("Câmera"))
        assertEquals('E', scrubberLetterOf("  éclair"))
        assertEquals(NON_LETTER_BUCKET, scrubberLetterOf("9gag"))
        assertEquals(NON_LETTER_BUCKET, scrubberLetterOf("@home"))
        assertEquals(NON_LETTER_BUCKET, scrubberLetterOf(""))
    }

    @Test
    fun `letter index keeps the first lazy index of each letter in grid order`() {
        val labels = listOf("1Password", "Ábaco", "Agenda", "Banco", "Câmera", "Calendário")

        val index = letterIndex(labels, offset = 3) { it }

        assertEquals(listOf('#', 'A', 'B', 'C'), index.keys.toList())
        assertEquals(mapOf('#' to 3, 'A' to 4, 'B' to 6, 'C' to 7), index)
        assertEquals(emptyMap<Char, Int>(), letterIndex(emptyList<String>()) { it })
    }

    @Test
    fun `grid order ignores case and accents`() {
        val sorted = sortedForPickerGrid(listOf("banco", "Zebra", "Ábaco", "agenda")) { it }

        assertEquals(listOf("Ábaco", "agenda", "banco", "Zebra"), sorted)
    }

    @Test
    fun `first grid cell comes after both headers and the selected rows`() {
        assertEquals(3, firstGridCellIndex(0))
        assertEquals(3, firstGridCellIndex(1))
        assertEquals(5, firstGridCellIndex(3))
    }
}
