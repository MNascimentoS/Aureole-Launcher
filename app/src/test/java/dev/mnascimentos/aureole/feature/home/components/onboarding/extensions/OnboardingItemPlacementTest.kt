package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingItemSize
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingPlacementRequest
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingItemPlacementTest {

    private val portrait = GridLimits(GridEngineUtils.PORTRAIT_MAX_COLS, GridEngineUtils.PORTRAIT_MAX_ROWS)

    private fun request(
        type: LauncherItemType,
        items: List<LauncherItemState> = emptyList(),
        limits: GridLimits = portrait,
        isLeftHanded: Boolean = false
    ) = OnboardingPlacementRequest(
        id = "new",
        type = type,
        items = items,
        limits = limits,
        isLandscape = false,
        isLeftHanded = isLeftHanded
    )

    @Test
    fun `when the type is a clock, container or widget list, should use a size smaller than the default`() {
        assertEquals(OnboardingItemSize(10, 4), onboardingDefaultSize(LauncherItemType.CLOCK))
        assertEquals(OnboardingItemSize(4, 6), onboardingDefaultSize(LauncherItemType.SHORTCUTS_CONTAINER))
        assertEquals(OnboardingItemSize(14, 6), onboardingDefaultSize(LauncherItemType.WIDGET_LIST))
        val clockDefault = GridDefaults.getDefaultSpanForType(LauncherItemType.CLOCK).first
        assertTrue(10 * 4 < clockDefault.first * clockDefault.second)
    }

    @Test
    fun `when the type has no compact size, should keep its default size`() {
        val default = GridDefaults.getDefaultSpanForType(LauncherItemType.APPS_LIST).first

        assertEquals(
            OnboardingItemSize(default.first, default.second),
            onboardingDefaultSize(LauncherItemType.APPS_LIST)
        )
    }

    @Test
    fun `when the default spot is free, should place the compact item there with the new id`() {
        val item = placeOnboardingItem(request(LauncherItemType.SHORTCUTS_CONTAINER))

        val default = GridDefaults.getDefaultItem(LauncherItemType.SHORTCUTS_CONTAINER, false, false)!!
        assertEquals("new", item?.id)
        assertEquals(default.col, item?.col)
        assertEquals(default.row, item?.row)
        assertEquals(4, item?.colSpan)
        assertEquals(6, item?.rowSpan)
    }

    @Test
    fun `when the default spot is taken, should place the item in the first free slot`() {
        val blocker = LauncherItemState(id = "b", type = LauncherItemType.QUICK_BUTTON, colSpan = 20, rowSpan = 4)

        val item = placeOnboardingItem(request(LauncherItemType.CLOCK, items = listOf(blocker)))

        assertEquals(0, item?.col)
        assertEquals(4, item?.row)
        assertEquals(10, item?.colSpan)
        assertEquals(4, item?.rowSpan)
    }

    @Test
    fun `when the left handed layout mirrors the default, should still stay inside the grid`() {
        val item = placeOnboardingItem(request(LauncherItemType.SHORTCUTS_CONTAINER, isLeftHanded = true))

        assertNotNull(item)
        assertTrue(item!!.col + item.colSpan <= portrait.maxCols)
    }

    @Test
    fun `when the grid has no room for the compact size, should return null`() {
        val full = LauncherItemState(id = "full", type = LauncherItemType.QUICK_BUTTON, colSpan = 20, rowSpan = 30)

        assertNull(placeOnboardingItem(request(LauncherItemType.CLOCK, items = listOf(full))))
    }

    @Test
    fun `when the type has no default item, should fall back to the first free slot`() {
        val item = placeOnboardingItem(request(LauncherItemType.QUICK_BUTTON))

        assertEquals(0, item?.col)
        assertEquals(0, item?.row)
        assertEquals(LauncherItemType.QUICK_BUTTON, item?.type)
    }
}
