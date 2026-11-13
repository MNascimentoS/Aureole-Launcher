package dev.mnascimentos.aureole.feature.home.components.overlays.model

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomizeItemsTest {

    private fun item(type: LauncherItemType) = LauncherItemState(id = type.name, type = type)

    @Test
    fun `when the home is empty, should have no item marked as on home`() {
        assertTrue(itemsOnHome(emptyList()).isEmpty())
    }

    @Test
    fun `when the home has an apps list that shows all apps, should still offer all apps to add another`() {
        val allApps = item(LauncherItemType.APPS_LIST).copy(showAllApps = true)

        assertTrue(itemsOnHome(listOf(item(LauncherItemType.CLOCK), allApps)).isEmpty())
        assertFalse(CustomizeAddItem.ALL_APPS.isSingleton)
        assertEquals(LauncherItemType.APPS_LIST, CustomizeAddItem.ALL_APPS.type)
    }

    @Test
    fun `when the home has a clock, should still offer the clock to add another`() {
        assertTrue(itemsOnHome(listOf(item(LauncherItemType.CLOCK))).isEmpty())
        assertFalse(CustomizeAddItem.CLOCK.isSingleton)
    }

    @Test
    fun `when the home has a favorites list, should still offer favorites to add another`() {
        assertTrue(itemsOnHome(listOf(item(LauncherItemType.APPS_LIST))).isEmpty())
        assertFalse(CustomizeAddItem.FAVORITES.isSingleton)
    }

    @Test
    fun `when the home has repeatable kinds, should never mark them`() {
        val repeatable = listOf(
            item(LauncherItemType.SHORTCUTS_CONTAINER),
            item(LauncherItemType.WIDGET_LIST),
            item(LauncherItemType.SCROLL_VIEW),
            item(LauncherItemType.QUICK_BUTTON),
            item(LauncherItemType.SINGLE_APP_WIDGET)
        )

        assertTrue(itemsOnHome(repeatable).isEmpty())
        assertFalse(CustomizeAddItem.CONTAINER.isOnHome(repeatable))
    }

    @Test
    fun `when laying out the grid, should have four columns and keep every item once in order`() {
        assertEquals(CUSTOMIZE_ADD_COLUMNS, CustomizeAddItem.rows.first().size)
        assertEquals(2, CustomizeAddItem.rows.size)
        assertEquals(CustomizeAddItem.entries, CustomizeAddItem.rows.flatten())
        assertEquals(CustomizeAddItem.CLOCK, CustomizeAddItem.rows.first().first())
        assertEquals(CustomizeAddItem.WIDGET, CustomizeAddItem.rows.last().last())
    }

    @Test
    fun `when listing adjust items, should be layout, palette and font`() {
        assertEquals(
            listOf(CustomizeAdjustItem.LAYOUT, CustomizeAdjustItem.PALETTE, CustomizeAdjustItem.FONT),
            CustomizeAdjustItem.entries
        )
    }
}
