package dev.mnascimentos.aureole.feature.home.components.scrubber.model

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ScrubberGeometryTest {

    private val density = Density(density = 2f)

    @Test
    fun `band takes the fraction of the parent and leaves the bottom padding under the letters`() {
        assertEquals(ScrubberBand(outerPx = 550, lettersPx = 326), scrubberBand(1000, 0.55f, 224))
    }

    @Test
    fun `band rounds like fillMaxHeight and never goes negative`() {
        assertEquals(ScrubberBand(outerPx = 551, lettersPx = 551), scrubberBand(1001, 0.55f, 0))
        assertEquals(ScrubberBand(outerPx = 55, lettersPx = 0), scrubberBand(100, 0.55f, 224))
        assertEquals(ScrubberBand(outerPx = 0, lettersPx = 0), scrubberBand(-10, 0.55f, 10))
    }

    @Test
    fun `bounds of the home scrubber sit from 45 percent of the screen down to the dock padding`() {
        val bounds = scrubberBoundsFor(2000f, density)
        val bottomPaddingPx = ScrubberDimensions.PaddingBottom.value * 2f

        assertEquals(900f, bounds.topPx, 0f)
        assertEquals(2000f - bottomPaddingPx, bounds.bottomPx, 0f)
        assertEquals(1100f - bottomPaddingPx, bounds.heightPx, 0f)
    }

    @Test
    fun `bounds follow the options of the scrubber`() {
        val bounds = scrubberBoundsFor(1000f, density, ScrubberOptions(heightFraction = 1f, bottomPadding = 8.dp))

        assertEquals(0f, bounds.topPx, 0f)
        assertEquals(984f, bounds.bottomPx, 0f)
    }

    @Test
    fun `bounds contain only the letters span and clamp to it`() {
        val bounds = ScrubberBounds(topPx = 100f, bottomPx = 360f)

        assertTrue(bounds.contains(100f))
        assertTrue(bounds.contains(360f))
        assertFalse(bounds.contains(99f))
        assertFalse(bounds.contains(361f))
        assertEquals(0f, bounds.toLocalY(50f), 0f)
        assertEquals(130f, bounds.toLocalY(230f), 0f)
        assertEquals(260f, bounds.toLocalY(900f), 0f)
    }

    @Test
    fun `empty bounds contain nothing`() {
        assertFalse(ScrubberBounds(topPx = 10f, bottomPx = 10f).contains(10f))
        assertEquals(0f, ScrubberBounds(topPx = 10f, bottomPx = 5f).heightPx, 0f)
    }

    @Test
    fun `letter index covers A at the very top and Z at the very bottom`() {
        assertEquals(0, letterIndexAt(0f, 260f, 26))
        assertEquals(0, letterIndexAt(9.9f, 260f, 26))
        assertEquals(1, letterIndexAt(10f, 260f, 26))
        assertEquals(15, letterIndexAt(155f, 260f, 26))
        assertEquals(25, letterIndexAt(259.9f, 260f, 26))
        assertEquals(25, letterIndexAt(260f, 260f, 26))
    }

    @Test
    fun `letter index clamps outside the column and rejects empty columns`() {
        assertEquals(0, letterIndexAt(-40f, 260f, 26))
        assertEquals(25, letterIndexAt(4000f, 260f, 26))
        assertEquals(-1, letterIndexAt(10f, 0f, 26))
        assertEquals(-1, letterIndexAt(10f, 260f, 0))
    }

    @Test
    fun `letter center is in the middle of its slot`() {
        assertEquals(5f, letterCenterY(0, 260f, 26), 0f)
        assertEquals(255f, letterCenterY(25, 260f, 26), 0f)
        assertEquals(0f, letterCenterY(3, 260f, 0), 0f)
    }

    @Test
    fun `bulge weight is one at the finger and fades away`() {
        assertEquals(1f, scrubberBulgeWeight(0f, 10f), 0f)
        assertTrue(scrubberBulgeWeight(10f, 10f) < 1f)
        assertTrue(scrubberBulgeWeight(40f, 10f) < scrubberBulgeWeight(10f, 10f))
        assertEquals(0f, scrubberBulgeWeight(5f, 0f), 0f)
    }

    @Test
    fun `edge zone on the right handed layout is the right strip as tall as the letters`() {
        val zone = ScrubberEdgeZone(
            screenWidthPx = 1000f,
            edgeWidthPx = 64f,
            bounds = ScrubberBounds(topPx = 900f, bottomPx = 1776f),
            isLeftHanded = false
        )

        assertTrue(zone.contains(990f, 1200f))
        assertTrue(zone.contains(990f, 900f))
        assertFalse(zone.contains(900f, 1200f))
        assertFalse(zone.contains(10f, 1200f))
        assertFalse(zone.contains(990f, 400f))
        assertFalse(zone.contains(990f, 1900f))
    }

    @Test
    fun `edge zone mirrors to the left strip in left handed mode`() {
        val zone = ScrubberEdgeZone(
            screenWidthPx = 1000f,
            edgeWidthPx = 64f,
            bounds = ScrubberBounds(topPx = 900f, bottomPx = 1776f),
            isLeftHanded = true
        )

        assertTrue(zone.contains(10f, 1200f))
        assertFalse(zone.contains(990f, 1200f))
        assertFalse(zone.contains(10f, 400f))
    }

    @Test
    fun `edge zone is empty before the screen is measured`() {
        val zone = ScrubberEdgeZone(0f, 64f, ScrubberBounds(0f, 0f), isLeftHanded = false)

        assertFalse(zone.contains(0f, 0f))
    }

    @Test
    fun `selection tracker reports each letter once until the scrub ends`() {
        val tracker = LetterSelectionTracker()

        assertFalse(tracker.select(null))
        assertTrue(tracker.select('P'))
        assertFalse(tracker.select('P'))
        assertTrue(tracker.select('Q'))
        tracker.reset()
        assertTrue(tracker.select('Q'))
    }
}
