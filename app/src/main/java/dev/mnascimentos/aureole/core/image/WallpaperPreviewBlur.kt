package dev.mnascimentos.aureole.core.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * The quick blur of the photo editor on Android 9-11, which has no live blur effect: the photo is decoded once at a
 * small size and each blur value is applied to a copy of it with [StackBlur]. The home gets its full copy when the
 * editor saves ([BlurredWallpaperCache]).
 */
object WallpaperPreviewBlur {

    /**
     * The photo shrunk for the preview.
     *
     * @property bitmap The small photo.
     * @property sample How many times smaller than the original it is.
     */
    class Source(val bitmap: Bitmap, val sample: Int)

    /** The photo at [path] shrunk so its longest side fits the preview; null when it cannot be read. */
    fun decode(path: String): Source? = runCatching {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return@runCatching null
        val sample = previewSampleSize(bounds.outWidth, bounds.outHeight)
        val options = BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }
        BitmapFactory.decodeFile(path, options)?.let { Source(it, sample) }
    }.getOrNull()

    /** A blurred copy of [source] that looks like the home's copy blurred by [blurPx]. */
    fun blurred(source: Source, blurPx: Int): Bitmap {
        val bitmap = source.bitmap
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        StackBlur.blur(pixels, bitmap.width, bitmap.height, previewBlurRadius(blurPx, source.sample))
        return Bitmap.createBitmap(pixels, bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
    }
}
