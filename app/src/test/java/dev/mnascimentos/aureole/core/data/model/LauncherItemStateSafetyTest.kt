package dev.mnascimentos.aureole.core.data.model

import com.google.gson.Gson
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.resolveEditingShortcut
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LauncherItemStateSafetyTest {

    @Test
    fun `when an item has an id, should keep it`() {
        assertEquals("clock_1", LauncherItemState(id = "clock_1").safeId)
    }

    @Test
    fun `when an item has a blank id, should generate a unique one`() {
        val blank = LauncherItemState(id = "  ")

        assertTrue(blank.safeId.startsWith("item_"))
        assertNotEquals(blank.safeId, blank.safeId)
    }

    @Test
    fun `when Gson leaves fields null, should fall back to safe defaults`() {
        val parsed = Gson().fromJson(
            """{"col":1,"row":2,"type":null,"scrollOrientation":null,"children":null}""",
            LauncherItemState::class.java
        )

        assertTrue(parsed.safeId.startsWith("item_"))
        assertEquals(LauncherItemType.APPS_LIST, parsed.safeType)
        assertEquals(ScrollOrientation.VERTICAL, parsed.safeScrollOrientation)
        assertEquals(emptyList<LauncherItemState>(), parsed.safeChildren)
    }

    @Test
    fun `when values are set, should expose them`() {
        val child = LauncherItemState(id = "child")
        val item = LauncherItemState(
            id = "scroll",
            type = LauncherItemType.SCROLL_VIEW,
            scrollOrientation = ScrollOrientation.HORIZONTAL,
            children = listOf(child)
        )

        assertEquals(LauncherItemType.SCROLL_VIEW, item.safeType)
        assertEquals(ScrollOrientation.HORIZONTAL, item.safeScrollOrientation)
        assertEquals(listOf(child), item.safeChildren)
    }

    @Test
    fun `when resolving the shortcut being edited, should pick the gesture or none`() {
        val shortcuts = QuickButtonShortcuts(
            click = ShortcutAction.AllApps,
            press = ShortcutAction.Notifications,
            swipeUp = ShortcutAction.systemIntent("Calendar"),
            swipeLeft = ShortcutAction.None,
            swipeRight = ShortcutAction.AllApps
        )
        val model = QuickButtonModel(shortcuts = shortcuts)

        assertEquals(shortcuts.click, resolveEditingShortcut("click", model))
        assertEquals(shortcuts.press, resolveEditingShortcut("press", model))
        assertEquals(shortcuts.swipeUp, resolveEditingShortcut("swipeUp", model))
        assertEquals(shortcuts.swipeLeft, resolveEditingShortcut("swipeLeft", model))
        assertEquals(shortcuts.swipeRight, resolveEditingShortcut("swipeRight", model))
        assertEquals(ShortcutAction.None, resolveEditingShortcut("doubleTap", model))
        assertEquals(ShortcutAction.None, resolveEditingShortcut(null, model))
    }
}
