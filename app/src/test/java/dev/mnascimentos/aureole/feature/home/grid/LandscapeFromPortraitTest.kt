package dev.mnascimentos.aureole.feature.home.grid

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.extensions.landscapeFromPortrait
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LandscapeFromPortraitTest {

    private val limits = GridLimits(GridEngineUtils.LANDSCAPE_MAX_COLS, GridEngineUtils.LANDSCAPE_MAX_ROWS)

    private fun item(
        id: String,
        type: LauncherItemType,
        placement: List<Int> = listOf(0, 0, 4, 3)
    ) = LauncherItemState(
        id = id,
        type = type,
        col = placement[0],
        row = placement[1],
        colSpan = placement[2],
        rowSpan = placement[3]
    )

    private fun LauncherItemState.placement() = listOf(col, row, colSpan, rowSpan)

    private val portrait = listOf(
        item("my_clock", LauncherItemType.CLOCK, listOf(0, 0, 20, 6)),
        item("my_widgets", LauncherItemType.WIDGET_LIST, listOf(0, 6, 20, 6)),
        item("my_favorites", LauncherItemType.APPS_LIST, listOf(0, 12, 16, 18)),
        item("my_container", LauncherItemType.SHORTCUTS_CONTAINER, listOf(16, 12, 4, 18))
    )

    private fun assertValid(items: List<LauncherItemState>) {
        items.forEach { assertTrue(GridEngineUtils.isWithinBounds(it.col, it.row, it.colSpan, it.rowSpan, limits)) }
        items.forEachIndexed { index, a ->
            items.drop(
                index + 1
            ).forEach { b -> assertFalse("${a.id} overlaps ${b.id}", GridEngineUtils.hasAABBCollision(a, b)) }
        }
    }

    @Test
    fun `when the portrait has the four default items, should fit them into the v4 mold keeping the ids`() {
        val result = landscapeFromPortrait(portrait, isLeftHanded = false)

        assertEquals(listOf("my_clock", "my_widgets", "my_favorites", "my_container"), result.map { it.id })
        assertEquals(
            listOf(listOf(0, 0, 10, 6), listOf(10, 0, 16, 20), listOf(0, 6, 10, 14), listOf(26, 0, 4, 20)),
            result.map { it.placement() }
        )
        assertEquals(portrait.map { it.type }, result.map { it.type })
    }

    @Test
    fun `when left handed, should mirror the mold`() {
        val result = landscapeFromPortrait(portrait, isLeftHanded = true)

        assertEquals(
            listOf(listOf(20, 0, 10, 6), listOf(4, 0, 16, 20), listOf(20, 6, 10, 14), listOf(0, 0, 4, 20)),
            result.map { it.placement() }
        )
        assertValid(result)
    }

    @Test
    fun `when the portrait has only some items, should place only those`() {
        val result = landscapeFromPortrait(portrait.take(1), isLeftHanded = false)

        assertEquals(listOf("my_clock"), result.map { it.id })
        assertEquals(listOf(0, 0, 10, 6), result.single().placement())
    }

    @Test
    fun `when the portrait has the all apps list, should use the favorites spot`() {
        val result = landscapeFromPortrait(
            listOf(item("all", LauncherItemType.ALL_APPS_LIST, listOf(0, 12, 16, 18))),
            isLeftHanded = false
        )

        assertEquals(LauncherItemType.ALL_APPS_LIST, result.single().safeType)
        assertEquals(listOf(0, 6, 10, 14), result.single().placement())
    }

    @Test
    fun `when an item has no spot in the mold, should place it in a free slot without collisions`() {
        val extras = listOf(
            item("button", LauncherItemType.QUICK_BUTTON),
            item("scroll", LauncherItemType.SCROLL_VIEW, listOf(0, 0, 6, 6)),
            item("second_clock", LauncherItemType.CLOCK, listOf(0, 0, 5, 2))
        )

        // Without a container the right edge column is free: the extras go there.
        val base = portrait.take(3)

        val result = landscapeFromPortrait(base + extras, isLeftHanded = false)

        assertEquals(base.map { it.id } + listOf("button", "scroll", "second_clock"), result.map { it.id })
        assertValid(result)
        // The mold items stay where the mold puts them.
        assertEquals(listOf(0, 0, 10, 6), result.first().placement())
        assertTrue(result.drop(3).all { it.col >= 26 })
    }

    @Test
    fun `when the mold fills the grid and an extra item has no room, should leave it out`() {
        val tooBig = item("scroll", LauncherItemType.SCROLL_VIEW, listOf(0, 0, 4, 4))
            .copy(minColSpan = 4, minRowSpan = 4)

        val result = landscapeFromPortrait(portrait + tooBig, isLeftHanded = false)

        assertEquals(portrait.map { it.id }, result.map { it.id })
    }

    @Test
    fun `when the portrait is empty, should return an empty layout`() {
        assertTrue(landscapeFromPortrait(emptyList(), isLeftHanded = false).isEmpty())
    }

    @Test
    fun `when an item has a widget id, should keep it`() {
        val widgets = portrait[1].copy(widgetId = 42)

        val result = landscapeFromPortrait(listOf(widgets), isLeftHanded = false)

        assertEquals(42, result.single().widgetId)
    }
}
