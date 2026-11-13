package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import org.junit.Assert.assertEquals
import org.junit.Test

class QuickButtonColorTargetTest {

    private val appearance = QuickButtonAppearance(backgroundColor = 1, strokeColor = 2, iconColor = 3)

    @Test
    fun `when reading a color, should return the one of the target`() {
        assertEquals(1, appearance.colorOf(QuickButtonColorTarget.BACKGROUND))
        assertEquals(2, appearance.colorOf(QuickButtonColorTarget.STROKE))
        assertEquals(3, appearance.colorOf(QuickButtonColorTarget.ICON))
    }

    @Test
    fun `when replacing a color, should change only the color of the target`() {
        assertEquals(appearance.copy(backgroundColor = 9), appearance.withColor(QuickButtonColorTarget.BACKGROUND, 9))
        assertEquals(appearance.copy(strokeColor = 9), appearance.withColor(QuickButtonColorTarget.STROKE, 9))
        assertEquals(appearance.copy(iconColor = 9), appearance.withColor(QuickButtonColorTarget.ICON, 9))
    }
}
