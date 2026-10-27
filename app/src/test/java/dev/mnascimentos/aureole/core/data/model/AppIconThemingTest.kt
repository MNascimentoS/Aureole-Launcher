package dev.mnascimentos.aureole.core.data.model

import android.content.ComponentName
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.graphics.createBitmap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AppIconThemingTest {

    private val resources = RuntimeEnvironment.getApplication().resources
    private val tint = Color.rgb(10, 200, 30)

    @Test
    fun `when checking crop ratio, should crop only content clearly smaller than the icon`() {
        assertTrue(AppIconHelper.isDimensionCropped(cropSize = 40, origSize = 100))
        assertFalse(AppIconHelper.isDimensionCropped(cropSize = 90, origSize = 100))
        assertFalse(AppIconHelper.isDimensionCropped(cropSize = 0, origSize = 100))
    }

    @Test
    fun `when finding pixel bounds, should return the box of visible pixels or null`() {
        val width = 4
        val pixels = IntArray(width * 3)
        assertNull(AppIconHelper.findPixelBounds(pixels, width, 3))

        pixels[1 * width + 1] = Color.RED
        pixels[2 * width + 2] = Color.RED
        val bounds = AppIconHelper.findPixelBounds(pixels, width, 3)

        assertEquals(Quad(1, 1, 2, 2), bounds)
    }

    @Test
    fun `when checking for a white background, should need both whiteness and opacity`() {
        assertTrue(AppIconHelper.isNearWhiteBackground(250, 250, 250, 255))
        assertFalse(AppIconHelper.isNearWhiteBackground(250, 250, 250, 100))
        assertFalse(AppIconHelper.isNearWhiteBackground(250, 100, 250, 255))
        assertEquals(1f, AppIconHelper.computeLuminance(255, 255, 255), 0.001f)
    }

    @Test
    fun `when converting drawables, should reuse bitmaps and size others to the default`() {
        val bitmap = solidBitmap(8, Color.BLUE)
        assertSame(bitmap, AppIconHelper.drawableToImageBitmap(BitmapDrawable(resources, bitmap)).asAndroidBitmap())

        val converted = AppIconHelper.drawableToImageBitmap(ColorDrawable(Color.RED))
        assertEquals(DEFAULT_ICON_SIZE, converted.width)
        assertEquals(DEFAULT_ICON_SIZE, converted.height)
    }

    @Test
    fun `when cropping and scaling, should produce a default sized icon`() {
        val scaled = AppIconHelper.scaleCroppedBitmap(solidBitmap(20, Color.RED), 5, 5, 10, 10)

        assertEquals(DEFAULT_ICON_SIZE, scaled.width)
    }

    @Test
    fun `when reading the first letter, should uppercase it or fall back to hash`() {
        assertEquals('M', app(" mail", ColorDrawable()).firstLetter)
        assertEquals('#', app("   ", ColorDrawable()).firstLetter)
    }

    @Test
    fun `when the plain icon bitmap is requested twice, should cache it`() {
        val info = app("Mail", ColorDrawable(Color.RED))

        assertSame(info.getIconBitmap(), info.getIconBitmap())
    }

    @Test
    fun `when theming an icon on a solid background, should drop the background and tint the glyph`() {
        val bitmap = solidBitmap(SIZE, Color.rgb(240, 240, 240)).apply {
            for (x in 40 until 60) for (y in 40 until 60) setPixel(x, y, Color.BLACK)
        }

        val themed = app("Mail", BitmapDrawable(resources, bitmap)).getThemedIconBitmap(tint).asAndroidBitmap()

        assertEquals(0, Color.alpha(themed.getPixel(0, 0)))
        val glyph = themed.getPixel(50, 50)
        assertTrue(Color.alpha(glyph) > 0)
        assertEquals(Color.green(tint), Color.green(glyph))
    }

    @Test
    fun `when the glyph is small on a transparent icon, should crop it to the default size`() {
        val bitmap = createBitmap(SIZE, SIZE).apply {
            for (x in 45 until 55) for (y in 45 until 55) setPixel(x, y, Color.WHITE)
        }

        val themed = app("Dot", BitmapDrawable(resources, bitmap)).getThemedIconBitmap(tint)

        assertEquals(DEFAULT_ICON_SIZE, themed.width)
    }

    @Test
    fun `when the themed icon is requested again, should reuse it only for the same tint`() {
        val info = app("Mail", BitmapDrawable(resources, solidBitmap(SIZE, Color.DKGRAY)))

        val first = info.getThemedIconBitmap(tint)

        assertSame(first, info.getThemedIconBitmap(tint))
        assertNotSame(first, info.getThemedIconBitmap(Color.RED))
    }

    // Robolectric's native canvas ignores a Paint's colour filter and draws scaled bitmaps empty, so
    // the two adaptive paths are checked by the route they take (crop or not), not by pixel colour.
    @Test
    fun `when an adaptive icon has a monochrome layer, should render it whole without cropping`() {
        val icon = AdaptiveIconDrawable(
            ColorDrawable(Color.WHITE),
            BitmapDrawable(resources, solidBitmap(SIZE, Color.BLACK)),
            BitmapDrawable(resources, solidBitmap(SIZE, Color.BLACK))
        )

        val themed = app("Maps", icon).getThemedIconBitmap(tint).asAndroidBitmap()

        assertEquals(SIZE, themed.width)
        assertTrue(Color.alpha(themed.getPixel(SIZE / 2, SIZE / 2)) > 0)
    }

    @Test
    fun `when an adaptive icon has no monochrome layer, should crop its foreground to the default size`() {
        val foreground = createBitmap(SIZE, SIZE).apply {
            for (x in 20 until 80) for (y in 20 until 80) setPixel(x, y, Color.WHITE)
        }
        val icon = AdaptiveIconDrawable(ColorDrawable(Color.BLUE), BitmapDrawable(resources, foreground))

        val themed = app("Notes", icon).getThemedIconBitmap(tint)

        assertEquals(DEFAULT_ICON_SIZE, themed.width)
    }

    private fun app(label: String, icon: Drawable) =
        AppInfo(label, "com.example", ComponentName("com.example", "com.example.Main"), icon)

    private fun solidBitmap(size: Int, color: Int): Bitmap = createBitmap(size, size).apply { eraseColor(color) }

    private companion object {
        const val SIZE = 100
    }
}
