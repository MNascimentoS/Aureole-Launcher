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
    fun `when in portrait, should give the clock 10x4, the container 4x6 and the widget list its default`() {
        assertEquals(OnboardingItemSize(10, 4), onboardingDefaultSize(LauncherItemType.CLOCK, isLandscape = false))
        assertEquals(
            OnboardingItemSize(4, 6),
            onboardingDefaultSize(LauncherItemType.SHORTCUTS_CONTAINER, isLandscape = false)
        )
        assertEquals(OnboardingItemSize(14, 6), onboardingDefaultSize(LauncherItemType.WIDGET_LIST, false))
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

    @Test
    fun `when in landscape, should give the clock, container and widget list the smallest size they accept`() {
        listOf(
            LauncherItemType.CLOCK,
            LauncherItemType.SHORTCUTS_CONTAINER,
            LauncherItemType.WIDGET_LIST
        ).forEach { type ->
            val minimum = GridDefaults.getDefaultSpanForType(type).second
            assertEquals(
                OnboardingItemSize(minimum.first, minimum.second),
                onboardingDefaultSize(type, isLandscape = true)
            )
        }
    }

    @Test
    fun `when the grid is landscape, should place the item at the landscape size`() {
        val landscape = GridLimits(GridEngineUtils.LANDSCAPE_MAX_COLS, GridEngineUtils.LANDSCAPE_MAX_ROWS)

        val item = placeOnboardingItem(request(LauncherItemType.CLOCK, limits = landscape).copy(isLandscape = true))

        assertEquals(1, item?.colSpan)
        assertEquals(1, item?.rowSpan)
    }

    @Test
    fun `when the size fits nowhere, should use the largest size that fits`() {
        val quick = LauncherItemType.QUICK_BUTTON
        val right = LauncherItemState(id = "r", type = quick, col = 5, colSpan = 15, rowSpan = 30)
        val below = LauncherItemState(id = "b", type = quick, row = 3, colSpan = 5, rowSpan = 27)

        val item = placeOnboardingItem(request(LauncherItemType.CLOCK, items = listOf(right, below)))

        assertEquals(listOf(0, 0), listOf(item?.col, item?.row))
        assertEquals(listOf(5, 3), listOf(item?.colSpan, item?.rowSpan))
    }
}
