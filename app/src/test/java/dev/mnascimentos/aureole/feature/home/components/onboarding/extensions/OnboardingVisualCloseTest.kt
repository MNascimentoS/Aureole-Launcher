package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.feature.home.components.onboarding.model.VisualChoice
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.resolveVisualClose
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingVisualCloseTest {

    private val defaults = visualDefaults

    @Test
    fun `the defaults are Frostbite and Istok Web`() {
        assertEquals(VisualChoice("Frostbite", "Istok Web"), defaults)
    }

    @Test
    fun `when the sheet closes the first time with no tap, should apply both defaults`() {
        val choice = resolveVisualClose(VisualChoice(), isFirstVisit = true, defaults = defaults)

        assertEquals(defaults, choice)
    }

    @Test
    fun `when only the font was tapped the first time, should keep it and default the palette`() {
        val choice = resolveVisualClose(VisualChoice(fontName = "Lora"), isFirstVisit = true, defaults = defaults)

        assertEquals(VisualChoice(paletteName = "Frostbite", fontName = "Lora"), choice)
    }

    @Test
    fun `when a done step is reopened, should apply only what was tapped`() {
        val untouched = resolveVisualClose(VisualChoice(), isFirstVisit = false, defaults = defaults)
        val palette = resolveVisualClose(VisualChoice(paletteName = "Ember"), isFirstVisit = false, defaults = defaults)

        assertEquals(VisualChoice(), untouched)
        assertEquals(VisualChoice(paletteName = "Ember"), palette)
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
