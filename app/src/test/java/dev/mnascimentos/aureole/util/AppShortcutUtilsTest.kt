package dev.mnascimentos.aureole.util

import android.content.Context
import android.content.Intent
import android.content.pm.LauncherApps
import android.content.pm.ShortcutInfo
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.ShapeDrawable
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.graphics.createBitmap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
class AppShortcutUtilsTest {

    private lateinit var context: Context
    private lateinit var launcherApps: LauncherApps

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        launcherApps = context.getSystemService(Context.LAUNCHER_APPS_SERVICE) as LauncherApps
    }

    private fun publish(vararg ids: String) {
        ids.forEach { id ->
            val shortcut = ShortcutInfo.Builder(context, id)
                .setShortLabel("Atalho $id")
                .setIntent(Intent(Intent.ACTION_VIEW))
                .build()
            shadowOf(launcherApps).addDynamicShortcut(shortcut)
        }
    }

    @Test
    fun `when the app has no shortcuts, should return none`() {
        assertTrue(AppShortcutUtils.getAppShortcuts(context, context.packageName).isEmpty())
    }

    @Test
    fun `when the app has shortcuts, should list them with their labels`() {
        publish("compose", "inbox")

        val shortcuts = AppShortcutUtils.getAppShortcuts(context, context.packageName)

        assertEquals(setOf("compose", "inbox"), shortcuts.map { it.id }.toSet())
        assertEquals(setOf("Atalho compose", "Atalho inbox"), shortcuts.map { it.label }.toSet())
    }

    @Test
    fun `when the app has more shortcuts than fit, should keep only six`() {
        publish("a", "b", "c", "d", "e", "f", "g", "h")

        assertEquals(6, AppShortcutUtils.getAppShortcuts(context, context.packageName).size)
    }

    @Test
    @Config(sdk = [29])
    fun `when on Android 10, should use the standard shortcut query`() {
        publish("compose")

        assertEquals(listOf("compose"), AppShortcutUtils.getAppShortcuts(context, context.packageName).map { it.id })
    }

    @Test
    fun `when converting drawables to images, should reuse bitmaps and size the rest`() {
        val bitmap = createBitmap(12, 7)
        assertSame(bitmap, BitmapDrawable(context.resources, bitmap).toImageBitmap().asAndroidBitmap())

        val noSize = ColorDrawable(Color.RED).toImageBitmap()
        assertEquals(96, noSize.width)

        val sized = ShapeDrawable().apply {
            intrinsicWidth = 30
            intrinsicHeight = 20
        }.toImageBitmap()
        assertEquals(30, sized.width)
        assertEquals(20, sized.height)
    }
}
