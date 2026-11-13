package dev.mnascimentos.aureole.core.data.repository

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** RF-12: a home saved by 0.9.30 or older looks the same after the update. */
class AppsListMigrationTest {

    private fun item(id: String, type: LauncherItemType, showAllApps: Boolean? = null) =
        LauncherItemState(id = id, type = type, col = 1, row = 2, colSpan = 3, rowSpan = 4, showAllApps = showAllApps)

    private fun scrollView(vararg children: LauncherItemState) =
        LauncherItemState(id = "scroll", type = LauncherItemType.SCROLL_VIEW, children = children.toList())

    @Test
    fun `an all apps list becomes an apps list that shows all apps, with the same id and place`() {
        val migrated = item("all", LauncherItemType.ALL_APPS_LIST).migrateLegacyAppsList()

        assertEquals(item("all", LauncherItemType.APPS_LIST, showAllApps = true), migrated)
    }

    @Test
    fun `a favorites list becomes an apps list that shows only its favorites, with the same id`() {
        val migrated = item("favs", LauncherItemType.APPS_LIST).migrateLegacyAppsList()

        assertEquals(item("favs", LauncherItemType.APPS_LIST, showAllApps = false), migrated)
        assertFalse(migrated.isShowingAllApps)
    }

    @Test
    fun `an apps list that already states its toggle is kept`() {
        val on = item("on", LauncherItemType.APPS_LIST, showAllApps = true)
        val off = item("off", LauncherItemType.APPS_LIST, showAllApps = false)

        assertEquals(on, on.migrateLegacyAppsList())
        assertEquals(off, off.migrateLegacyAppsList())
    }

    @Test
    fun `the lists inside a scroll view are migrated too`() {
        val legacy = scrollView(item("all", LauncherItemType.ALL_APPS_LIST), item("favs", LauncherItemType.APPS_LIST))

        val migrated = legacy.migrateLegacyAppsList()

        assertEquals(
            listOf(
                item("all", LauncherItemType.APPS_LIST, showAllApps = true),
                item("favs", LauncherItemType.APPS_LIST, showAllApps = false)
            ),
            migrated.safeChildren
        )
        assertNull(migrated.showAllApps)
    }

    @Test
    fun `items of other kinds are left as they are`() {
        val clock = item("clock", LauncherItemType.CLOCK)

        assertEquals(clock, clock.migrateLegacyAppsList())
        assertEquals(clock, clock.withExplicitShowAllApps())
    }

    @Test
    fun `saving states the toggle a list shows now, a new list on, also inside a scroll view`() {
        val saved = scrollView(
            item("new", LauncherItemType.APPS_LIST),
            item("off", LauncherItemType.APPS_LIST, showAllApps = false),
            item("all", LauncherItemType.ALL_APPS_LIST)
        ).withExplicitShowAllApps()

        assertEquals(listOf(true, false, true), saved.safeChildren.map { it.showAllApps })
        assertTrue(saved.safeChildren.all { it.safeType == LauncherItemType.APPS_LIST })
    }

    @Test
    fun `a saved layout read back is not migrated again`() {
        val newList = item("new", LauncherItemType.APPS_LIST)

        val readBack = newList.withExplicitShowAllApps().migrateLegacyAppsList()

        assertTrue(readBack.isShowingAllApps)
    }
}
