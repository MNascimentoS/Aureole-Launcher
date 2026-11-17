package dev.mnascimentos.aureole.core.image

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.geometry.Size
import androidx.core.graphics.createBitmap
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.data.model.WallpaperGeometry
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val BLUR_DOWNSCALE = 4
private const val HALF = 0.5f
private const val HALF_BYTE = 127
private const val ALPHA_MASK = 0xFF000000.toInt()
private const val FULL_TURN = 360
private const val SCALE_FIT = "Fit"
private const val SCALE_FILL = "Fill"
private const val QUARTER_TURN = 90
private const val HALF_TURN = 180
private const val THREE_QUARTER_TURN = 270
private const val BYTE = 8
private const val MASK = 0xFF
private const val CHANNELS = 4

/**
 * What [WallpaperRenderer.renderLockBitmap] draws.
 *
 * @property adjustments Framing, dim and blur saved for the photo.
 * @property width Width of the bitmap, in px (the screen in portrait).
 * @property height Height of the bitmap, in px.
 * @property density Screen density, to turn the blur from dp to px.
 * @property scaleType The saved "Image scale"; it only matters while the photo has no framing, as on the home.
 * @property backgroundColor What shows beside a photo that is scaled to "Fit".
 */
data class LockRenderParams(
    val adjustments: WallpaperAdjustments,
    val width: Int,
    val height: Int,
    val density: Float = 1f,
    val scaleType: String = "Crop",
    val backgroundColor: Int = Color.BLACK
)

/**
 * Draws the final bitmap of the lock screen: the home background without the apps. The photo goes through the same
 * [WallpaperGeometry] as the home (visible part, rotation), is blurred when asked and gets the black dim layer on
 * top. Plain pixel math, with no Canvas and no Android service, so it runs in any thread and in unit tests.
 */
object WallpaperRenderer {

    /** A flat bitmap of [color] for a solid color background. */
    fun renderSolid(color: Int, width: Int, height: Int): Bitmap =
        createBitmap(max(width, 1), max(height, 1)).also { it.eraseColor(color) }

    /** The photo [source] framed, blurred and dimmed as [params] say, at exactly the size they ask for. */
    fun renderLockBitmap(source: Bitmap, params: LockRenderParams): Bitmap {
        val width = max(params.width, 1)
        val height = max(params.height, 1)
        val adjustments = params.adjustments.coerced()
        val blurPx = adjustments.blurDp * params.density
        val blurred = blurPx >= 1f
        val workWidth = if (blurred) max(width / BLUR_DOWNSCALE, 1) else width
        val workHeight = if (blurred) max(height / BLUR_DOWNSCALE, 1) else height

        var photo = Pixels(IntArray(workWidth * workHeight), workWidth, workHeight)
        drawPhoto(Pixels.of(source), params, photo)
        if (blurred) {
            StackBlur.blur(photo.data, workWidth, workHeight, max((blurPx / BLUR_DOWNSCALE).roundToInt(), 1))
            val full = Pixels(IntArray(width * height), width, height)
            full.draw(photo, Area(0f, 0f, workWidth.toFloat(), workHeight.toFloat()), Region(0, 0, width, height))
            photo = full
        }
        photo.dim((adjustments.dim * MASK).roundToInt())
        return Bitmap.createBitmap(photo.data, width, height, Bitmap.Config.ARGB_8888)
    }

    private fun drawPhoto(source: Pixels, params: LockRenderParams, out: Pixels) {
        val adjustments = params.adjustments.coerced()
        val turned = source.rotated(adjustments.rotation)
        val whole = Area(0f, 0f, turned.width.toFloat(), turned.height.toFloat())
        val keepsScale = !adjustments.hasFraming
        when {
            keepsScale && params.scaleType == SCALE_FILL -> out.draw(turned, whole, Region(0, 0, out.width, out.height))
            keepsScale && params.scaleType == SCALE_FIT -> {
                out.data.fill(params.backgroundColor)
                out.draw(turned, whole, fitRegion(turned, out.width, out.height))
            }
            else -> {
                val visible = WallpaperGeometry.visibleRect(
                    Size(source.width.toFloat(), source.height.toFloat()),
                    Size(out.width.toFloat(), out.height.toFloat()),
                    adjustments
                )
                val area = Area(
                    visible.left * turned.width,
                    visible.top * turned.height,
                    visible.width * turned.width,
                    visible.height * turned.height
                )
                out.draw(turned, area, Region(0, 0, out.width, out.height))
            }
        }
    }

