package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class GridRepositoryParsingTest {

    private lateinit var context: Context
    private lateinit var repository: GridRepository

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        repository = GridRepository(context)
        prefs().edit().clear().commit()
    }

    @After
    fun tearDown() {
        prefs().edit().clear().commit()
    }

    private fun prefs() = context.getSharedPreferences("aureole_grid_prefs", Context.MODE_PRIVATE)

    private fun ids(isLandscape: Boolean) = repository.getGridItems(isLandscape).map { it.safeType }

    private fun defaults(isLandscape: Boolean) = GridDefaults.getDefaultGridItems(isLandscape).map { it.safeType }

    @Test
    fun `when nothing is saved, should return the defaults for each orientation`() {
        assertEquals(defaults(isLandscape = false), ids(isLandscape = false))
        assertEquals(defaults(isLandscape = true), ids(isLandscape = true))
    }

    @Test
    fun `when nothing is saved in left-handed mode, should return the mirrored defaults`() {
        val items = repository.getGridItems(isLandscape = false, isLeftHanded = true)

        assertEquals(GridDefaults.getDefaultGridItems(isLandscape = false, isLeftHanded = true), items)
    }

    @Test
    fun `when only the legacy portrait key exists, should read it`() {
        prefs().edit().putString("grid_items_json", """[{"id":"legacy","type":"CLOCK"}]""").commit()

        assertEquals(listOf("legacy"), repository.getGridItems(isLandscape = false).map { it.id })
        assertEquals(defaults(isLandscape = true), ids(isLandscape = true))
    }

    @Test
    fun `when the saved data is broken, should fall back to the defaults`() {
        prefs().edit().putString("grid_items_portrait_json", "{broken").commit()
        assertEquals(defaults(isLandscape = false), ids(isLandscape = false))

        prefs().edit().putString("grid_items_portrait_json", "null").commit()
        assertEquals(defaults(isLandscape = false), ids(isLandscape = false))

        prefs().edit().putString("grid_items_portrait_json", "[null]").commit()
        assertEquals(defaults(isLandscape = false), ids(isLandscape = false))
    }

    @Test
    fun `when the saved list is empty, should keep the home empty`() {
        prefs().edit().putString("grid_items_portrait_json", "[]").commit()

        assertTrue(repository.getGridItems(isLandscape = false).isEmpty())
        assertTrue(repository.hasSavedLayout())
    }

    @Test
    fun `when saved items miss fields, should fill ids, types and orientation`() {
        prefs().edit().putString(
            "grid_items_portrait_json",
            """[{"id":"","type":null,"scrollOrientation":null,"children":[{"id":null,"type":"CLOCK"}]}]"""
        ).commit()

        val item = repository.getGridItems(isLandscape = false).single()

        assertTrue(item.id.startsWith("item_"))
        assertEquals(LauncherItemType.APPS_LIST, item.type)
        assertEquals(ScrollOrientation.VERTICAL, item.scrollOrientation)
        assertTrue(item.safeChildren.single().id.startsWith("item_"))
    }
}
