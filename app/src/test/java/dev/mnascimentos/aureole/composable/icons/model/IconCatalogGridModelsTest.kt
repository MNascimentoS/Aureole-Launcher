package dev.mnascimentos.aureole.composable.icons.model

import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCatalog
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class IconCatalogGridModelsTest {

    private val items = iconGridItems()

    @Test
    fun `grid has a header before the icons of each category and every icon once`() {
        val icons = items.filterIsInstance<IconGridItem.Icon>().map { it.entry.key }

        assertEquals(AureoleIconCatalog.entries.map { it.key }, icons)
        assertEquals(AureoleIconCategory.entries, iconGridCategories(items))
        assertTrue(items.first() is IconGridItem.Header)
    }

    @Test
    fun `a category without icons gets no header`() {
        val withoutGames = AureoleIconCatalog.entries.filter { it.category != AureoleIconCategory.GAMES }

        val categories = iconGridCategories(iconGridItems(withoutGames))

        assertFalse(AureoleIconCategory.GAMES in categories)
        assertEquals(AureoleIconCategory.entries.size - 1, categories.size)
    }

    @Test
    fun `header index points at the header of the category`() {
        AureoleIconCategory.entries.forEach {
            assertEquals(IconGridItem.Header(it), items[iconGridHeaderIndex(items, it)])
        }
        assertEquals(0, iconGridHeaderIndex(items, AureoleIconCategory.ACTIONS))
        assertEquals(0, iconGridHeaderIndex(emptyList(), AureoleIconCategory.WORK))
    }

    @Test
    fun `category at an index is the one of the nearest header above`() {
        val workHeader = iconGridHeaderIndex(items, AureoleIconCategory.WORK)

        assertEquals(AureoleIconCategory.WORK, iconGridCategoryAt(items, workHeader))
        assertEquals(AureoleIconCategory.WORK, iconGridCategoryAt(items, workHeader + 3))
        assertEquals(AureoleIconCategory.ACTIONS, iconGridCategoryAt(items, workHeader - 1))
        assertEquals(AureoleIconCategory.ACTIONS, iconGridCategoryAt(items, 0))
    }

    @Test
    fun `category at an index out of range is clamped`() {
        assertEquals(AureoleIconCategory.TOOLS, iconGridCategoryAt(items, items.size + 50))
        assertEquals(AureoleIconCategory.ACTIONS, iconGridCategoryAt(items, -4))
        assertNull(iconGridCategoryAt(emptyList(), 0))
    }

    @Test
    fun `category at a list that starts with an icon is the category of that icon`() {
        val onlyIcons = items.filterIsInstance<IconGridItem.Icon>().take(2)

        assertEquals(onlyIcons.first().entry.category, iconGridCategoryAt(onlyIcons, 1))
    }

    @Test
    fun `grid opens on the category of the saved icon, old keys included`() {
        assertEquals(iconGridHeaderIndex(items, AureoleIconCategory.MONEY), initialIconGridIndex(items, "savings"))
        assertEquals(iconGridHeaderIndex(items, AureoleIconCategory.ACTIONS), initialIconGridIndex(items, "STAR"))
        assertEquals(iconGridHeaderIndex(items, AureoleIconCategory.MONEY), initialIconGridIndex(items, "cart"))
    }

    @Test
    fun `grid opens on the top without a usable saved icon`() {
        assertEquals(0, initialIconGridIndex(items, null))
        assertEquals(0, initialIconGridIndex(items, "no_such_icon"))
    }

    @Test
    fun `selected icon is the one the saved key resolves to`() {
        val star = AureoleIconCatalog.entries.first { it.key == "action_star" }
        val savings = AureoleIconCatalog.entries.first { it.key == "savings" }

        assertTrue(isIconSelected(star, "STAR"))
        assertTrue(isIconSelected(star, "action_star"))
        assertFalse(isIconSelected(savings, "STAR"))
        assertFalse(isIconSelected(star, null))
        assertFalse(isIconSelected(star, "no_such_icon"))
    }

    @Test
    fun `a chip already inside the row does not move it`() {
        assertEquals(0, chipScrollDelta(chipStart = 10, chipEnd = 90, viewportStart = 0, viewportEnd = 100))
        assertEquals(0, chipScrollDelta(chipStart = 0, chipEnd = 100, viewportStart = 0, viewportEnd = 100))
    }

    @Test
    fun `a chip cut at the end scrolls forward by what is hidden`() {
        assertEquals(30, chipScrollDelta(chipStart = 50, chipEnd = 130, viewportStart = 0, viewportEnd = 100))
    }

    @Test
    fun `a chip cut at the start scrolls back by what is hidden`() {
        assertEquals(-20, chipScrollDelta(chipStart = -20, chipEnd = 60, viewportStart = 0, viewportEnd = 100))
    }

    @Test
    fun `a chip wider than the row lines up with the start`() {
        assertEquals(10, chipScrollDelta(chipStart = 10, chipEnd = 300, viewportStart = 0, viewportEnd = 100))
    }
}
