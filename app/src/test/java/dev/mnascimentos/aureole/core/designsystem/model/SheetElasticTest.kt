package dev.mnascimentos.aureole.core.designsystem.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

private const val LIMIT = 64f
private const val REST = 400f
private const val TOLERANCE = 0.01f

class SheetElasticTest {

    @Test
    fun `when there is no overshoot or no limit, should not stretch`() {
        assertEquals(0f, rubberBand(overshootPx = 0f, limitPx = LIMIT))
        assertEquals(0f, rubberBand(overshootPx = -10f, limitPx = LIMIT))
        assertEquals(0f, rubberBand(overshootPx = 100f, limitPx = 0f))
    }

    @Test
    fun `when the finger goes further, should stretch more but always below the limit`() {
        var previous = 0f
        listOf(1f, 10f, 50f, 200f, 1000f, 100_000f).forEach { overshoot ->
            val stretch = rubberBand(overshoot, LIMIT)
            assertTrue("stretch grows at $overshoot", stretch > previous)
            assertTrue("stretch stays under the limit at $overshoot", stretch < LIMIT)
            assertTrue("stretch resists at $overshoot", stretch < overshoot)
            previous = stretch
        }
    }

    @Test
    fun `when inverted, should give back the finger overshoot`() {
        listOf(5f, 40f, 300f).forEach { overshoot ->
            assertEquals(overshoot, rubberBandInverse(rubberBand(overshoot, LIMIT), LIMIT), overshoot * TOLERANCE)
        }
        assertEquals(0f, rubberBandInverse(stretchPx = 0f, limitPx = LIMIT))
    }

    @Test
    fun `when below rest, the height should follow the finger freely`() {
        assertEquals(300f, stretchedHeight(rawHeight = 300f, rest = REST, limitPx = LIMIT))
        assertEquals(300f, unstretchedHeight(height = 300f, rest = REST, limitPx = LIMIT))
    }

    @Test
    fun `when above rest, the height should stretch and invert back`() {
        val height = stretchedHeight(rawHeight = REST + 120f, rest = REST, limitPx = LIMIT)
        assertTrue(height > REST && height < REST + LIMIT)
        assertEquals(REST + 120f, unstretchedHeight(height, rest = REST, limitPx = LIMIT), 1f)
    }
}
