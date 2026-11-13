package dev.mnascimentos.aureole.feature.home.components.overlays.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.feature.home.components.overlays.model.CustomizeAddItem
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CustomizeActionsTest {

    private data class Call(val type: LauncherItemType, val orientation: ScrollOrientation?)

    @Test
    fun `when adding a typed item, should call onAddGridItem with its type and no orientation`() {
        val calls = mutableListOf<Call>()
        val actions = createDefaultHomeScreenActions().copy(onAddGridItem = { type, _, o -> calls += Call(type, o) })

        listOf(
            CustomizeAddItem.CONTAINER,
            CustomizeAddItem.WIDGET_GROUP,
            CustomizeAddItem.QUICK_BUTTON
        ).forEach(actions::addCustomizeItem)

        assertEquals(
            listOf(
                Call(LauncherItemType.SHORTCUTS_CONTAINER, null),
                Call(LauncherItemType.WIDGET_LIST, null),
                Call(LauncherItemType.QUICK_BUTTON, null)
            ),
            calls
        )
    }

    @Test
    fun `when adding a scrolling group, should be vertical`() {
        val calls = mutableListOf<Call>()
        val actions = createDefaultHomeScreenActions().copy(onAddGridItem = { type, _, o -> calls += Call(type, o) })

        actions.addCustomizeItem(CustomizeAddItem.SCROLL_GROUP)

        assertEquals(listOf(Call(LauncherItemType.SCROLL_VIEW, ScrollOrientation.VERTICAL)), calls)
    }

    @Test
    fun `when adding an apps list, should ask for a new list and not add a grid item`() {
        var asked = false
        var added = false
        val actions = createDefaultHomeScreenActions().copy(
            onAddAppsList = { asked = true },
            onAddGridItem = { _, _, _ -> added = true }
        )

        actions.addCustomizeItem(CustomizeAddItem.APPS_LIST)

        assertTrue(asked)
        assertEquals(false, added)
    }

    @Test
    fun `when choosing settings, should open the settings and not add a grid item`() {
        var opened = 0
        var added = false
        val actions = createDefaultHomeScreenActions().copy(
            onSettingsClick = { opened++ },
            onAddGridItem = { _, _, _ -> added = true }
        )

        actions.addCustomizeItem(CustomizeAddItem.SETTINGS)

        assertEquals(1, opened)
        assertEquals(false, added)
    }

    @Test
    fun `when adding a clock, should add a new clock and not a plain grid item`() {
        var added = 0
        var gridItems = 0
        val actions = createDefaultHomeScreenActions().copy(
            onAddClock = { added++ },
            onAddGridItem = { _, _, _ -> gridItems++ }
        )

        actions.addCustomizeItem(CustomizeAddItem.CLOCK)
        actions.addCustomizeItem(CustomizeAddItem.CLOCK)

        assertEquals(2, added)
        assertEquals(0, gridItems)
    }

    @Test
    fun `when adding a widget, should flag a single widget and open the picker`() {
        var single: Boolean? = null
        var pickerArg: String? = "unset"
        val actions = createDefaultHomeScreenActions().copy(
            onSetIsAddingSingleWidget = { single = it },
            onAddWidgetClick = { pickerArg = it }
        )

        actions.addCustomizeItem(CustomizeAddItem.WIDGET)

        assertEquals(true, single)
        assertEquals(null, pickerArg)
    }
}
