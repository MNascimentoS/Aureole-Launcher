package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import dev.mnascimentos.aureole.core.data.model.QuickButtonIcon
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickButtonIconOptionsTest {

    @Test
    fun `the picker never lists two icons with the same drawable`() {
        val icons = quickButtonPickerIcons()

        assertEquals(icons.size, icons.map { it.iconRes }.toSet().size)
    }

    @Test
    fun `the picker keeps the first of the icons that share a drawable`() {
        val icons = quickButtonPickerIcons()

        assertTrue(QuickButtonIcon.CREATE in icons)
        assertFalse(QuickButtonIcon.EDIT in icons)
    }

    @Test
    fun `an alias of the selected icon counts as selected`() {
        assertTrue(isSameQuickButtonIcon(QuickButtonIcon.CREATE, QuickButtonIcon.EDIT))
        assertFalse(isSameQuickButtonIcon(QuickButtonIcon.STAR, QuickButtonIcon.EDIT))
    }
}
