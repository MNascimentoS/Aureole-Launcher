package dev.mnascimentos.aureole.core.data.cache

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import androidx.core.graphics.createBitmap
import dev.mnascimentos.aureole.core.data.model.FullBleedIconDrawable
import dev.mnascimentos.aureole.core.data.model.IconCacheNaming
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
        iconDir = File(context.cacheDir, IconCacheNaming.DIR_NAME)
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

        assertTrue(iconFile().exists() || File(iconDir, "$PACKAGE.fb.png").exists())
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

    @Test
    fun `when an adaptive icon is cached, should save it full bleed and read it back as full bleed`() {
        val adaptive = AdaptiveIconDrawable(ColorDrawable(Color.BLUE), ColorDrawable(Color.TRANSPARENT))
        IconCache(context).cacheIcon(PACKAGE, adaptive)

        assertTrue(File(iconDir, "$PACKAGE.fb.png").exists())
        assertFalse(iconFile().exists())
        assertTrue(IconCache(context).getIcon(PACKAGE, component()) is FullBleedIconDrawable)
    }

    @Test
    fun `when a full bleed icon is cached again, should keep it full bleed`() {
        val resources = context.resources
        IconCache(context).cacheIcon(PACKAGE, FullBleedIconDrawable(resources, createBitmap(6, 6)))

        assertTrue(File(iconDir, "$PACKAGE.fb.png").exists())
        assertFalse(iconFile().exists())
    }

    @Test
    fun `when the app is updated, should discard the old cache folder with masked icons`() {
        val legacyDir = File(context.cacheDir, IconCacheNaming.LEGACY_DIR_NAME).apply { mkdirs() }
        File(legacyDir, "$PACKAGE.png").writeText("masked")

        IconCache(context)

        assertFalse(legacyDir.exists())
    }

    @Test
    fun `when an adaptive icon is removed, should delete the full bleed file too`() {
        val cache = IconCache(context)
        cache.cacheIcon(PACKAGE, AdaptiveIconDrawable(ColorDrawable(Color.BLUE), ColorDrawable(Color.RED)))

        cache.removeIcon(PACKAGE)

        assertFalse(File(iconDir, "$PACKAGE.fb.png").exists())
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
