package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickButtonSheetModelsTest {

    @Test
    fun `when icon and background are the same color, should be low contrast`() {
        assertTrue(isLowContrast(Color.White, Color.White))
        assertTrue(isLowContrast(Color(0xFF455A64), Color(0xFF546E7A)))
    }

    @Test
    fun `when icon and background are far apart, should not be low contrast`() {
        assertFalse(isLowContrast(Color.White, Color.Black))
        assertFalse(isLowContrast(Color.White, Color(0xFF455A64)))
    }

    @Test
    fun `when the ratio is just under or at the limit, should warn only under it`() {
        // Gray 0x76 on white is about 4.5:1; gray 0x9E on white is about 2.7:1; gray 0x949494 is about 3.03:1.
        assertTrue(isLowContrast(Color(0xFF9E9E9E), Color.White))
        assertFalse(isLowContrast(Color(0xFF949494), Color.White))
        assertFalse(isLowContrast(Color(0xFF767676), Color.White))
    }

    @Test
    fun `when the background is translucent, should read it as opaque`() {
        assertFalse(isLowContrast(Color.White, Color(0x00000000)))
    }

    @Test
    fun `when a name was typed, should use it trimmed, else the fallback`() {
        assertEquals("Mail", QuickButtonAppearance(label = "  Mail ").resolvedName("All apps"))
        assertEquals("All apps", QuickButtonAppearance(label = "   ").resolvedName("All apps"))
        assertEquals("All apps", QuickButtonAppearance(label = null).resolvedName("All apps"))
    }

    @Test
    fun `when the click opens an app, should default to the app name, else to the action name`() {
        val model = QuickButtonModel(shortcuts = QuickButtonShortcuts(click = ShortcutAction.openApp("a.b")))
        assertEquals("Gallery", model.defaultName("Gallery", "Open App"))
        assertEquals("All apps", QuickButtonModel().defaultName(null, "All apps"))
    }

    @Test
    fun `when normalizing, should trim the name and store a blank one as null`() {
        assertEquals("Mail", QuickButtonAppearance(label = " Mail ").normalized().label)
        assertNull(QuickButtonAppearance(label = "  ").normalized().label)
        assertEquals(QuickButtonAppearance(), QuickButtonAppearance(label = "").normalized())
    }

    @Test
    fun `when mapping tabs and color targets by index, should round trip and fall back on a bad index`() {
        QuickButtonSheetTab.entries.forEach { assertEquals(it, quickButtonSheetTabAt(quickButtonSheetTabIndex(it))) }
        assertEquals(QuickButtonSheetTab.ICON, quickButtonSheetTabAt(9))
        QUICK_BUTTON_COLOR_TARGETS.forEach {
            assertEquals(it, quickButtonColorTargetAt(quickButtonColorTargetIndex(it)))
        }
        assertEquals(QuickButtonColorTarget.ICON, quickButtonColorTargetAt(9))
        assertEquals(0, quickButtonColorTargetIndex(QuickButtonColorTarget.STROKE))
    }
}
