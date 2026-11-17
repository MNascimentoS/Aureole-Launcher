package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingDismissDefaultTest {

    @Test
    fun `when the palette picker closes with no tap, should apply the default palette`() {
        assertEquals(DismissDefault.PALETTE, dismissDefaultFor(AppearancePicker.PALETTE, hadPreview = false))
        assertEquals("Frostbite", DEFAULT_PALETTE_NAME)
    }

    @Test
    fun `when the font picker closes with no tap, should apply the default font`() {
        assertEquals(DismissDefault.FONT, dismissDefaultFor(AppearancePicker.FONT, hadPreview = false))
        assertEquals("Istok Web", DEFAULT_FONT_NAME)
    }

    @Test
    fun `when something was previewed, should keep it and apply no default`() {
        assertEquals(DismissDefault.NONE, dismissDefaultFor(AppearancePicker.PALETTE, hadPreview = true))
        assertEquals(DismissDefault.NONE, dismissDefaultFor(AppearancePicker.FONT, hadPreview = true))
    }

    @Test
    fun `when no picker is active, should apply no default`() {
        assertEquals(DismissDefault.NONE, dismissDefaultFor(null, hadPreview = false))
    }

    @Test
    fun `when the checklist creates a container, should save it with no background and no labels but a grid`() {
        val model = onboardingContainerModel(id = "c1", position = "Center", isExpandCell = true)

        assertEquals("c1", model.id)
        assertFalse(model.isBackgroundEnabled)
        assertFalse(model.showFolderLabels)
        assertTrue(model.isGridFolderEnabled)
        assertEquals("Center", model.position)
        assertTrue(model.isExpandCell)
    }
}
