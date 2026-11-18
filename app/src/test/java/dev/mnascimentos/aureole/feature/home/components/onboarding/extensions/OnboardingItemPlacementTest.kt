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
    fun `when in portrait, should give the clock 20x6, the widget list 20x6 and the container 4x18`() {
        assertEquals(OnboardingItemSize(20, 6), onboardingDefaultSize(LauncherItemType.CLOCK, isLandscape = false))
        assertEquals(
            OnboardingItemSize(4, 18),
            onboardingDefaultSize(LauncherItemType.SHORTCUTS_CONTAINER, isLandscape = false)
        )
        assertEquals(OnboardingItemSize(20, 6), onboardingDefaultSize(LauncherItemType.WIDGET_LIST, false))
        val layoutClock = GridDefaults.getDefaultItem(LauncherItemType.CLOCK, false, false)!!
        assertEquals(
            OnboardingItemSize(layoutClock.colSpan, layoutClock.rowSpan),
            onboardingDefaultSize(LauncherItemType.CLOCK)
        )
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
    fun `when the default spot is free, should place the item there at the layout size with the new id`() {
        val item = placeOnboardingItem(request(LauncherItemType.SHORTCUTS_CONTAINER))

        val default = GridDefaults.getDefaultItem(LauncherItemType.SHORTCUTS_CONTAINER, false, false)!!
        assertEquals("new", item?.id)
        assertEquals(default.col, item?.col)
        assertEquals(default.row, item?.row)
        assertEquals(4, item?.colSpan)
        assertEquals(18, item?.rowSpan)
    }

    @Test
    fun `when the default spot is free, should place the widget list on row 6 at 20x6`() {
        val item = placeOnboardingItem(request(LauncherItemType.WIDGET_LIST))

        assertEquals(listOf(0, 6, 20, 6), listOf(item?.col, item?.row, item?.colSpan, item?.rowSpan))
    }

    @Test
    fun `when left handed, should mirror the container to the first column`() {
        val item = placeOnboardingItem(request(LauncherItemType.SHORTCUTS_CONTAINER, isLeftHanded = true))

        assertEquals(listOf(0, 12, 4, 18), listOf(item?.col, item?.row, item?.colSpan, item?.rowSpan))
    }

    @Test
    fun `when the default spot is taken, should place the item in the first free slot`() {
        val blocker = LauncherItemState(id = "b", type = LauncherItemType.QUICK_BUTTON, colSpan = 20, rowSpan = 4)

        val item = placeOnboardingItem(request(LauncherItemType.CLOCK, items = listOf(blocker)))

        assertEquals(0, item?.col)
        assertEquals(4, item?.row)
        assertEquals(20, item?.colSpan)
        assertEquals(6, item?.rowSpan)
    }

    @Test
    fun `when the default spot is free, should place the clock at the layout's spot and size`() {
        val item = placeOnboardingItem(request(LauncherItemType.CLOCK))

        val default = GridDefaults.getDefaultItem(LauncherItemType.CLOCK, false, false)!!
        assertEquals(
            listOf(default.col, default.row, 20, 6),
            listOf(item?.col, item?.row, item?.colSpan, item?.rowSpan)
        )
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
    fun `when in landscape, should give the clock, container and widget list the landscape layout size`() {
        listOf(
            LauncherItemType.CLOCK,
            LauncherItemType.SHORTCUTS_CONTAINER,
            LauncherItemType.WIDGET_LIST
        ).forEach { type ->
            val layout = GridDefaults.getDefaultItem(type, isLandscape = true, isLeftHanded = false)!!
            assertEquals(
                OnboardingItemSize(layout.colSpan, layout.rowSpan),
                onboardingDefaultSize(type, isLandscape = true)
            )
        }
    }

    @Test
    fun `when the grid is landscape, should place the item at the landscape layout spot and size`() {
        val landscape = GridLimits(GridEngineUtils.LANDSCAPE_MAX_COLS, GridEngineUtils.LANDSCAPE_MAX_ROWS)

        val item = placeOnboardingItem(
            request(LauncherItemType.SHORTCUTS_CONTAINER, limits = landscape).copy(isLandscape = true)
        )

        assertEquals(listOf(26, 0, 4, 20), listOf(item?.col, item?.row, item?.colSpan, item?.rowSpan))
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
