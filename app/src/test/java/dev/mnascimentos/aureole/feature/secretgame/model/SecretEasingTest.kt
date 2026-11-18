package dev.mnascimentos.aureole.feature.secretgame.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SecretEasingTest {
    @Test
    fun `progress is clamped to the window`() {
        assertEquals(0f, progress(50L, 100L, 200L), EPS)
        assertEquals(0.5f, progress(200L, 100L, 200L), EPS)
        assertEquals(1f, progress(900L, 100L, 200L), EPS)
    }

    @Test
    fun `easings start at 0, end at 1 and are clamped`() {
        listOf(::easeInOutCubic, ::easeOutCubic).forEach { ease ->
            assertEquals(0f, ease(-1f), EPS)
            assertEquals(1f, ease(2f), EPS)
        }
        assertEquals(0.5f, easeInOutCubic(0.5f), EPS)
        assertEquals(0.875f, easeOutCubic(0.5f), EPS)
        assertEquals(0.0625f, easeInOutCubic(0.25f), EPS)
        assertEquals(0.9375f, easeInOutCubic(0.75f), EPS)
    }

    @Test
    fun `lane centres split the width in thirds`() {
        assertEquals(1f / 6f, laneX(0), EPS)
        assertEquals(0.5f, laneX(1), EPS)
        assertEquals(5f / 6f, laneX(2), EPS)
        assertEquals(2.5f, lerp(2f, 3f, 0.5f), EPS)
    }

    private companion object {
        const val EPS = 0.0001f
    }
}
