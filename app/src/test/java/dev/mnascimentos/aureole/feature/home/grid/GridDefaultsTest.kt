package dev.mnascimentos.aureole.feature.home.grid

import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class GridDefaultsTest {

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
    fun `when getting default portrait grid items, should return expected list of default items`() {
        val items = GridDefaults.getDefaultGridItems(isLandscape = false)
        assertEquals(3, items.size)

        val clock = items.find { it.id == "clock_item" }
        assertNotNull(clock)
        assertEquals(LauncherItemType.CLOCK, clock?.type)

        val container = items.find { it.id == "container_item" }
        assertNotNull(container)
        assertEquals(LauncherItemType.SHORTCUTS_CONTAINER, container?.type)

        val appsList = items.find { it.id == "apps_list_item" }
        assertNotNull(appsList)
        assertEquals(LauncherItemType.APPS_LIST, appsList?.type)
    }

    @Test
    fun `when getting default landscape grid items, should return expected landscape layout`() {
        val items = GridDefaults.getDefaultGridItems(isLandscape = true)
        assertEquals(3, items.size)

        val clock = items.find { it.id == "clock_item" }
        assertNotNull(clock)
        assertEquals(20, clock?.colSpan)
        assertEquals(6, clock?.rowSpan)

        val container = items.find { it.id == "container_item" }
        assertNotNull(container)
        assertEquals(24, container?.col)
        assertEquals(0, container?.row)

        val appsList = items.find { it.id == "apps_list_item" }
        assertNotNull(appsList)
        assertEquals(0, appsList?.col)
        assertEquals(6, appsList?.row)
    }
}
