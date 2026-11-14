package dev.mnascimentos.aureole.feature.home.components.favorites.extensions

import com.google.gson.Gson
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

/** The "Favorites" and "All apps" labels of one apps list (RF-47). */
class AppsListLabelsTest {

    private val list = LauncherItemState(id = "list", type = LauncherItemType.APPS_LIST, showAllApps = true)
    private val child = LauncherItemState(id = "child", type = LauncherItemType.APPS_LIST, showAllApps = true)
    private val group = LauncherItemState(id = "group", type = LauncherItemType.SCROLL_VIEW, children = listOf(child))

    @Test
    fun `labels show only when the list shows all apps and the labels are on`() {
        assertTrue(shouldShowLabels(showAllApps = true, showLabels = true))
        assertFalse(shouldShowLabels(showAllApps = true, showLabels = false))
        assertFalse(shouldShowLabels(showAllApps = false, showLabels = true))
        assertFalse(shouldShowLabels(showAllApps = false, showLabels = false))
    }

    @Test
    fun `a list saved before the switch has the labels on`() {
        val legacy = Gson().fromJson(
            """{"id":"a","type":"APPS_LIST","showAllApps":true}""",
            LauncherItemState::class.java
        )

        assertTrue(legacy.isShowingLabels)
        assertEquals(null, legacy.showLabels)
    }

    @Test
    fun `turning the labels off changes only the list with that id`() {
        val other = list.copy(id = "other")

        val updated = listOf(list, other).withShowLabels("list", show = false)

        assertEquals(listOf(list.copy(showLabels = false), other), updated)
    }

    @Test
    fun `a list inside a scroll group is reached through its children`() {
        val updated = listOf(group).withShowLabels("child", show = false)

        assertFalse(updated.single().safeChildren.single().isShowingLabels)
    }

    @Test
    fun `when nothing changes, the same list comes back`() {
        val items = listOf(list, group)

        assertSame(items, items.withShowLabels("list", show = true))
        assertSame(items, items.withShowLabels("missing", show = false))
    }

    @Test
    fun `the state changes both orientations and the saved layout of an edit`() {
        val state = MainUiState(
            gridItems = listOf(list),
            portraitGridItems = listOf(list),
            landscapeGridItems = listOf(list),
            cachedPortraitGridItems = listOf(list)
        )

        val updated = state.withShowLabels("list", show = false)

        assertFalse(updated.portraitGridItems.single().isShowingLabels)
        assertFalse(updated.landscapeGridItems.single().isShowingLabels)
        assertFalse(updated.gridItems.single().isShowingLabels)
        assertFalse(checkNotNull(updated.cachedPortraitGridItems).single().isShowingLabels)
    }
}
