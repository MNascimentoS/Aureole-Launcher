package dev.mnascimentos.aureole.core.designsystem.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PreviewFadeTest {

    @Test
    fun `at rest the preview is fully visible`() {
        assertEquals(1f, previewAlpha(0f), 0f)
    }

    @Test
    fun `expanded the preview is gone`() {
        assertEquals(0f, previewAlpha(1f), 0f)
    }

    @Test
    fun `the fade follows the expansion linearly`() {
        assertEquals(0.5f, previewAlpha(0.5f), 0.0001f)
        assertEquals(0.75f, previewAlpha(0.25f), 0.0001f)
    }

    @Test
    fun `fractions out of range are clamped`() {
        assertEquals(1f, previewAlpha(-0.2f), 0f)
        assertEquals(0f, previewAlpha(1.3f), 0f)
    }

    @Test
    fun `only a fully faded preview is hidden from touch`() {
        assertTrue(isPreviewHidden(previewAlpha(1f)))
        assertFalse(isPreviewHidden(previewAlpha(0.99f)))
        assertFalse(isPreviewHidden(previewAlpha(0f)))
    }
}
