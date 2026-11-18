package dev.mnascimentos.aureole.feature.home.grid.model

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ScrollChildSizeTest {

    private fun item(type: LauncherItemType) =
        LauncherItemState(id = "i", type = type, col = 0, row = 0, colSpan = 3, rowSpan = 5)

    @Test
    fun `when the item is the apps list, should not be resizable and have no span`() {
        val apps = item(LauncherItemType.APPS_LIST)

        assertFalse(apps.isResizable())
        assertNull(apps.effectiveSpan(isVertical = true))
        assertNull(apps.effectiveSpan(isVertical = false))
    }

    @Test
    fun `when the item is anything else, should be resizable`() {
        LauncherItemType.entries.filter { it != LauncherItemType.APPS_LIST }.forEach {
            assertTrue(it.name, item(it).isResizable())
        }
    }

    @Test
    fun `when the group scrolls vertically, should use the row span, else the column span`() {
        val clock = item(LauncherItemType.CLOCK)

        assertEquals(5, clock.effectiveSpan(isVertical = true))
        assertEquals(3, clock.effectiveSpan(isVertical = false))
    }
}