    private fun fitRegion(turned: Pixels, width: Int, height: Int): Region {
        val scale = min(width / turned.width.toFloat(), height / turned.height.toFloat())
        val fitWidth = max((turned.width * scale).roundToInt(), 1)
        val fitHeight = max((turned.height * scale).roundToInt(), 1)
        val left = (width - fitWidth) / 2
        val top = (height - fitHeight) / 2
        return Region(left, top, left + fitWidth, top + fitHeight)
    }
}

/** A part of a photo, in its own pixels. */
private class Area(val left: Float, val top: Float, val width: Float, val height: Float)

/** A part of the output, in pixels: [left, right) by [top, bottom). */
private class Region(val left: Int, val top: Int, val right: Int, val bottom: Int)

/** ARGB pixels, row by row. */
private class Pixels(val data: IntArray, val width: Int, val height: Int) {

    /** This photo turned clockwise by [degrees] (0, 90, 180 or 270). */
    fun rotated(degrees: Int): Pixels {
        if (degrees % FULL_TURN == 0) return this
        val swap = degrees == QUARTER_TURN || degrees == THREE_QUARTER_TURN
        val out = Pixels(IntArray(data.size), if (swap) height else width, if (swap) width else height)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val to = when (degrees) {
                    QUARTER_TURN -> (height - 1 - y) + x * out.width
                    HALF_TURN -> (width - 1 - x) + (height - 1 - y) * width
                    else -> y + (width - 1 - x) * out.width
                }
                out.data[to] = data[x + y * width]
            }
        }
        return out
    }

    /** Fills [region] with the [area] of [from], scaled with bilinear sampling. */
    fun draw(from: Pixels, area: Area, region: Region) {
        val regionWidth = region.right - region.left
        val regionHeight = region.bottom - region.top
        for (y in region.top until region.bottom) {
            val v = area.top + (y - region.top + HALF) * area.height / regionHeight - HALF
            for (x in region.left until region.right) {
                val u = area.left + (x - region.left + HALF) * area.width / regionWidth - HALF
                data[x + y * width] = from.sample(u, v)
            }
        }
    }

    private fun sample(u: Float, v: Float): Int {
        val x0 = floor(u).toInt()
        val y0 = floor(v).toInt()
        val fx = u - x0
        val fy = v - y0
        val a = at(x0, y0)
        val b = at(x0 + 1, y0)
        val c = at(x0, y0 + 1)
        val d = at(x0 + 1, y0 + 1)
        var argb = 0
        for (channel in CHANNELS - 1 downTo 0) {
            val shift = channel * BYTE
            val top = lerp((a ushr shift) and MASK, (b ushr shift) and MASK, fx)
            val bottom = lerp((c ushr shift) and MASK, (d ushr shift) and MASK, fx)
            argb = argb or (lerp(top, bottom, fy).roundToInt().coerceIn(0, MASK) shl shift)
        }
        return argb
    }

    private fun at(x: Int, y: Int): Int = data[x.coerceIn(0, width - 1) + y.coerceIn(0, height - 1) * width]

    private fun lerp(from: Int, to: Int, fraction: Float): Float = from + (to - from) * fraction

    private fun lerp(from: Float, to: Float, fraction: Float): Float = from + (to - from) * fraction

    /** Lays a black layer of [alpha] (0 to 255) over every pixel. */
    fun dim(alpha: Int) {
        if (alpha <= 0) return
        val keep = MASK - alpha
        for (i in data.indices) {
            val p = data[i]
            val r = (((p shr 2 * BYTE) and MASK) * keep + HALF_BYTE) / MASK
            val g = (((p shr BYTE) and MASK) * keep + HALF_BYTE) / MASK
            val b = ((p and MASK) * keep + HALF_BYTE) / MASK
            data[i] = (p and ALPHA_MASK) or (r shl 2 * BYTE) or (g shl BYTE) or b
        }
    }

    companion object {
        fun of(bitmap: Bitmap): Pixels {
            val pixels = IntArray(bitmap.width * bitmap.height)
            bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
            return Pixels(pixels, bitmap.width, bitmap.height)
        }
    }
}
