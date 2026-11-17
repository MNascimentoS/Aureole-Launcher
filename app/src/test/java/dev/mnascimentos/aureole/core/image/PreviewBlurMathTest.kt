package dev.mnascimentos.aureole.core.image

import org.junit.Assert.assertEquals
import org.junit.Test

class PreviewBlurMathTest {

    @Test
    fun `the preview copy shrinks by powers of two until it fits`() {
        assertEquals(1, previewSampleSize(400, 300))
        assertEquals(1, previewSampleSize(540, 540))
        assertEquals(2, previewSampleSize(1080, 720))
        assertEquals(8, previewSampleSize(3000, 4000))
        assertEquals(2048, previewSampleSize(4000, 3000, maxSide = 0))
    }

    @Test
    fun `the preview radius follows the shrink and never drops a blur to zero`() {
        assertEquals(0, previewBlurRadius(0, 4))
        assertEquals(10, previewBlurRadius(40, 4))
        assertEquals(1, previewBlurRadius(2, 8))
        assertEquals(5, previewBlurRadius(5, 0))
    }
}
