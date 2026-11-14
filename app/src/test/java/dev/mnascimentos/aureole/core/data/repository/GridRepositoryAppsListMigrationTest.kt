package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

/** RF-12: the layout JSON saved by 0.9.30 or older is migrated on the first read and saved back. */
@RunWith(RobolectricTestRunner::class)
class GridRepositoryAppsListMigrationTest {

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

    private fun saveLegacy(key: String, json: String) {
        prefs().edit().putString(key, json).commit()
    }

    @Test
    fun `a home of 0_9_30 keeps its lists, in both orientations and inside scroll views`() {
        saveLegacy(PORTRAIT_KEY, LEGACY_JSON)
        saveLegacy(LANDSCAPE_KEY, LEGACY_JSON)

        for (isLandscape in listOf(false, true)) {
            val items = repository.getGridItems(isLandscape = isLandscape)

            val (all, favs, scroll) = items
            assertEquals(listOf("all", "favs", "scroll"), items.map { it.id })
            assertEquals(LauncherItemType.APPS_LIST, all.type)
            assertTrue(all.isShowingAllApps)
            assertEquals(LauncherItemType.APPS_LIST, favs.type)
            assertFalse(favs.isShowingAllApps)
            assertEquals(listOf(true, false), scroll.safeChildren.map { it.showAllApps })
            assertEquals(listOf("child_all", "child_favs"), scroll.safeChildren.map { it.id })
            assertEquals(listOf(2, 3, 5, 7), listOf(favs.col, favs.row, favs.colSpan, favs.rowSpan))
        }
    }

    @Test
    fun `the migrated layout is saved back, so a second read gives the same home`() {
        saveLegacy(PORTRAIT_KEY, LEGACY_JSON)

        val first = repository.getGridItems(isLandscape = false)
        val saved = prefs().getString(PORTRAIT_KEY, null).orEmpty()

        assertFalse(saved.contains("ALL_APPS_LIST"))
        assertTrue(saved.contains("\"showAllApps\":false"))
        assertEquals(first, repository.getGridItems(isLandscape = false))
    }

    @Test
    fun `a home saved only under the legacy key is migrated too`() {
        saveLegacy(LEGACY_KEY, """[{"id":"all","type":"ALL_APPS_LIST"}]""")

        val list = repository.getGridItems(isLandscape = false).single()

        assertEquals(LauncherItemType.APPS_LIST, list.type)
        assertTrue(list.isShowingAllApps)
    }

    @Test
    fun `a list created in this version still shows all apps after it is saved and read back`() {
        val newList = LauncherItemState(id = "new", type = LauncherItemType.APPS_LIST)
        val off = LauncherItemState(id = "off", type = LauncherItemType.APPS_LIST, showAllApps = false)

        repository.saveGridItems(listOf(newList, off), isLandscape = false)
        val readBack = repository.getGridItems(isLandscape = false)

        assertEquals(listOf(true, false), readBack.map { it.showAllApps })
    }

    private companion object {
        const val LEGACY_KEY = "grid_items_json"
        const val PORTRAIT_KEY = "grid_items_portrait_json"
        const val LANDSCAPE_KEY = "grid_items_landscape_json"
        const val LEGACY_JSON = """[
            {"id":"all","type":"ALL_APPS_LIST","col":0,"row":0,"colSpan":5,"rowSpan":5},
            {"id":"favs","type":"APPS_LIST","col":2,"row":3,"colSpan":5,"rowSpan":7},
            {"id":"scroll","type":"SCROLL_VIEW","scrollOrientation":"VERTICAL","children":[
                {"id":"child_all","type":"ALL_APPS_LIST"},
                {"id":"child_favs","type":"APPS_LIST"}
            ]}
        ]"""
    }
}
