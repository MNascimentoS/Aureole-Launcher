package dev.mnascimentos.aureole.feature.home.components.overlays.model

import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CustomizeItemsTest {

    @Test
    fun `when listing add items, should be the eight tiles in order with settings last`() {
        assertEquals(
            listOf(
                CustomizeAddItem.CLOCK,
                CustomizeAddItem.APPS_LIST,
                CustomizeAddItem.CONTAINER,
                CustomizeAddItem.WIDGET_GROUP,
                CustomizeAddItem.SCROLL_GROUP,
                CustomizeAddItem.QUICK_BUTTON,
                CustomizeAddItem.WIDGET,
                CustomizeAddItem.SETTINGS
            ),
            CustomizeAddItem.entries
        )
    }

    @Test
    fun `when reading the apps list tile, should add the apps list type`() {
        assertEquals(LauncherItemType.APPS_LIST, CustomizeAddItem.APPS_LIST.type)
    }

    @Test
    fun `when reading the widget and settings tiles, should add no grid item type`() {
        assertNull(CustomizeAddItem.WIDGET.type)
        assertNull(CustomizeAddItem.SETTINGS.type)
    }

    @Test
    fun `when laying out the grid, should have two full rows of four and keep every item once in order`() {
        assertEquals(4, CUSTOMIZE_ADD_COLUMNS)
        assertEquals(listOf(CUSTOMIZE_ADD_COLUMNS, CUSTOMIZE_ADD_COLUMNS), CustomizeAddItem.rows.map { it.size })
        assertEquals(CustomizeAddItem.entries, CustomizeAddItem.rows.flatten())
        assertEquals(CustomizeAddItem.CLOCK, CustomizeAddItem.rows.first().first())
        assertEquals(CustomizeAddItem.SETTINGS, CustomizeAddItem.rows.last().last())
    }

    @Test
    fun `when listing adjust items, should be layout, palette and font`() {
        assertEquals(
            listOf(CustomizeAdjustItem.LAYOUT, CustomizeAdjustItem.PALETTE, CustomizeAdjustItem.FONT),
            CustomizeAdjustItem.entries
        )
    }
}
