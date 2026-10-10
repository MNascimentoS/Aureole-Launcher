package dev.mnascimentos.aureole.core.data.model

import android.content.ComponentName
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Rect
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.compose.ui.graphics.Color as ComposeColor

private const val DEFAULT_ICON_SIZE = 96
private const val BACKGROUND_WHITE_THRESHOLD = 235
private const val BACKGROUND_ALPHA_THRESHOLD = 180
private const val MIN_ALPHA_THRESHOLD = 10

data class AppInfo(
    val label: String,
    val packageName: String,
    val componentName: ComponentName,
    val icon: Drawable,
) {
    @Transient
    private var cachedIconBitmap: ImageBitmap? = null

    @Transient
    private var cachedThemedBitmap: ImageBitmap? = null

    @Transient
    private var cachedThemedColor: Int? = null

    val firstLetter: Char
        get() = label.trimStart().firstOrNull()?.uppercaseChar() ?: '#'

    fun getIconBitmap(): ImageBitmap {
        val existing = cachedIconBitmap
        if (existing != null) return existing
        val bitmap = icon.toImageBitmap()
        cachedIconBitmap = bitmap
        return bitmap
    }

    @Composable
    fun getDisplayIconBitmap(
        isThemed: Boolean,
        tintColor: ComposeColor = ComposeColor(0xFFE2E2E2)
    ): ImageBitmap {
        val argb = tintColor.toArgb()
        return remember(packageName, isThemed, argb) {
            if (isThemed) {
                getThemedIconBitmap(argb)
            } else {
                getIconBitmap()
            }
        }
    }

    fun getThemedIconBitmap(tintColor: Int): ImageBitmap {
        if (cachedThemedColor == tintColor) {
            val existing = cachedThemedBitmap
            if (existing != null) return existing
        }

        val isAdaptive = icon is AdaptiveIconDrawable

        var drawableToRender: Drawable? = null
        if (isAdaptive && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val adaptive = icon
            if (adaptive.monochrome != null) {
                drawableToRender = adaptive.monochrome
            }
        }

        val isOfficialMonochrome = drawableToRender != null

        if (drawableToRender == null) {
            drawableToRender = if (isAdaptive) {
                (icon as AdaptiveIconDrawable).foreground
            } else {
                icon
            }
        }

        val origW = if (drawableToRender.intrinsicWidth > 0) drawableToRender.intrinsicWidth else DEFAULT_ICON_SIZE
        val origH = if (drawableToRender.intrinsicHeight > 0) drawableToRender.intrinsicHeight else DEFAULT_ICON_SIZE

        val rawBitmap = createBitmap(origW, origH)
        val rawCanvas = Canvas(rawBitmap)
        drawableToRender.setBounds(0, 0, origW, origH)

        if (isOfficialMonochrome) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                colorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
            }
            rawCanvas.drawBitmap(drawableToRender.toBitmap(origW, origH), 0f, 0f, paint)
        } else {
            drawableToRender.draw(rawCanvas)
        }

        val rawPixels = IntArray(origW * origH)
        rawBitmap.getPixels(rawPixels, 0, origW, 0, 0, origW, origH)

        var minX = origW
        var minY = origH
        var maxX = -1
        var maxY = -1

        for (y in 0 until origH) {
            for (x in 0 until origW) {
                val a = Color.alpha(rawPixels[y * origW + x])
                if (a > MIN_ALPHA_THRESHOLD) {
                    if (x < minX) minX = x
                    if (x > maxX) maxX = x
                    if (y < minY) minY = y
                    if (y > maxY) maxY = y
                }
            }
        }

        val croppedBitmap: Bitmap = if (maxX >= minX && maxY >= minY) {
            val cropW = maxX - minX + 1
            val cropH = maxY - minY + 1
            if (cropW > 0 && cropH > 0 && (cropW < origW * 0.85f || cropH < origH * 0.85f)) {
                val cropped = Bitmap.createBitmap(rawBitmap, minX, minY, cropW, cropH)
                val targetSize = DEFAULT_ICON_SIZE
                val scaled = Bitmap.createBitmap(targetSize, targetSize, Bitmap.Config.ARGB_8888)
                val scaledCanvas = Canvas(scaled)

                val padding = (targetSize * 0.08f).toInt()
                val drawSize = targetSize - (padding * 2)

                val srcRect = Rect(0, 0, cropW, cropH)
                val dstRect = Rect(padding, padding, padding + drawSize, padding + drawSize)
                val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                scaledCanvas.drawBitmap(cropped, srcRect, dstRect, paint)
                scaled
            } else {
                rawBitmap
            }
        } else {
            rawBitmap
        }

        val width = croppedBitmap.width
        val height = croppedBitmap.height
        val pixels = IntArray(width * height)
        croppedBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        if (isOfficialMonochrome) {
            val themed = croppedBitmap.asImageBitmap()
            cachedThemedColor = tintColor
            cachedThemedBitmap = themed
            return themed
        }

        val corners = intArrayOf(
            pixels[0],
            pixels[width - 1],
            pixels[(height - 1) * width],
            pixels[(height - 1) * width + width - 1]
        )
        val cornersAreSolid = corners.count { Color.alpha(it) > 200 } >= 3

        val tr = Color.red(tintColor)
        val tg = Color.green(tintColor)
        val tb = Color.blue(tintColor)

        if (isAdaptive && !cornersAreSolid) {
            for (i in pixels.indices) {
                val p = pixels[i]
                val a = Color.alpha(p)
                if (a <= MIN_ALPHA_THRESHOLD) {
                    pixels[i] = 0
                } else {
                    val r = Color.red(p)
                    val g = Color.green(p)
                    val b = Color.blue(p)
                    val lum = (0.299f * r + 0.587f * g + 0.114f * b) / 255f
                    val finalAlpha = (a * (0.4f + 0.6f * lum)).toInt().coerceIn(0, 255)
                    pixels[i] = Color.argb(finalAlpha, tr, tg, tb)
                }
            }
        } else {
            val bgR = corners.map { Color.red(it) }.average()
            val bgG = corners.map { Color.green(it) }.average()
            val bgB = corners.map { Color.blue(it) }.average()

            for (i in pixels.indices) {
                val p = pixels[i]
                val a = Color.alpha(p)
                if (a <= MIN_ALPHA_THRESHOLD) {
                    pixels[i] = 0
                    continue
                }

                val r = Color.red(p)
                val g = Color.green(p)
                val b = Color.blue(p)

                val dr = r - bgR
                val dg = g - bgG
                val db = b - bgB
                val dist = Math.sqrt(dr * dr + dg * dg + db * db)

                if (cornersAreSolid && (dist < 45.0 || isNearWhiteBackground(r, g, b, a))) {
                    pixels[i] = 0
                } else {
                    val lum = (0.299f * r + 0.587f * g + 0.114f * b) / 255f
                    val finalAlpha = (a * (0.4f + 0.6f * lum)).toInt().coerceIn(0, 255)
                    pixels[i] = Color.argb(finalAlpha, tr, tg, tb)
                }
            }
        }

        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        output.setPixels(pixels, 0, width, 0, 0, width, height)
        val themed = output.asImageBitmap()
        cachedThemedColor = tintColor
        cachedThemedBitmap = themed
        return themed
    }
}

private fun isNearWhiteBackground(red: Int, green: Int, blue: Int, alpha: Int): Boolean {
    val isWhite = red > BACKGROUND_WHITE_THRESHOLD &&
        green > BACKGROUND_WHITE_THRESHOLD &&
        blue > BACKGROUND_WHITE_THRESHOLD
    return isWhite && alpha > BACKGROUND_ALPHA_THRESHOLD
}

private fun Drawable.toImageBitmap(): ImageBitmap {
    if ((this is BitmapDrawable) && (this.bitmap != null)) {
        return this.bitmap.asImageBitmap()
    }
    val width = if (intrinsicWidth > 0) intrinsicWidth else DEFAULT_ICON_SIZE
    val height = if (intrinsicHeight > 0) intrinsicHeight else DEFAULT_ICON_SIZE
    val bitmap = createBitmap(width, height)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap.asImageBitmap()
}
