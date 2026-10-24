package dev.mnascimentos.aureole.core.data.cache

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import androidx.core.graphics.createBitmap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.File

@RunWith(RobolectricTestRunner::class)
class IconCacheTest {

    private lateinit var context: Context
    private lateinit var iconDir: File

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        iconDir = File(context.cacheDir, "app_icons")
        iconDir.deleteRecursively()
    }

    @Test
    fun `when an icon is requested twice, should serve the second from memory`() {
        val cache = IconCache(context)

        val first = cache.getIcon(PACKAGE, component())
        val second = cache.getIcon(PACKAGE, component())

        assertSame(first, second)
    }

    @Test
    fun `when an unknown app icon is requested, should fall back to a default and save it to disk`() {
        IconCache(context).getIcon(PACKAGE, component())

        assertTrue(iconFile().exists())
    }

    @Test
    fun `when a new cache starts, should load a previously saved icon from disk`() {
        IconCache(context).cacheIcon(PACKAGE, bitmapDrawable(width = 12, height = 8))

        val loaded = IconCache(context).getIcon(PACKAGE, component())

        assertTrue(loaded is BitmapDrawable)
        assertEquals(12, (loaded as BitmapDrawable).bitmap.width)
        assertEquals(8, loaded.bitmap.height)
    }

    @Test
    fun `when caching a drawable without intrinsic size, should save it with the default size`() {
        IconCache(context).cacheIcon(PACKAGE, ColorDrawable(Color.RED))

        val loaded = IconCache(context).getIcon(PACKAGE, component()) as BitmapDrawable

        assertEquals(DEFAULT_ICON_SIZE, loaded.bitmap.width)
        assertEquals(DEFAULT_ICON_SIZE, loaded.bitmap.height)
    }

    @Test
    fun `when an icon is removed, should delete it from memory and disk`() {
        val cache = IconCache(context)
        val cached = bitmapDrawable(width = 4, height = 4)
        cache.cacheIcon(PACKAGE, cached)

        cache.removeIcon(PACKAGE)

        assertFalse(iconFile().exists())
        assertNotSame(cached, cache.getIcon(PACKAGE, component()))
    }

    @Test
    fun `when the saved icon file is corrupt, should replace it with a valid icon`() {
        iconDir.mkdirs()
        iconFile().writeText("not a png")

        IconCache(context).getIcon(PACKAGE, component())

        assertNotNull(BitmapFactory.decodeFile(iconFile().absolutePath))
    }

    private fun component() = ComponentName(PACKAGE, "$PACKAGE.MainActivity")

    private fun iconFile() = File(iconDir, "$PACKAGE.png")

    private fun bitmapDrawable(width: Int, height: Int): BitmapDrawable {
        val bitmap: Bitmap = createBitmap(width, height)
        return BitmapDrawable(context.resources, bitmap)
    }

    private companion object {
        const val PACKAGE = "com.example.notes"
        const val DEFAULT_ICON_SIZE = 96
    }
}
