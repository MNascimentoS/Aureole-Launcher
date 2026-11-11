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
    fun `pagerHeight follows the settled page`() {
        assertEquals(300, ClockTab.pagerHeight(listOf(300, 500, 400), currentPage = 0, offsetFraction = 0f))
        assertEquals(500, ClockTab.pagerHeight(listOf(300, 500, 400), currentPage = 1, offsetFraction = 0f))
    }

    @Test
    fun `pagerHeight blends with the next page while sliding forward`() {
        assertEquals(400, ClockTab.pagerHeight(listOf(300, 500, 400), currentPage = 0, offsetFraction = 0.5f))
    }

    @Test
    fun `pagerHeight blends with the previous page while sliding back`() {
        assertEquals(400, ClockTab.pagerHeight(listOf(300, 500, 400), currentPage = 1, offsetFraction = -0.5f))
    }

    @Test
    fun `pagerHeight wraps content until the page is measured`() {
        assertEquals(0, ClockTab.pagerHeight(listOf(0, 500, 400), currentPage = 0, offsetFraction = 0f))
    }

    @Test
    fun `pagerHeight keeps the page height when the neighbour is unknown or missing`() {
        assertEquals(300, ClockTab.pagerHeight(listOf(300, 0, 400), currentPage = 0, offsetFraction = 0.5f))
        assertEquals(400, ClockTab.pagerHeight(listOf(300, 500, 400), currentPage = 2, offsetFraction = 0.5f))
    }
}
