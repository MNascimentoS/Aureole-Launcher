package dev.mnascimentos.aureole.feature.home.components.clock.model

import org.junit.Assert.assertEquals
import org.junit.Test

class ClockTabTest {

    @Test
    fun `tabs follow the pager order`() {
        assertEquals(listOf(ClockTab.STYLE, ClockTab.FORMAT, ClockTab.COLORS), ClockTab.entries)
        assertEquals(3, ClockTab.pageCount)
    }

    @Test
    fun `index and fromPage are inverse`() {
        ClockTab.entries.forEach { assertEquals(it, ClockTab.fromPage(it.index)) }
    }

    @Test
    fun `fromPage clamps out of range pages`() {
        assertEquals(ClockTab.STYLE, ClockTab.fromPage(-4))
        assertEquals(ClockTab.COLORS, ClockTab.fromPage(9))
    }

    @Test
    fun `resolve uses the target page while scrolling`() {
        assertEquals(ClockTab.FORMAT, ClockTab.resolve(currentPage = 0, targetPage = 1, isScrolling = true))
    }

    @Test
    fun `resolve uses the current page when settled`() {
        assertEquals(ClockTab.STYLE, ClockTab.resolve(currentPage = 0, targetPage = 1, isScrolling = false))
    }

    @Test
    fun `stableHeight never shrinks`() {
        assertEquals(500, ClockTab.stableHeight(current = 300, measured = 500))
        assertEquals(500, ClockTab.stableHeight(current = 500, measured = 300))
    }
}
