package dev.mnascimentos.aureole.core.image

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.alpha
import android.graphics.Color.blue
import android.graphics.Color.red
import androidx.core.graphics.createBitmap
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WallpaperRendererTest {

    /** [width] x [height], [first] on the left (or top when [vertical]) half and [second] on the other. */
    private fun halves(width: Int, height: Int, first: Int, second: Int, vertical: Boolean = false): Bitmap {
        val bitmap = createBitmap(width, height)
        for (x in 0 until width) {
            for (y in 0 until height) {
                val inFirst = if (vertical) y < height / 2 else x < width / 2
                bitmap.setPixel(x, y, if (inFirst) first else second)
            }
        }
        return bitmap
    }

    private fun render(source: Bitmap, adjustments: WallpaperAdjustments, density: Float = 1f): Bitmap =
        WallpaperRenderer.renderLockBitmap(
            source,
            LockRenderParams(adjustments = adjustments, width = PORTRAIT_W, height = PORTRAIT_H, density = density)
        )

    @Test
    fun `solid color gives a flat bitmap of the asked size`() {
        val bitmap = WallpaperRenderer.renderSolid(Color.rgb(10, 20, 30), PORTRAIT_W, PORTRAIT_H)

        assertEquals(PORTRAIT_W, bitmap.width)
        assertEquals(PORTRAIT_H, bitmap.height)
        listOf(0 to 0, PORTRAIT_W - 1 to PORTRAIT_H - 1, 50 to 100).forEach { (x, y) ->
            assertEquals(Color.rgb(10, 20, 30), bitmap.getPixel(x, y))
        }
    }

    @Test
    fun `photo comes out at the size of the screen whatever its own size`() {
        val wide = render(halves(200, 100, Color.RED, Color.BLUE), WallpaperAdjustments(dim = 0f))
        val tall = render(halves(30, 90, Color.RED, Color.BLUE), WallpaperAdjustments(dim = 0f))

        assertEquals(PORTRAIT_W to PORTRAIT_H, wide.width to wide.height)
        assertEquals(PORTRAIT_W to PORTRAIT_H, tall.width to tall.height)
    }

    @Test
    fun `flat photo stays flat without dim and blur`() {
        val bitmap = render(halves(80, 80, Color.GREEN, Color.GREEN), WallpaperAdjustments(dim = 0f))

        assertEquals(Color.GREEN, bitmap.getPixel(5, 5))
        assertEquals(Color.GREEN, bitmap.getPixel(PORTRAIT_W - 5, PORTRAIT_H - 5))
    }

    @Test
    fun `crop follows the visible rect and the focus`() {
        val source = halves(200, 100, Color.RED, Color.BLUE)

        val centered = render(source, WallpaperAdjustments(dim = 0f))
        val left = render(source, WallpaperAdjustments(focusX = 0f, dim = 0f))
        val right = render(source, WallpaperAdjustments(focusX = 1f, dim = 0f))

        assertEquals(Color.RED, centered.getPixel(5, 100))
        assertEquals(Color.BLUE, centered.getPixel(PORTRAIT_W - 5, 100))
        assertEquals(Color.RED, left.getPixel(PORTRAIT_W - 5, 100))
        assertEquals(Color.BLUE, right.getPixel(5, 100))
    }

    @Test
    fun `zoom narrows the crop to the middle of the photo`() {
        val source = halves(200, 100, Color.RED, Color.BLUE)

        val zoomed = render(source, WallpaperAdjustments(zoom = 4f, dim = 0f))

        assertEquals(Color.RED, zoomed.getPixel(PORTRAIT_W / 2 - 5, 100))
        assertEquals(Color.BLUE, zoomed.getPixel(PORTRAIT_W / 2 + 5, 100))
    }

    @Test
    fun `rotation turns the photo clockwise before cropping`() {
        val source = halves(200, 100, Color.RED, Color.BLUE)

        val turned = render(source, WallpaperAdjustments(rotation = 90, dim = 0f))

        assertEquals(Color.RED, turned.getPixel(50, 5))
        assertEquals(Color.BLUE, turned.getPixel(50, PORTRAIT_H - 5))
    }

    @Test
    fun `dim darkens the photo with a black layer`() {
        val source = halves(80, 80, Color.rgb(200, 100, 50), Color.rgb(200, 100, 50))

        val plain = render(source, WallpaperAdjustments(dim = 0f)).getPixel(10, 10)
        val dimmed = render(source, WallpaperAdjustments(dim = 0.5f)).getPixel(10, 10)

        assertTrue(red(dimmed) < red(plain))
        assertEquals(red(plain) / 2f, red(dimmed).toFloat(), 2f)
        assertEquals(blue(plain) / 2f, blue(dimmed).toFloat(), 2f)
        assertEquals(255, alpha(dimmed))
    }

    @Test
    fun `blur mixes the colors across an edge and keeps the size`() {
        val source = halves(100, 200, Color.RED, Color.BLUE, vertical = true)

        val sharp = render(source, WallpaperAdjustments(dim = 0f))
        val blurred = render(source, WallpaperAdjustments(blurDp = 25f, dim = 0f), density = 2f)

        assertEquals(PORTRAIT_W to PORTRAIT_H, blurred.width to blurred.height)
        assertEquals(Color.RED, sharp.getPixel(50, PORTRAIT_H / 2 - 2))
        val nearEdge = blurred.getPixel(50, PORTRAIT_H / 2 - 2)
        assertNotEquals(Color.RED, nearEdge)
        assertTrue(blue(nearEdge) > 0)
    }

    @Test
    fun `blur keeps a flat photo flat`() {
        val source = halves(100, 200, Color.GREEN, Color.GREEN)

        val blurred = render(source, WallpaperAdjustments(blurDp = 12f, dim = 0f), density = 2f)

        assertEquals(Color.GREEN, blurred.getPixel(3, 3))
        assertEquals(Color.GREEN, blurred.getPixel(PORTRAIT_W / 2, PORTRAIT_H / 2))
    }

    @Test
    fun `fill stretches the whole photo and fit leaves bars in the background color`() {
        val source = halves(100, 100, Color.RED, Color.BLUE)
        val flat = WallpaperAdjustments(dim = 0f)

        val filled = WallpaperRenderer.renderLockBitmap(
            source,
            LockRenderParams(flat, PORTRAIT_W, PORTRAIT_H, scaleType = "Fill")
        )
        val fitted = WallpaperRenderer.renderLockBitmap(
            source,
            LockRenderParams(flat, PORTRAIT_W, PORTRAIT_H, scaleType = "Fit", backgroundColor = Color.GREEN)
        )

        assertEquals(Color.RED, filled.getPixel(5, 5))
        assertEquals(Color.BLUE, filled.getPixel(PORTRAIT_W - 5, PORTRAIT_H - 5))
        assertEquals(Color.GREEN, fitted.getPixel(PORTRAIT_W / 2, 3))
        assertEquals(Color.RED, fitted.getPixel(5, PORTRAIT_H / 2))
    }

    private companion object {
        const val PORTRAIT_W = 100
        const val PORTRAIT_H = 200
    }
}
