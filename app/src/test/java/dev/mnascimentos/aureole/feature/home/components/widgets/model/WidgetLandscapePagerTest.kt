package dev.mnascimentos.aureole.feature.home.components.widgets.model

import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetLandscapePagerTest {

    @Test
    fun `the pager should have one page per widget and the add card last`() {
        assertEquals(1, widgetPagerPages(0))
        assertEquals(2, widgetPagerPages(1))
        assertEquals(4, widgetPagerPages(3))
    }

    @Test
    fun `the card should take the row less its margins when it fits in height`() {
        assertEquals(268f, landscapeWidgetCardWidth(rowWidth = 300f, margin = 16f, maxCardHeight = 1000f), 0.01f)
        assertEquals(268f, landscapeWidgetCardWidth(300f, 16f, Float.POSITIVE_INFINITY), 0.01f)
    }

    @Test
    fun `a short preview should narrow the card so the whole widget shows`() {
        val width = landscapeWidgetCardWidth(rowWidth = 300f, margin = 16f, maxCardHeight = 164f)

        assertEquals(200f, width, 0.01f)
        assertEquals(164f, widgetCardHeight(width), 0.01f)
        assertEquals(0f, landscapeWidgetCardWidth(rowWidth = 10f, margin = 16f, maxCardHeight = 100f), 0f)
    }
}
