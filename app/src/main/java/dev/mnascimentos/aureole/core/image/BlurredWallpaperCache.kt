package dev.mnascimentos.aureole.core.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

private const val BLUR_FILE_NAME = "wallpaper_blur.jpg"
private const val KEY_FILE_NAME = "wallpaper_blur.key"
private const val DOWNSCALE = 4
private const val JPEG_QUALITY = 85

/**
 * The blurred copy of the wallpaper used on Android 9-11, where the live blur effect does not exist. It lives in
 * `filesDir` and is rebuilt only when the photo or the blur changes (the key file remembers what it was made from).
 */
object BlurredWallpaperCache {

    /** What the copy of [source] blurred by [blurPx] is made from; a different key means the copy is stale. */
    internal fun keyOf(source: File, blurPx: Int): String = "${source.absolutePath}|${source.lastModified()}|" +
        "${source.length()}|$blurPx"

    /** The blurred copy of [source] for [blurPx], made now when missing or stale; null when it cannot be made. */
    fun obtain(context: Context, source: File, blurPx: Int): File? {
        if (!source.exists() || blurPx < 1) return null
        val target = File(context.filesDir, BLUR_FILE_NAME)
        val keyFile = File(context.filesDir, KEY_FILE_NAME)
        val key = keyOf(source, blurPx)
        if (target.exists() && keyFile.exists() && keyFile.readText() == key) return target
        return runCatching { build(source, blurPx, target) }.getOrDefault(false)
            .also { if (it) keyFile.writeText(key) else target.delete() }
            .let { if (it) target else null }
    }

    /** Deletes the copy, because the photo was removed or replaced. */
    fun clear(context: Context) {
        File(context.filesDir, BLUR_FILE_NAME).delete()
        File(context.filesDir, KEY_FILE_NAME).delete()
    }

    private fun build(source: File, blurPx: Int, target: File): Boolean {
        val options = BitmapFactory.Options().apply { inSampleSize = DOWNSCALE }
        val bitmap = BitmapFactory.decodeFile(source.absolutePath, options) ?: return false
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        StackBlur.blur(pixels, bitmap.width, bitmap.height, blurPx / DOWNSCALE)
        val blurred = Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        FileOutputStream(target).use { blurred.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, it) }
        bitmap.recycle()
        blurred.recycle()
        return true
    }
}
