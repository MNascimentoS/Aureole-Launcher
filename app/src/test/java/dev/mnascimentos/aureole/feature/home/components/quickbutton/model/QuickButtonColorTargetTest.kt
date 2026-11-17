package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import org.junit.Assert.assertEquals
import org.junit.Test

class QuickButtonColorTargetTest {

    private fun ref(argb: Int) = ThemeColorRef.Fixed(argb)

    private val appearance = QuickButtonAppearance(backgroundColor = ref(1), strokeColor = ref(2), iconColor = ref(3))

    @Test
    fun `when reading a color, should return the one of the target`() {
        assertEquals(ref(1), appearance.colorOf(QuickButtonColorTarget.BACKGROUND))
        assertEquals(ref(2), appearance.colorOf(QuickButtonColorTarget.STROKE))
        assertEquals(ref(3), appearance.colorOf(QuickButtonColorTarget.ICON))
    }

    @Test
    fun `when replacing a color, should change only the color of the target`() {
        assertEquals(appearance.copy(backgroundColor = ref(9)), appearance.withColor(QuickButtonColorTarget.BACKGROUND, ref(9)))
        assertEquals(appearance.copy(strokeColor = ref(9)), appearance.withColor(QuickButtonColorTarget.STROKE, ref(9)))
        assertEquals(appearance.copy(iconColor = ref(9)), appearance.withColor(QuickButtonColorTarget.ICON, ref(9)))
    }
}
