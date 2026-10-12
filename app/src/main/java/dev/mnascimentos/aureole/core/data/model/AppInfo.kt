package dev.mnascimentos.aureole.core.data.model

import android.content.ComponentName
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.sqrt
import androidx.compose.ui.graphics.Color as ComposeColor

private const val DEFAULT_TINT_COLOR_INT = 0xFFE2E2E2.toInt()

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
        cachedIconBitmap?.let { return it }
        val bitmap = AppIconHelper.drawableToImageBitmap(icon)
        cachedIconBitmap = bitmap
        return bitmap
    }

    @Composable
    fun getDisplayIconBitmap(
        isThemed: Boolean,
        tintColor: ComposeColor = ComposeColor(DEFAULT_TINT_COLOR_INT)
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
        val cached = if (cachedThemedColor == tintColor) cachedThemedBitmap else null
        if (cached != null) return cached

        val (drawableToRender, isOfficialMonochrome) = resolveDrawableToRender(icon)
        val origW = if (drawableToRender.intrinsicWidth > 0) drawableToRender.intrinsicWidth else DEFAULT_ICON_SIZE
        val origH = if (drawableToRender.intrinsicHeight > 0) drawableToRender.intrinsicHeight else DEFAULT_ICON_SIZE

        val rawBitmap = renderRawBitmap(drawableToRender, origW, origH, isOfficialMonochrome, tintColor)
        val croppedBitmap = cropRawBitmapIfNeeded(rawBitmap, origW, origH)

        val imageBitmap = if (isOfficialMonochrome) {
            croppedBitmap.asImageBitmap()
        } else {
            applyTintToBitmap(croppedBitmap, tintColor, icon is AdaptiveIconDrawable).asImageBitmap()
        }

        return cacheThemedBitmap(imageBitmap, tintColor)
    }

    private fun cacheThemedBitmap(bitmap: ImageBitmap, tintColor: Int): ImageBitmap {
        cachedThemedColor = tintColor
        cachedThemedBitmap = bitmap
        return bitmap
    }
}

private fun resolveDrawableToRender(icon: Drawable): Pair<Drawable, Boolean> {
    val isAdaptive = icon is AdaptiveIconDrawable
    var drawableToRender: Drawable? = null

    if (isAdaptive && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        drawableToRender = (icon as AdaptiveIconDrawable).monochrome
    }

    val isOfficialMonochrome = drawableToRender != null
    if (drawableToRender == null) {
        drawableToRender = if (isAdaptive) {
            (icon as AdaptiveIconDrawable).foreground
        } else {
            icon
        }
    }

    return Pair(drawableToRender, isOfficialMonochrome)
}

private fun renderRawBitmap(
    drawable: Drawable,
    width: Int,
    height: Int,
    isOfficialMonochrome: Boolean,
    tintColor: Int
): Bitmap {
    val rawBitmap = createBitmap(width, height)
    val rawCanvas = Canvas(rawBitmap)
    drawable.setBounds(0, 0, width, height)

    if (isOfficialMonochrome) {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            colorFilter = PorterDuffColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
        }
        rawCanvas.drawBitmap(drawable.toBitmap(width, height), 0f, 0f, paint)
    } else {
        drawable.draw(rawCanvas)
    }

    return rawBitmap
}

private fun cropRawBitmapIfNeeded(rawBitmap: Bitmap, origW: Int, origH: Int): Bitmap {
    val rawPixels = IntArray(origW * origH)
    rawBitmap.getPixels(rawPixels, 0, origW, 0, 0, origW, origH)

    val bounds = AppIconHelper.findPixelBounds(rawPixels, origW, origH)
    var resultBitmap = rawBitmap

    if (bounds != null) {
        val cropW = bounds.third - bounds.first + 1
        val cropH = bounds.fourth - bounds.second + 1
        val shouldCrop = AppIconHelper.isDimensionCropped(cropW, origW) ||
            AppIconHelper.isDimensionCropped(cropH, origH)
        if (shouldCrop) {
            resultBitmap = AppIconHelper.scaleCroppedBitmap(rawBitmap, bounds.first, bounds.second, cropW, cropH)
        }
    }

    return resultBitmap
}

private fun applyTintToBitmap(bitmap: Bitmap, tintColor: Int, isAdaptive: Boolean): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

    val corners = intArrayOf(
        pixels[0],
        pixels[width - 1],
        pixels[(height - 1) * width],
        pixels[(height - 1) * width + width - 1]
    )
    val cornersAreSolid = corners.count { Color.alpha(it) > ALPHA_CORNER_THRESHOLD } >= SOLID_CORNERS_MIN_COUNT

    if (isAdaptive && !cornersAreSolid) {
        recolorAdaptivePixels(pixels, tintColor)
    } else {
        recolorStandardPixels(pixels, corners, cornersAreSolid, tintColor)
    }

    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    output.setPixels(pixels, 0, width, 0, 0, width, height)
    return output
}

private fun recolorAdaptivePixels(pixels: IntArray, tintColor: Int) {
    val tr = Color.red(tintColor)
    val tg = Color.green(tintColor)
    val tb = Color.blue(tintColor)

    for (i in pixels.indices) {
        val p = pixels[i]
        val a = Color.alpha(p)
        if (a <= MIN_ALPHA_THRESHOLD) {
            pixels[i] = 0
        } else {
            val lum = AppIconHelper.computeLuminance(Color.red(p), Color.green(p), Color.blue(p))
            val finalAlpha = (a * (ALPHA_BASE_RATIO + ALPHA_LUM_RATIO * lum)).toInt().coerceIn(0, COLOR_MAX_INT)
            pixels[i] = Color.argb(finalAlpha, tr, tg, tb)
        }
    }
}

private fun recolorStandardPixels(pixels: IntArray, corners: IntArray, cornersAreSolid: Boolean, tintColor: Int) {
    val tr = Color.red(tintColor)
    val tg = Color.green(tintColor)
    val tb = Color.blue(tintColor)
    val bgR = corners.fold(0) { acc, c -> acc + Color.red(c) } / corners.size.toDouble()
    val bgG = corners.fold(0) { acc, c -> acc + Color.green(c) } / corners.size.toDouble()
    val bgB = corners.fold(0) { acc, c -> acc + Color.blue(c) } / corners.size.toDouble()

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
        val dist = sqrt(dr * dr + dg * dg + db * db)

        if (cornersAreSolid && (dist < BG_DIST_THRESHOLD || AppIconHelper.isNearWhiteBackground(r, g, b, a))) {
            pixels[i] = 0
        } else {
            val lum = AppIconHelper.computeLuminance(r, g, b)
            val finalAlpha = (a * (ALPHA_BASE_RATIO + ALPHA_LUM_RATIO * lum)).toInt().coerceIn(0, COLOR_MAX_INT)
            pixels[i] = Color.argb(finalAlpha, tr, tg, tb)
        }
    }
}
