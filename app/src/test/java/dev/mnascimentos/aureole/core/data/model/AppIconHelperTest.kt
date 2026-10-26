package dev.mnascimentos.aureole.core.data.model

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppIconHelperTest {

    @Test
    fun testIsDimensionCropped() {
        assertTrue(AppIconHelper.isDimensionCropped(50, 100))
        assertFalse(AppIconHelper.isDimensionCropped(90, 100))
        assertFalse(AppIconHelper.isDimensionCropped(0, 100))
    }

    @Test
    fun testFindPixelBounds() {
        val pixels = IntArray(100) { 0 }
        assertNull(AppIconHelper.findPixelBounds(pixels, 10, 10))

        pixels[55] = Color.RED
        val bounds = AppIconHelper.findPixelBounds(pixels, 10, 10)
        assertNotNull(bounds)
        assertEquals(5, bounds!!.first)
        assertEquals(5, bounds.second)
        assertEquals(5, bounds.third)
        assertEquals(5, bounds.fourth)
    }

    @Test
    fun testComputeLuminanceAndNearWhite() {
        val lum = AppIconHelper.computeLuminance(255, 255, 255)
        assertEquals(1.0f, lum, 0.01f)

        assertTrue(AppIconHelper.isNearWhiteBackground(240, 240, 240, 255))
        assertFalse(AppIconHelper.isNearWhiteBackground(100, 100, 100, 255))
    }

    @Test
    fun testDrawableToImageBitmap() {
        val drawable = ColorDrawable(Color.RED)
        val bitmap = AppIconHelper.drawableToImageBitmap(drawable)
        assertNotNull(bitmap)
    }
}
