package dev.mnascimentos.aureole.core.designsystem.icons

import dev.mnascimentos.aureole.composable.dialogs.model.LEGACY_FOLDER_ICON_KEYS
import dev.mnascimentos.aureole.core.data.model.QuickButtonIcon
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AureoleIconCatalogTest {

    private val entries = AureoleIconCatalog.entries

    @Test
    fun `catalog holds the folder icons and the quick button ones without the shared drawable twice`() {
        val actions = AureoleIconCatalog.byCategory(AureoleIconCategory.ACTIONS)

        assertEquals(AureoleFolderIcons.all.size + actions.size, entries.size)
        assertEquals(QuickButtonIcon.entries.distinctBy { it.iconRes }.size, actions.size)
        assertEquals(AureoleFolderIcons.all.size, entries.size - actions.size)
    }

    @Test
    fun `every key and every drawable appears once`() {
        assertEquals(entries.size, entries.map { it.key }.toSet().size)
        assertEquals(entries.size, entries.map { it.res }.toSet().size)
    }

    @Test
    fun `every icon is in exactly one category`() {
        val perCategory = AureoleIconCatalog.categories.associateWith { AureoleIconCatalog.byCategory(it) }

        assertEquals(entries.size, perCategory.values.sumOf { it.size })
        assertEquals(entries.map { it.key }.toSet(), perCategory.values.flatten().map { it.key }.toSet())
        assertTrue(perCategory.values.all { it.isNotEmpty() })
    }

    @Test
    fun `actions come first in the category order`() {
        assertEquals(AureoleIconCategory.ACTIONS, AureoleIconCatalog.categories.first())
        assertEquals(AureoleIconCategory.ACTIONS, entries.first().category)
    }

    @Test
    fun `every old folder key resolves to itself`() {
        AureoleFolderIcons.all.forEach { assertEquals(it.key, AureoleIconCatalog.resolveKey(it.key)) }
    }

    @Test
    fun `every quick button name resolves to its action key and drawable`() {
        QuickButtonIcon.entries.forEach { icon ->
            val key = AureoleIconCatalog.resolveKey(icon.name)

            assertEquals(AureoleIconCatalog.actionKey(icon), key)
            assertEquals(icon.iconRes, AureoleIconCatalog.resFor(icon.name))
        }
    }

    @Test
    fun `the aliased quick button icons share one key`() {
        assertEquals("action_edit", AureoleIconCatalog.resolveKey("CREATE"))
        assertEquals("action_edit", AureoleIconCatalog.resolveKey("EDIT"))
    }

    @Test
    fun `an upper case enum name wins over the folder key of the same word`() {
        assertEquals("action_favorite", AureoleIconCatalog.resolveKey("FAVORITE"))
        assertEquals("favorite", AureoleIconCatalog.resolveKey("favorite"))
        assertEquals("action_date_range", AureoleIconCatalog.resolveKey("DATE_RANGE"))
        assertEquals("calendar_month", AureoleIconCatalog.resolveKey("date_range"))
    }

    @Test
    fun `old folder keys without their own drawable resolve to an action or a remapped icon`() {
        val resolvable = LEGACY_FOLDER_ICON_KEYS - "delete"

        resolvable.forEach { assertNotNull("$it should resolve", AureoleIconCatalog.resolveKey(it)) }
        assertEquals("action_add", AureoleIconCatalog.resolveKey("add"))
        assertEquals("action_notifications", AureoleIconCatalog.resolveKey("more_vert"))
        assertEquals("location_on", AureoleIconCatalog.resolveKey("place"))
        assertEquals("shopping_cart", AureoleIconCatalog.resolveKey("cart"))
    }

    @Test
    fun `keys are matched without case, spaces or surrounding blanks`() {
        assertEquals("business_center", AureoleIconCatalog.resolveKey("  Business Center "))
    }

    @Test
    fun `unknown, blank and missing keys resolve to nothing`() {
        assertNull(AureoleIconCatalog.resolveKey(null))
        assertNull(AureoleIconCatalog.resolveKey("   "))
        assertNull(AureoleIconCatalog.resolveKey("no_such_icon"))
        assertNull(AureoleIconCatalog.resolveKey("delete"))
        assertNull(AureoleIconCatalog.resFor("no_such_icon"))
        assertNull(AureoleIconCatalog.categoryOf(null))
    }

    @Test
    fun `category of a key follows the key it resolves to`() {
        assertEquals(AureoleIconCategory.ACTIONS, AureoleIconCatalog.categoryOf("STAR"))
        assertEquals(AureoleIconCategory.MONEY, AureoleIconCatalog.categoryOf("savings"))
        assertEquals(AureoleIconCategory.MONEY, AureoleIconCatalog.categoryOf("cart"))
    }
}
