package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.ui.unit.Constraints
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SheetEdgeToEdgeRowTest {

    @Test
    fun `the row should grow by the inset on both sides`() {
        assertEquals(1080 + 2 * 42, edgeToEdgeWidth(maxWidth = 1080, insetPx = 42))
        assertEquals(1080, edgeToEdgeWidth(maxWidth = 1080, insetPx = -5))
    }

    @Test
    fun `an unbounded parent should leave the row as it is`() {
        assertNull(edgeToEdgeWidth(maxWidth = Constraints.Infinity, insetPx = 42))
    }
}
