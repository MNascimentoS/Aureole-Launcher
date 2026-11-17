package dev.mnascimentos.aureole.feature.settings.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class BackgroundChoiceTest {

    @Test
    fun `without a custom image the sheet opens on the solid color of the current color`() {
        val draft = initialBackgroundDraft(isCustomImageSet = false, currentColor = 0xFF123456.toInt())

        assertEquals(BackgroundType.SOLID_COLOR, draft.type)
        assertEquals(0xFF123456.toInt(), draft.color)
    }

    @Test
    fun `with a custom image the sheet opens on the custom image type`() {
        val draft = initialBackgroundDraft(isCustomImageSet = true, currentColor = 1)

        assertEquals(BackgroundType.CUSTOM_IMAGE, draft.type)
    }

    @Test
    fun `a new solid color is saved on close`() {
        val initial = BackgroundDraft(BackgroundType.SOLID_COLOR, color = 1)

        assertEquals(42, backgroundColorToSave(initial, BackgroundDraft(BackgroundType.SOLID_COLOR, color = 42)))
    }

    @Test
    fun `the same solid color saves nothing`() {
        val draft = BackgroundDraft(BackgroundType.SOLID_COLOR, color = 1)

        assertNull(backgroundColorToSave(draft, draft))
    }

    @Test
    fun `the custom image type saves nothing because the picker saves it`() {
        val initial = BackgroundDraft(BackgroundType.SOLID_COLOR, color = 1)

        assertNull(backgroundColorToSave(initial, BackgroundDraft(BackgroundType.CUSTOM_IMAGE, color = 2)))
    }

    @Test
    fun `going from the image back to a solid color saves that color`() {
        val initial = BackgroundDraft(BackgroundType.CUSTOM_IMAGE, color = 7)

        assertEquals(7, backgroundColorToSave(initial, BackgroundDraft(BackgroundType.SOLID_COLOR, color = 7)))
    }
}
