package dev.mnascimentos.aureole.core.data.model

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VisibleAppsTest {

    private fun app(pkg: String) = AppInfo(
        label = pkg,
        packageName = pkg,
        componentName = ComponentName(pkg, "Main"),
        icon = ColorDrawable(Color.GREEN)
    )

    private val all = listOf(app("a"), app("b"), app("c"))

    @Test
    fun `without favorites every app is visible`() {
        assertEquals(all, visibleApps(all, emptyList()))
    }

    @Test
    fun `with one favorite only that app is visible`() {
        assertEquals(listOf(all[1]), visibleApps(all, listOf("b")))
    }

    @Test
    fun `favorites keep their saved order, not the order of all apps`() {
        assertEquals(listOf(all[2], all[0]), visibleApps(all, listOf("c", "a")))
    }

    @Test
    fun `a favorite that is no longer installed is skipped`() {
        assertEquals(listOf(all[0]), visibleApps(all, listOf("gone", "a")))
    }

    @Test
    fun `when only uninstalled favorites remain, nothing is visible`() {
        assertEquals(emptyList<AppInfo>(), visibleApps(all, listOf("gone")))
    }

    @Test
    fun `without apps and without favorites nothing is visible`() {
        assertEquals(emptyList<AppInfo>(), visibleApps(emptyList(), emptyList()))
    }
}
