package dev.mnascimentos.aureole.feature.home.components.scrubber.model

import org.junit.Assert.assertEquals
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
}
