package dev.mnascimentos.aureole.core.designsystem.model

import org.junit.Assert.assertEquals
import org.junit.Test

/** Landscape with a preview puts the preview beside the sheet; everything else keeps the portrait layout. */
class OverlayLayoutTest {

    @Test
    fun `landscape with a preview goes side by side`() {
        assertEquals(OverlayLayout.SIDE_BY_SIDE, overlayLayoutFor(width = 915f, height = 411f, hasPreview = true))
    }

    @Test
    fun `portrait, a square screen or no preview stay stacked`() {
        assertEquals(OverlayLayout.STACKED, overlayLayoutFor(width = 411f, height = 915f, hasPreview = true))
        assertEquals(OverlayLayout.STACKED, overlayLayoutFor(width = 600f, height = 600f, hasPreview = true))
        assertEquals(OverlayLayout.STACKED, overlayLayoutFor(width = 915f, height = 411f, hasPreview = false))
    }

    @Test
    fun `an unbounded height is never landscape`() {
        assertEquals(
            OverlayLayout.STACKED,
            overlayLayoutFor(width = 915f, height = Float.POSITIVE_INFINITY, hasPreview = true)
        )
    }

    @Test
    fun `the sheet takes a little more than half of the free width, after the safe area and the gaps`() {
        val columns = sideBySideColumns(width = 2000, insets = 0 to 100, gap = 50, maxSheetWidth = 5000)
        // Free: 2000 - 100 - 3 * 50 = 1750; the sheet takes 1.15 / 2.15 of it.
        assertEquals(936, columns.sheetWidth)
        assertEquals(50, columns.previewStart)
        assertEquals(1750 - 936, columns.previewWidth)
        assertEquals(50 + 814 + 50, columns.sheetStart)
        assertEquals(2000 - 100 - 50, columns.sheetStart + columns.sheetWidth)
    }

    @Test
    fun `a wide screen caps the sheet and gives the rest to the preview`() {
        val columns = sideBySideColumns(width = 3000, insets = 40 to 0, gap = 20, maxSheetWidth = 1000)
        assertEquals(1000, columns.sheetWidth)
        assertEquals(60, columns.previewStart)
        assertEquals(3000 - 40 - 60 - 1000, columns.previewWidth)
        assertEquals(3000 - 20, columns.sheetStart + columns.sheetWidth)
    }

    @Test
    fun `a screen too narrow for the gaps leaves empty columns instead of negative ones`() {
        val columns = sideBySideColumns(width = 10, insets = 0 to 0, gap = 20, maxSheetWidth = 1000)
        assertEquals(0, columns.sheetWidth)
        assertEquals(0, columns.previewWidth)
    }
}
