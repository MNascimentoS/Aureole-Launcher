package dev.mnascimentos.aureole.feature.home.components.widgets.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WidgetStackMetricsTest {

    @Test
    fun `card width shows one card and a half in a 360 dp row`() {
        val width = widgetCardWidth(rowWidth = 360f, margin = 16f, gap = 12f)

        assertEquals(210.67f, width, 0.01f)
    }

    @Test
    fun `card width grows with the row`() {
        assertEquals(245.33f, widgetCardWidth(rowWidth = 412f, margin = 16f, gap = 12f), 0.01f)
    }

    @Test
    fun `card width is never negative`() {
        assertEquals(0f, widgetCardWidth(rowWidth = 10f, margin = 16f, gap = 12f), 0f)
    }

    @Test
    fun `card height is 0,82 of the width`() {
        assertEquals(100f, widgetCardHeight(100f / WIDGET_CARD_HEIGHT_RATIO), 0.001f)
    }

    @Test
    fun `step target moves one place and stops at the ends`() {
        val widgets = listOf(10, 20, 30)

        assertEquals(0, widgetStepTarget(widgets, 20, towardsStart = true))
        assertEquals(2, widgetStepTarget(widgets, 20, towardsStart = false))
        assertNull(widgetStepTarget(widgets, 10, towardsStart = true))
        assertNull(widgetStepTarget(widgets, 30, towardsStart = false))
    }

    @Test
    fun `step target is null for a widget that is not in the stack`() {
        assertNull(widgetStepTarget(listOf(1, 2), 9, towardsStart = false))
    }

    @Test
    fun `reveal keeps the offset when the card is already whole`() {
        assertEquals(
            50,
            revealScrollTarget(
                50,
                RevealScrollParams(1, slotPx = 100f, cardPx = 88f, marginPx = 16f, viewportPx = 300f)
            )
        )
    }

    @Test
    fun `reveal scrolls back when the card starts before the row`() {
        assertEquals(
            100,
            revealScrollTarget(
                250,
                RevealScrollParams(1, slotPx = 100f, cardPx = 88f, marginPx = 16f, viewportPx = 300f)
            )
        )
    }

    @Test
    fun `reveal scrolls forward when the card is cut at the end`() {
        // Card 2 ends at 16 + 200 + 88 = 304 and needs 16 more: 320 - 300 = 20.
        assertEquals(
            20,
            revealScrollTarget(0, RevealScrollParams(2, slotPx = 100f, cardPx = 88f, marginPx = 16f, viewportPx = 300f))
        )
    }
}
