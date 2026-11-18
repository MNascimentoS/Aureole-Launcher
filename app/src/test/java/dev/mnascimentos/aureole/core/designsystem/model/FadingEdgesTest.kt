package dev.mnascimentos.aureole.core.designsystem.model

import org.junit.Assert.assertEquals
import org.junit.Test

class FadingEdgesTest {

    @Test
    fun `when at the top of the scroll, should fade only at the bottom`() {
        assertEquals(FadeEdges(top = false, bottom = true), fadeEdgesFor(canScrollBackward = false, canScrollForward = true))
    }

    @Test
    fun `when in the middle of the scroll, should fade on both edges`() {
        assertEquals(FadeEdges(top = true, bottom = true), fadeEdgesFor(canScrollBackward = true, canScrollForward = true))
    }

    @Test
    fun `when at the end of the scroll, should fade only at the top`() {
        assertEquals(FadeEdges(top = true, bottom = false), fadeEdgesFor(canScrollBackward = true, canScrollForward = false))
    }

    @Test
    fun `when the content fits, should not fade`() {
        assertEquals(FadeEdges(top = false, bottom = false), fadeEdgesFor(canScrollBackward = false, canScrollForward = false))
    }
}
