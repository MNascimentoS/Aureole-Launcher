package dev.mnascimentos.aureole.core.data.model

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap

internal const val DEFAULT_ICON_SIZE = 96
internal const val BACKGROUND_WHITE_THRESHOLD = 235
internal const val BACKGROUND_ALPHA_THRESHOLD = 180
internal const val MIN_ALPHA_THRESHOLD = 10
internal const val CROP_THRESHOLD_RATIO = 0.85f
internal const val CROP_PADDING_RATIO = 0.08f
internal const val ALPHA_CORNER_THRESHOLD = 200
internal const val SOLID_CORNERS_MIN_COUNT = 3
internal const val LUM_RED_WEIGHT = 0.299f
internal const val LUM_GREEN_WEIGHT = 0.587f
internal const val LUM_BLUE_WEIGHT = 0.114f
internal const val COLOR_MAX_VALUE = 255f
internal const val ALPHA_BASE_RATIO = 0.4f
internal const val ALPHA_LUM_RATIO = 0.6f
internal const val COLOR_MAX_INT = 255
internal const val BG_DIST_THRESHOLD = 45.0

internal data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

internal object AppIconHelper {

    fun isDimensionCropped(cropSize: Int, origSize: Int): Boolean {
        return cropSize > 0 && cropSize < origSize * CROP_THRESHOLD_RATIO
    }

    fun findPixelBounds(pixels: IntArray, width: Int, height: Int): Quad<Int, Int, Int, Int>? {
        var minX = width
        var minY = height
        var maxX = -1
        var maxY = -1

        for (y in 0 until height) {
            val yOffset = y * width
            val rowBounds = findRowBounds(pixels, yOffset, width) ?: continue
            if (rowBounds.first < minX) minX = rowBounds.first
            if (rowBounds.second > maxX) maxX = rowBounds.second
            if (y < minY) minY = y
            if (y > maxY) maxY = y
        }

        return if (maxX >= minX && maxY >= minY) Quad(minX, minY, maxX, maxY) else null
    }

    private fun findRowBounds(pixels: IntArray, yOffset: Int, width: Int): Pair<Int, Int>? {
        var rowMinX = width
        var rowMaxX = -1
        for (x in 0 until width) {
            if (Color.alpha(pixels[yOffset + x]) > MIN_ALPHA_THRESHOLD) {
                if (x < rowMinX) rowMinX = x
                if (x > rowMaxX) rowMaxX = x
            }
        }
        return if (rowMaxX >= rowMinX) Pair(rowMinX, rowMaxX) else null
    }

    fun scaleCroppedBitmap(srcBitmap: Bitmap, minX: Int, minY: Int, cropW: Int, cropH: Int): Bitmap {
        val cropped = Bitmap.createBitmap(srcBitmap, minX, minY, cropW, cropH)
        val scaled = Bitmap.createBitmap(DEFAULT_ICON_SIZE, DEFAULT_ICON_SIZE, Bitmap.Config.ARGB_8888)
        val scaledCanvas = Canvas(scaled)

        val padding = (DEFAULT_ICON_SIZE * CROP_PADDING_RATIO).toInt()
        val drawSize = DEFAULT_ICON_SIZE - (padding * 2)

        val srcRect = Rect(0, 0, cropW, cropH)
        val dstRect = Rect(padding, padding, padding + drawSize, padding + drawSize)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        scaledCanvas.drawBitmap(cropped, srcRect, dstRect, paint)
        return scaled
    }

    fun computeLuminance(red: Int, green: Int, blue: Int): Float {
        return (LUM_RED_WEIGHT * red + LUM_GREEN_WEIGHT * green + LUM_BLUE_WEIGHT * blue) / COLOR_MAX_VALUE
    }

    fun isNearWhiteBackground(red: Int, green: Int, blue: Int, alpha: Int): Boolean {
        val isWhite = red > BACKGROUND_WHITE_THRESHOLD &&
            green > BACKGROUND_WHITE_THRESHOLD &&
            blue > BACKGROUND_WHITE_THRESHOLD
        return isWhite && alpha > BACKGROUND_ALPHA_THRESHOLD
    }

    fun drawableToImageBitmap(drawable: Drawable): ImageBitmap {
        if ((drawable is BitmapDrawable) && (drawable.bitmap != null)) {
            return drawable.bitmap.asImageBitmap()
        }
        val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else DEFAULT_ICON_SIZE
        val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else DEFAULT_ICON_SIZE
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap.asImageBitmap()
    }
}
