package dev.mnascimentos.aureole.feature.settings.model

import org.junit.Assert.assertEquals
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
    fun `applying the solid color type saves the draft color`() {
        val action = BackgroundDraft(BackgroundType.SOLID_COLOR, color = 42).applyAction()

        assertEquals(BackgroundApplyAction.SaveColor(42), action)
    }

    @Test
    fun `applying the custom image type opens the photo picker`() {
        val action = BackgroundDraft(BackgroundType.CUSTOM_IMAGE, color = 42).applyAction()

        assertEquals(BackgroundApplyAction.PickImage, action)
    }
}
