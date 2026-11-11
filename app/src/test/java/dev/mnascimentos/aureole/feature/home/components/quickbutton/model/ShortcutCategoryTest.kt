package dev.mnascimentos.aureole.feature.home.components.quickbutton.model

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShortcutCategoryTest {

    private val options = listOf(
        ShortcutAction.AllApps,
        ShortcutAction.systemIntent("Camera"),
        ShortcutAction.Settings,
        ShortcutAction.None,
        ShortcutAction.systemIntent("Clock")
    )

    @Test
    fun `category of an action follows its type`() {
        assertEquals(ShortcutCategory.AUREOLE, shortcutCategoryOf(ShortcutAction.AllApps))
        assertEquals(ShortcutCategory.AUREOLE, shortcutCategoryOf(ShortcutAction.None))
        assertEquals(ShortcutCategory.SYSTEM, shortcutCategoryOf(ShortcutAction.systemIntent("Camera")))
        assertEquals(ShortcutCategory.APPS, shortcutCategoryOf(ShortcutAction.openApp("a.b")))
    }

    @Test
    fun `shortcuts in category keep the original order`() {
        assertEquals(
            listOf(ShortcutAction.AllApps, ShortcutAction.Settings, ShortcutAction.None),
            shortcutsInCategory(options, ShortcutCategory.AUREOLE)
        )
        assertEquals(
            listOf(ShortcutAction.systemIntent("Camera"), ShortcutAction.systemIntent("Clock")),
            shortcutsInCategory(options, ShortcutCategory.SYSTEM)
        )
        assertEquals(emptyList<ShortcutAction>(), shortcutsInCategory(options, ShortcutCategory.APPS))
    }

    @Test
    fun `label of an open app shortcut is the app label with a fallback`() {
        val app = AppInfo(
            label = "Browser",
            packageName = "com.example.browser",
            componentName = ComponentName("com.example.browser", "Main"),
            icon = ColorDrawable(Color.BLACK)
        )
        assertEquals("Browser", shortcutLabel(ShortcutAction.openApp("com.example.browser"), listOf(app)))
        assertEquals("Open App", shortcutLabel(ShortcutAction.openApp("missing"), listOf(app)))
        assertEquals("All apps", shortcutLabel(ShortcutAction.AllApps, listOf(app)))
    }

    @Test
    fun `gesture label depends on the edited gesture`() {
        assertEquals(R.string.home_quickbutton_gesture_click, shortcutGestureLabelRes("click"))
        assertEquals(R.string.home_quickbutton_gesture_press, shortcutGestureLabelRes("press"))
        assertEquals(R.string.home_quickbutton_gesture_swipe_up, shortcutGestureLabelRes("swipeUp"))
        assertEquals(R.string.home_quickbutton_gesture_swipe_left, shortcutGestureLabelRes("swipeLeft"))
        assertEquals(R.string.home_quickbutton_gesture_swipe_right, shortcutGestureLabelRes("swipeRight"))
        assertEquals(R.string.home_quickbutton_gesture_generic, shortcutGestureLabelRes(null))
    }
}
