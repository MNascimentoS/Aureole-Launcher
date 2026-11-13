package dev.mnascimentos.aureole.feature.home.components.widgets.model

import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetPreviewPagingTest {

    @Test
    fun `the page is the card closest to the start of the row`() {
        assertEquals(0, widgetPreviewPage(scrollPx = 0, slotPx = 100f, count = 3))
        assertEquals(0, widgetPreviewPage(scrollPx = 49, slotPx = 100f, count = 3))
        assertEquals(1, widgetPreviewPage(scrollPx = 51, slotPx = 100f, count = 3))
    }

    @Test
    fun `the page stays inside the cards`() {
        assertEquals(2, widgetPreviewPage(scrollPx = 900, slotPx = 100f, count = 3))
    }

    @Test
    fun `an empty row or an unmeasured slot is page zero`() {
        assertEquals(0, widgetPreviewPage(scrollPx = 300, slotPx = 100f, count = 0))
        assertEquals(0, widgetPreviewPage(scrollPx = 300, slotPx = 0f, count = 3))
    }
}
