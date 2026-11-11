package dev.mnascimentos.aureole.composable.mirror.model

import androidx.compose.ui.unit.LayoutDirection
import org.junit.Assert.assertEquals
import org.junit.Test

class MirrorDirectionTest {
    @Test
    fun `right handed keeps the current direction`() {
        assertEquals(LayoutDirection.Ltr, mirroredLayoutDirection(false, LayoutDirection.Ltr))
        assertEquals(LayoutDirection.Rtl, mirroredLayoutDirection(false, LayoutDirection.Rtl))
    }

    @Test
    fun `left handed flips the current direction`() {
        assertEquals(LayoutDirection.Rtl, mirroredLayoutDirection(true, LayoutDirection.Ltr))
        assertEquals(LayoutDirection.Ltr, mirroredLayoutDirection(true, LayoutDirection.Rtl))
    }
}
