package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.LayoutPreset
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.LayoutPresetPlan
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingSurface
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.PresetFolder
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.grid.model.GridLimits
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingPresetLayoutTest {

    private var ids = 0
    private val newId: () -> String = { "id${ids++}" }

    private val minimal = LayoutPresetPlan(LayoutPreset.MINIMAL, favorites = listOf("a"))
    private val productive = LayoutPresetPlan(
        LayoutPreset.PRODUCTIVE,
        favorites = listOf("a"),
        folders = listOf(PresetFolder(category = 4, name = "Social", packageNames = listOf("b", "c"))),
        groupApps = listOf("d")
    )
    private val widgets = LayoutPresetPlan(LayoutPreset.WIDGETS, favorites = listOf("a"), groupApps = listOf("b"))

    private fun List<LauncherItemState>.of(type: LauncherItemType) = single { it.safeType == type }

    private fun List<LauncherItemState>.fitAndDoNotOverlap(): Boolean = all { item ->
        GridEngineUtils.isWithinBounds(
            item.col,
            item.row,
            item.colSpan,
            item.rowSpan,
            GridLimits(
                GridEngineUtils.PORTRAIT_MAX_COLS,
                GridEngineUtils.PORTRAIT_MAX_ROWS
            )
        ) && !GridEngineUtils.checkCollisionWithOthers(item, this)
    }

    @Test
    fun `only a home empty in both orientations should take a preset`() {
        val item = LauncherItemState(id = "x", type = LauncherItemType.CLOCK)

        assertTrue(canTakeLayoutPreset(emptyList(), emptyList()))
        assertFalse(canTakeLayoutPreset(listOf(item), emptyList()))
        assertFalse(canTakeLayoutPreset(emptyList(), listOf(item)))
    }

    @Test
    fun `minimal should place a clock and a full width favorites list, with fresh ids`() {
        val items = presetPortraitItems(minimal, isLeftHanded = false, newId = newId)

        assertEquals(setOf(LauncherItemType.CLOCK, LauncherItemType.APPS_LIST), items.map { it.safeType }.toSet())
        assertEquals(GridEngineUtils.PORTRAIT_MAX_COLS, items.of(LauncherItemType.APPS_LIST).colSpan)
        assertFalse(items.of(LauncherItemType.APPS_LIST).isShowingAllApps)
        assertTrue(items.none { it.id == GridDefaults.CLOCK_ITEM_ID || it.id == GridDefaults.APPS_LIST_ITEM_ID })
        assertTrue(items.fitAndDoNotOverlap())
    }

    @Test
    fun `the widget rows of the default layout should stay free for the widget step`() {
        val widgetSlot = checkNotNull(GridDefaults.getDefaultItem(LauncherItemType.WIDGET_LIST, false, false))

        listOf(minimal, productive, widgets).forEach { plan ->
            val items = presetPortraitItems(plan, isLeftHanded = false, newId = newId)
            assertFalse(GridEngineUtils.checkCollisionWithOthers(widgetSlot, items))
        }
    }

    @Test
    fun `productive should put the group on the side as the default layout does`() {
        val items = presetPortraitItems(productive, isLeftHanded = false, newId = newId)
        val defaultGroup = checkNotNull(
            GridDefaults.getDefaultItem(LauncherItemType.SHORTCUTS_CONTAINER, isLandscape = false, isLeftHanded = false)
        )

        val group = items.of(LauncherItemType.SHORTCUTS_CONTAINER)
        assertEquals(defaultGroup.col to defaultGroup.colSpan, group.col to group.colSpan)
        assertTrue(items.fitAndDoNotOverlap())
    }

    @Test
    fun `widgets should lay the group as a dock under the favorites`() {
        val items = presetPortraitItems(widgets, isLeftHanded = false, newId = newId)

        val apps = items.of(LauncherItemType.APPS_LIST)
        val dock = items.of(LauncherItemType.SHORTCUTS_CONTAINER)
        assertEquals(GridEngineUtils.PORTRAIT_MAX_COLS, dock.colSpan)
        assertEquals(PRESET_DOCK_ROW_SPAN, dock.rowSpan)
        assertEquals(apps.row + apps.rowSpan, dock.row)
        assertEquals(GridEngineUtils.PORTRAIT_MAX_ROWS, dock.row + dock.rowSpan)
        assertTrue(items.fitAndDoNotOverlap())
    }

    @Test
    fun `left-handed should mirror the group to the other side`() {
        val right = presetPortraitItems(productive, isLeftHanded = false, newId = newId)
        val left = presetPortraitItems(productive, isLeftHanded = true, newId = newId)

        assertEquals(0, left.of(LauncherItemType.SHORTCUTS_CONTAINER).col)
        assertEquals(right.of(LauncherItemType.APPS_LIST).colSpan, left.of(LauncherItemType.APPS_LIST).colSpan)
    }

    @Test
    fun `with no favorites, the list should show all apps`() {
        val items = presetPortraitItems(LayoutPresetPlan(LayoutPreset.MINIMAL), isLeftHanded = false, newId = newId)

        assertTrue(items.of(LauncherItemType.APPS_LIST).isShowingAllApps)
    }

    @Test
    fun `the group should hold the folders first and then the apps, as a dock for widgets`() {
        val side = presetContainerModel(productive, id = "g", position = "Space Between", isExpandCell = false)
        val dock = presetContainerModel(widgets, id = "g", position = "Space Between", isExpandCell = false)

        assertEquals(listOf("Social"), side.folders.map { it.name })
        assertEquals(listOf("b", "c"), side.folders.single().appPackageNames)
        assertEquals("g", side.folders.single().panelId)
        assertEquals(listOf("d"), side.appPackageNames)
        assertEquals("Vertical", side.orientation)
        assertEquals("Horizontal", dock.orientation)
        assertFalse(side.isBackgroundEnabled)
    }

    @Test
    fun `id of should find the first item of a type`() {
        val items = presetPortraitItems(minimal, isLeftHanded = false, newId = newId)

        assertEquals(items.of(LauncherItemType.CLOCK).id, items.idOf(LauncherItemType.CLOCK))
        assertNull(items.idOf(LauncherItemType.SHORTCUTS_CONTAINER))
    }

    @Test
    fun `thumbnail should show the preset's shape before the plan is ready`() {
        val items = LayoutPreset.WIDGETS.thumbnailItems(plan = null, isLeftHanded = false)

        assertTrue(items.any { it.safeType == LauncherItemType.SHORTCUTS_CONTAINER })
        assertTrue(LayoutPreset.MINIMAL.thumbnailItems(plan = null, isLeftHanded = false).none {
            it.safeType == LauncherItemType.SHORTCUTS_CONTAINER
        })
    }

    @Test
    fun `the coach should leave at once and other surfaces fade`() {
        assertEquals(0, surfaceExitMillis(OnboardingSurface.GESTURE_COACH))
        assertTrue(surfaceExitMillis(OnboardingSurface.WELCOME) > 0)
    }

    @Test
    fun `every preset should list four lines of content`() {
        LayoutPreset.entries.forEach { assertEquals(4, it.contentLines.size) }
    }
}
