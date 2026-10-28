package dev.mnascimentos.aureole.feature.home.grid

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GridDefaultsTest {

    private val variations = listOf(false to false, false to true, true to false, true to true)

    private val expectedIds = listOf(
        GridDefaults.CLOCK_ITEM_ID,
        GridDefaults.WIDGETS_ITEM_ID,
        GridDefaults.APPS_LIST_ITEM_ID,
        GridDefaults.CONTAINER_ITEM_ID
    )

    private fun limits(isLandscape: Boolean) = if (isLandscape) {
        GridLimits(GridEngineUtils.LANDSCAPE_MAX_COLS, GridEngineUtils.LANDSCAPE_MAX_ROWS)
    } else {
        GridLimits(GridEngineUtils.PORTRAIT_MAX_COLS, GridEngineUtils.PORTRAIT_MAX_ROWS)
    }

    private fun LauncherItemState.placement() = listOf(col, row, colSpan, rowSpan)

    @Test
    fun `when getting default span for all item types, should return non-null valid spans`() {
        for (type in LauncherItemType.entries) {
            val (spans, minSpans) = GridDefaults.getDefaultSpanForType(type)
            assertNotNull(spans)
            assertNotNull(minSpans)
            assertEquals(Pair(1, 1), minSpans)
        }
    }

    @Test
    fun `when getting the default layout, should have no overlapping items in any variation`() {
        for ((isLandscape, isLeftHanded) in variations) {
            val items = GridDefaults.getDefaultGridItems(isLandscape, isLeftHanded)
            items.forEach { item ->
                assertFalse(GridEngineUtils.checkCollisionWithOthers(item, items))
            }
        }
    }

    @Test
    fun `when getting the default layout, should keep every item inside the grid`() {
        for ((isLandscape, isLeftHanded) in variations) {
            GridDefaults.getDefaultGridItems(isLandscape, isLeftHanded).forEach { item ->
                assertTrue(
                    GridEngineUtils.isWithinBounds(item.col, item.row, item.colSpan, item.rowSpan, limits(isLandscape))
                )
            }
        }
    }

    @Test
    fun `when getting the default layout, should hold the four item types with stable ids`() {
        val expectedTypes = listOf(
            LauncherItemType.CLOCK,
            LauncherItemType.WIDGET_LIST,
            LauncherItemType.APPS_LIST,
            LauncherItemType.SHORTCUTS_CONTAINER
        )
        for ((isLandscape, isLeftHanded) in variations) {
            val items = GridDefaults.getDefaultGridItems(isLandscape, isLeftHanded)
            assertEquals(expectedTypes, items.map { it.safeType })
            assertEquals(expectedIds, items.map { it.id })
        }
    }

    @Test
    fun `when getting the portrait layout, should match the v3 placements`() {
        val right = GridDefaults.getDefaultGridItems(isLandscape = false).map { it.placement() }
        val left = GridDefaults.getDefaultGridItems(isLandscape = false, isLeftHanded = true).map { it.placement() }

        assertEquals(
            listOf(listOf(0, 0, 20, 6), listOf(0, 6, 20, 6), listOf(0, 12, 16, 18), listOf(16, 12, 4, 18)),
            right
        )
        assertEquals(
            listOf(listOf(0, 0, 20, 6), listOf(0, 6, 20, 6), listOf(4, 12, 16, 18), listOf(0, 12, 4, 18)),
            left
        )
    }

    @Test
    fun `when getting the landscape layout, should match the v3 placements`() {
        val right = GridDefaults.getDefaultGridItems(isLandscape = true).map { it.placement() }
        val left = GridDefaults.getDefaultGridItems(isLandscape = true, isLeftHanded = true).map { it.placement() }

        assertEquals(
            listOf(listOf(0, 0, 14, 7), listOf(14, 0, 12, 20), listOf(0, 7, 14, 13), listOf(26, 0, 4, 20)),
            right
        )
        assertEquals(
            listOf(listOf(16, 0, 14, 7), listOf(4, 0, 12, 20), listOf(16, 7, 14, 13), listOf(0, 0, 4, 20)),
            left
        )
    }

    @Test
    fun `when asking for one default item, should return it or null when the layout has none`() {
        val apps = GridDefaults.getDefaultItem(LauncherItemType.APPS_LIST, isLandscape = false, isLeftHanded = true)

        assertEquals(GridDefaults.APPS_LIST_ITEM_ID, apps?.id)
        assertEquals(4, apps?.col)
        assertNull(
            GridDefaults.getDefaultItem(LauncherItemType.QUICK_BUTTON, isLandscape = false, isLeftHanded = false)
        )
    }

    @Test
    fun `when there are no favorites, should put the all apps list at the apps spot`() {
        for ((isLandscape, isLeftHanded) in variations) {
            val withFavorites = GridDefaults.getDefaultGridItems(isLandscape, isLeftHanded)
            val withoutFavorites = GridDefaults.getDefaultGridItems(isLandscape, isLeftHanded, hasFavorites = false)

            assertEquals(LauncherItemType.ALL_APPS_LIST, withoutFavorites[2].safeType)
            assertEquals(withFavorites[2].copy(type = LauncherItemType.ALL_APPS_LIST), withoutFavorites[2])
        }
        val allApps = GridDefaults.getDefaultItem(
            LauncherItemType.ALL_APPS_LIST,
            isLandscape = true,
            isLeftHanded = false
        )
        assertEquals(listOf(0, 7), listOf(allApps?.col, allApps?.row))
    }
}
