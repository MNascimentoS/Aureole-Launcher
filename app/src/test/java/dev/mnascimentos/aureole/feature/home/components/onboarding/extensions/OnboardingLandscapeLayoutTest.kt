package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingLandscapeLayoutTest {

    private val allTypes = listOf(
        LauncherItemType.CLOCK,
        LauncherItemType.APPS_LIST,
        LauncherItemType.WIDGET_LIST,
        LauncherItemType.SHORTCUTS_CONTAINER
    )

    private fun portraitItem(type: LauncherItemType, children: List<LauncherItemState> = emptyList()) =
        LauncherItemState(
            id = "p_${type.name}",
            type = type,
            col = 3,
            row = 3,
            colSpan = 5,
            rowSpan = 5,
            children = children
        )

    private fun portrait(vararg types: LauncherItemType) = types.map { portraitItem(it) }

    private fun List<LauncherItemState>.of(type: LauncherItemType) = single { it.safeType == type }

    private fun LauncherItemState.box() = listOf(col, row, colSpan, rowSpan)

    @Test
    fun `with nothing in portrait, should be the default landscape layout`() {
        assertEquals(
            GridDefaults.getDefaultGridItems(isLandscape = true, isLeftHanded = false),
            onboardingLandscapeLayout(emptyList(), isLeftHanded = false)
        )
    }

    @Test
    fun `with everything in portrait, should sit where the default landscape layout puts it`() {
        val layout = onboardingLandscapeLayout(portrait(*allTypes.toTypedArray()), isLeftHanded = false)

        GridDefaults.getDefaultGridItems(isLandscape = true, isLeftHanded = false).forEach { slot ->
            assertEquals(slot.box(), layout.of(slot.safeType).box())
        }
    }

    @Test
    fun `without a clock, the apps list should take the whole left column`() {
        val layout = onboardingLandscapeLayout(
            portrait(LauncherItemType.APPS_LIST, LauncherItemType.WIDGET_LIST, LauncherItemType.SHORTCUTS_CONTAINER),
            isLeftHanded = false
        )

        assertEquals(listOf(0, 0, 10, 20), layout.of(LauncherItemType.APPS_LIST).box())
    }

    @Test
    fun `without a clock and without apps, the widgets should grow to the left`() {
        val layout = onboardingLandscapeLayout(
            portrait(LauncherItemType.WIDGET_LIST, LauncherItemType.SHORTCUTS_CONTAINER),
            isLeftHanded = false
        )

        assertEquals(listOf(0, 0, 26, 20), layout.of(LauncherItemType.WIDGET_LIST).box())
        assertEquals(listOf(26, 0, 4, 20), layout.of(LauncherItemType.SHORTCUTS_CONTAINER).box())
    }

    @Test
    fun `without widgets, the container should fill the center`() {
        val layout = onboardingLandscapeLayout(
            portrait(LauncherItemType.CLOCK, LauncherItemType.APPS_LIST, LauncherItemType.SHORTCUTS_CONTAINER),
            isLeftHanded = false
        )

        assertEquals(listOf(10, 0, 20, 20), layout.of(LauncherItemType.SHORTCUTS_CONTAINER).box())
    }

    @Test
    fun `without a container, the widgets should grow to the right`() {
        val layout = onboardingLandscapeLayout(
            portrait(LauncherItemType.CLOCK, LauncherItemType.APPS_LIST, LauncherItemType.WIDGET_LIST),
            isLeftHanded = false
        )

        assertEquals(listOf(10, 0, 20, 20), layout.of(LauncherItemType.WIDGET_LIST).box())
    }

    @Test
    fun `items the user did not add should not come along`() {
        val layout = onboardingLandscapeLayout(portrait(LauncherItemType.CLOCK), isLeftHanded = false)

        assertEquals(listOf(LauncherItemType.CLOCK), layout.map { it.safeType })
        assertEquals(listOf(0, 0, 30, 20), layout.single().box())
    }

    @Test
    fun `an item type with no spot in the default layout should be left out`() {
        val layout = onboardingLandscapeLayout(
            portrait(LauncherItemType.CLOCK, LauncherItemType.SINGLE_APP_WIDGET),
            isLeftHanded = false
        )

        assertTrue(layout.none { it.safeType == LauncherItemType.SINGLE_APP_WIDGET })
    }

    @Test
    fun `should show all apps in the apps list and keep the ids`() {
        val apps = portraitItem(LauncherItemType.APPS_LIST).copy(showAllApps = false)

        val result = onboardingLandscapeLayout(listOf(apps), isLeftHanded = false).single()

        assertEquals(true, result.showAllApps)
        assertEquals("p_APPS_LIST", result.id)
    }

    @Test
    fun `a portrait widget stack should bring its children`() {
        val child = LauncherItemState(id = "calendar", type = LauncherItemType.WIDGET_LIST, widgetId = 7)
        val stack = portraitItem(LauncherItemType.WIDGET_LIST, children = listOf(child))

        val widgets = onboardingLandscapeLayout(listOf(stack), isLeftHanded = false).single()

        assertEquals(listOf(child), widgets.safeChildren)
    }

    @Test
    fun `left-handed should mirror the layout`() {
        val layout = onboardingLandscapeLayout(portrait(*allTypes.toTypedArray()), isLeftHanded = true)

        GridDefaults.getDefaultGridItems(isLandscape = true, isLeftHanded = true).forEach { slot ->
            assertEquals(slot.box(), layout.of(slot.safeType).box())
        }
    }

    @Test
    fun `every combination should stay in bounds without overlapping`() {
        val limits = GridLimits(GridEngineUtils.LANDSCAPE_MAX_COLS, GridEngineUtils.LANDSCAPE_MAX_ROWS)
        for (mask in 1 until (1 shl allTypes.size)) {
            val types = allTypes.filterIndexed { index, _ -> mask and (1 shl index) != 0 }
            listOf(false, true).forEach { leftHanded ->
                val layout = onboardingLandscapeLayout(types.map { portraitItem(it) }, leftHanded)

                layout.forEach { item ->
                    assertTrue(
                        "$types",
                        GridEngineUtils.isWithinBounds(item.col, item.row, item.colSpan, item.rowSpan, limits)
                    )
                    assertFalse("$types", GridEngineUtils.checkCollisionWithOthers(item, layout))
                }
            }
        }
    }
}
