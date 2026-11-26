package dev.mnascimentos.aureole.feature.home.components.clock.model

import org.junit.Assert.assertEquals
import org.junit.Test

class PagerMinHeightTest {

    @Test
    fun `when expanded, should have no minimum`() {
        assertEquals(0, pagerMinHeight(isExpanded = true, tallestPx = 800))
    }

    @Test
    fun `when at rest, should be the tallest page`() {
        assertEquals(800, pagerMinHeight(isExpanded = false, tallestPx = 800))
    }

    @Test
    fun `when at rest with a negative height, should not go below zero`() {
        assertEquals(0, pagerMinHeight(isExpanded = false, tallestPx = -5))
    }

    @Test
    fun `session tallest page only grows`() {
        assertEquals(900, sessionTallestPage(previousPx = 900, pageHeights = listOf(300, 500)))
        assertEquals(700, sessionTallestPage(previousPx = 400, pageHeights = listOf(300, 700)))
        assertEquals(0, sessionTallestPage(previousPx = 0, pageHeights = emptyList()))
    }
}
