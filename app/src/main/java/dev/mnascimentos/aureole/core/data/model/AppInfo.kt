package dev.mnascimentos.aureole.core.data.model

import android.content.ComponentName
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.createBitmap

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
    val firstLetter: Char
        get() = label.trimStart().firstOrNull()?.uppercaseChar() ?: '#'

    fun getIconBitmap(): ImageBitmap {
        return icon.toImageBitmap()
    }

    fun getThemedIconBitmap(tintColor: Int): ImageBitmap {
        val originalBitmap = when (val drawable = icon) {
            is BitmapDrawable -> drawable.bitmap
            else -> {
                val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else DEFAULT_ICON_SIZE
                val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else DEFAULT_ICON_SIZE
                val bitmap = createBitmap(width, height)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            }
        }

        val width = originalBitmap.width
        val height = originalBitmap.height
        val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        val pixels = IntArray(width * height)
        originalBitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val tr = Color.red(tintColor)
        val tg = Color.green(tintColor)
        val tb = Color.blue(tintColor)

        for (i in pixels.indices) {
            val pixel = pixels[i]
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)
            val alpha = Color.alpha(pixel)

            if (isNearWhiteBackground(red, green, blue, alpha)) {
                pixels[i] = 0
            } else if (alpha > MIN_ALPHA_THRESHOLD) {
                pixels[i] = Color.argb(alpha, tr, tg, tb)
            }
        }

        output.setPixels(pixels, 0, width, 0, 0, width, height)
        return output.asImageBitmap()
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
