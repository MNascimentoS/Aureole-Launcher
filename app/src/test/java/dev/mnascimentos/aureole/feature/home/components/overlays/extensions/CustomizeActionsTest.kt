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
            CustomizeAddItem.CLOCK,
            CustomizeAddItem.ALL_APPS,
            CustomizeAddItem.CONTAINER,
            CustomizeAddItem.WIDGET_GROUP,
            CustomizeAddItem.QUICK_BUTTON
        ).forEach(actions::addCustomizeItem)

        assertEquals(
            listOf(
                Call(LauncherItemType.CLOCK, null),
                Call(LauncherItemType.ALL_APPS_LIST, null),
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
    fun `when adding favorites, should open the favorites flow and not add a grid item`() {
        var asked = false
        var added = false
        val actions = createDefaultHomeScreenActions().copy(
            onAddFavoritesList = { asked = true },
            onAddGridItem = { _, _, _ -> added = true }
        )

        actions.addCustomizeItem(CustomizeAddItem.FAVORITES)

        assertTrue(asked)
        assertEquals(false, added)
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
