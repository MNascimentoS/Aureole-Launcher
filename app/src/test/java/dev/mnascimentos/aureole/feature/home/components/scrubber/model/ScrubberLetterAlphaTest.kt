package dev.mnascimentos.aureole.feature.home.components.scrubber.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ScrubberLetterAlphaTest {
    @Test
    fun `selected letter is hidden while dragging so the badge is the only copy`() {
        assertEquals(0f, scrubberLetterAlpha(isSelected = true, isDragging = true, scrubberAlpha = 1f), 0f)
    }

    @Test
    fun `other letters keep the strip alpha`() {
        assertEquals(1f, scrubberLetterAlpha(isSelected = false, isDragging = true, scrubberAlpha = 1f), 0f)
        assertEquals(0.35f, scrubberLetterAlpha(isSelected = true, isDragging = false, scrubberAlpha = 0.35f), 0f)
    }

    @Test
    fun `prominent selection keeps the selected letter in the strip`() {
        assertEquals(
            1f,
            scrubberLetterAlpha(isSelected = true, isDragging = true, scrubberAlpha = 1f, isBadgeOnLetter = false),
            0f
        )
    }

    @Test
    fun `selected letter is the biggest and the neighbours grow less with distance`() {
        val selected = scrubberLetterScale(distancePx = 0f, growthSpreadPx = 24f, isSelected = true)
        val near = scrubberLetterScale(distancePx = 14f, growthSpreadPx = 24f, isSelected = false)
        val far = scrubberLetterScale(distancePx = 42f, growthSpreadPx = 24f, isSelected = false)
        val away = scrubberLetterScale(distancePx = 400f, growthSpreadPx = 24f, isSelected = false)

        assertEquals(ScrubberMotion.SELECTED_LETTER_SCALE, selected, 0f)
        assertTrue(near < selected)
        assertTrue(far < near)
        assertTrue(far > 1f)
        assertEquals(1f, away, 0.001f)
    }

    @Test
    fun `large badge sits inward of the edge and above the finger, mirrored for left handed`() {
        val right = prominentBadgeOffset(
            touchYPx = 500f,
            badgeSizePx = 76f,
            insetPx = 96f,
            liftPx = 56f,
            isLeftHanded = false
        )
        val left = prominentBadgeOffset(
            touchYPx = 500f,
            badgeSizePx = 76f,
            insetPx = 96f,
            liftPx = 56f,
            isLeftHanded = true
        )

        assertEquals(ProminentBadgeOffset(xPx = -96f, yPx = 406f), right)
        assertEquals(ProminentBadgeOffset(xPx = 96f, yPx = 406f), left)
        assertTrue(right.yPx + 76f < 500f)
    }
}
