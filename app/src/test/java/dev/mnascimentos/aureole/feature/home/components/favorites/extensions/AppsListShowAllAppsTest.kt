package dev.mnascimentos.aureole.feature.home.components.favorites.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test

/** "Show all apps" of one apps list, set in place on the home and inside scroll groups (RF-10). */
class AppsListShowAllAppsTest {

    private val list = LauncherItemState(id = "list", type = LauncherItemType.APPS_LIST, showAllApps = true)
    private val other = LauncherItemState(id = "other", type = LauncherItemType.APPS_LIST, showAllApps = true)
    private val clock = LauncherItemState(id = "clock", type = LauncherItemType.CLOCK)
    private val child = LauncherItemState(id = "child", type = LauncherItemType.APPS_LIST, showAllApps = false)
    private val group = LauncherItemState(id = "group", type = LauncherItemType.SCROLL_VIEW, children = listOf(child))

    @Test
    fun `turning it off changes only the list with that id and keeps the id`() {
        val updated = listOf(list, other, clock).withShowAllApps("list", show = false)

        assertEquals(listOf(list.copy(showAllApps = false), other, clock), updated)
    }

    @Test
    fun `a list inside a scroll group is reached through the group's children`() {
        val updated = listOf(clock, group).withShowAllApps("child", show = true)

        assertEquals(true, updated[1].safeChildren.single().showAllApps)
        assertEquals("child", updated[1].safeChildren.single().id)
        assertSame(clock, updated[0])
    }

    @Test
    fun `when nothing changes, the same list comes back so the caller skips the save`() {
        val items = listOf(list, group)

        assertSame(items, items.withShowAllApps("list", show = true))
        assertSame(items, items.withShowAllApps("missing", show = false))
        assertSame(items, items.withShowAllApps("clock", show = false))
    }

    @Test
    fun `an item of another type with that id is left alone`() {
        val items = listOf(clock)

        assertSame(items, items.withShowAllApps("clock", show = true))
    }

    @Test
    fun `findAppsList finds a list on the home and inside a group, and nothing for other types`() {
        val items = listOf(list, clock, group)

        assertEquals(list, items.findAppsList("list"))
        assertEquals(child, items.findAppsList("child"))
        assertNull(items.findAppsList("clock"))
        assertNull(items.findAppsList("missing"))
    }

    @Test
    fun `the state changes the list in both orientations`() {
        val state = MainUiState(
            gridItems = listOf(list),
            portraitGridItems = listOf(list),
            landscapeGridItems = listOf(clock, list.copy(col = 4))
        )

        val updated = state.withShowAllApps("list", show = false)

        assertEquals(false, updated.gridItems.single().showAllApps)
        assertEquals(false, updated.portraitGridItems.single().showAllApps)
        assertEquals(false, updated.landscapeGridItems.findAppsList("list")?.showAllApps)
        assertEquals(4, updated.landscapeGridItems.findAppsList("list")?.col)
    }

    @Test
    fun `in the grid edit mode, the layout the edit started from changes too and is what gets saved`() {
        val draft = listOf(list.copy(col = 3))
        val state = MainUiState(
            isGridEditMode = true,
            gridItems = draft,
            portraitGridItems = draft,
            landscapeGridItems = listOf(list),
            cachedGridItems = listOf(list),
            cachedPortraitGridItems = listOf(list),
            cachedLandscapeGridItems = listOf(list)
        )

        val updated = state.withShowAllApps("list", show = false)

        assertEquals(listOf(list.copy(col = 3, showAllApps = false)), updated.portraitGridItems)
        assertEquals(listOf(list.copy(showAllApps = false)), updated.cachedPortraitGridItems)
        assertEquals(listOf(list.copy(showAllApps = false)), updated.savedGridItems(isLandscape = false))
        assertEquals(listOf(list.copy(showAllApps = false)), updated.savedGridItems(isLandscape = true))
    }

    @Test
    fun `out of the edit mode, the saved layout is the current one`() {
        val state = MainUiState(portraitGridItems = listOf(list), landscapeGridItems = listOf(clock))

        assertEquals(listOf(list), state.savedGridItems(isLandscape = false))
        assertEquals(listOf(clock), state.savedGridItems(isLandscape = true))
    }
}
