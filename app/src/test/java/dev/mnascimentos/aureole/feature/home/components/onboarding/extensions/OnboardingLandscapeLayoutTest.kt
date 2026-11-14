package dev.mnascimentos.aureole.feature.home.components.onboarding.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingLandscapeLayoutTest {

    private fun portraitItem(type: LauncherItemType, id: String, children: List<LauncherItemState> = emptyList()) =
        LauncherItemState(id = id, type = type, col = 3, row = 3, colSpan = 5, rowSpan = 5, children = children)

    @Test
    fun `with nothing in portrait, should be the default landscape layout`() {
        assertEquals(
            GridDefaults.getDefaultGridItems(isLandscape = true, isLeftHanded = false),
            onboardingLandscapeLayout(emptyList(), isLeftHanded = false)
        )
    }

    @Test
    fun `should show all apps in the apps list`() {
        val portrait = listOf(portraitItem(LauncherItemType.APPS_LIST, "my_apps").copy(showAllApps = false))

        val apps = onboardingLandscapeLayout(portrait, isLeftHanded = false)
            .single { it.safeType == LauncherItemType.APPS_LIST }

        assertEquals(true, apps.showAllApps)
        assertEquals("my_apps", apps.id)
    }

    @Test
    fun `a portrait widget stack should bring its children and keep the default spot`() {
        val child = LauncherItemState(id = "calendar", type = LauncherItemType.WIDGET_LIST, widgetId = 7)
        val portrait = listOf(portraitItem(LauncherItemType.WIDGET_LIST, "stack", children = listOf(child)))
        val defaultWidgets = GridDefaults.getDefaultItem(LauncherItemType.WIDGET_LIST, true, false)!!

        val widgets = onboardingLandscapeLayout(portrait, isLeftHanded = false)
            .single { it.safeType == LauncherItemType.WIDGET_LIST }

        assertEquals(listOf(child), widgets.safeChildren)
        assertEquals("stack", widgets.id)
        assertEquals(
            listOf(defaultWidgets.col, defaultWidgets.row, defaultWidgets.colSpan, defaultWidgets.rowSpan),
            listOf(widgets.col, widgets.row, widgets.colSpan, widgets.rowSpan)
        )
    }

    @Test
    fun `the container should keep its id so it shares the items`() {
        val portrait = listOf(portraitItem(LauncherItemType.SHORTCUTS_CONTAINER, "panel_1"))

        val container = onboardingLandscapeLayout(portrait, isLeftHanded = false)
            .single { it.safeType == LauncherItemType.SHORTCUTS_CONTAINER }

        assertEquals("panel_1", container.id)
    }

    @Test
    fun `left-handed should mirror the default spots`() {
        val layout = onboardingLandscapeLayout(emptyList(), isLeftHanded = true)

        assertEquals(GridDefaults.getDefaultGridItems(isLandscape = true, isLeftHanded = true), layout)
    }

    @Test
    fun `an item type that the default layout has no spot for should be left out`() {
        val portrait = listOf(portraitItem(LauncherItemType.SINGLE_APP_WIDGET, "extra"))

        val layout = onboardingLandscapeLayout(portrait, isLeftHanded = false)

        assertTrue(layout.none { it.id == "extra" })
    }
}
