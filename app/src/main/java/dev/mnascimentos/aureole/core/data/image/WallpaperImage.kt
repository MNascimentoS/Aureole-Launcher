package dev.mnascimentos.aureole.core.data.image

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface

private const val QUARTER_TURN = 90
private const val HALF_TURN = 180
private const val THREE_QUARTER_TURN = 270

/** The clockwise rotation, in degrees, that puts a photo with the EXIF [orientation] upright (mirroring aside). */
internal fun exifRotationDegrees(orientation: Int): Int = when (orientation) {
    ExifInterface.ORIENTATION_ROTATE_90, ExifInterface.ORIENTATION_TRANSVERSE -> QUARTER_TURN
    ExifInterface.ORIENTATION_ROTATE_180, ExifInterface.ORIENTATION_FLIP_VERTICAL -> HALF_TURN
    ExifInterface.ORIENTATION_ROTATE_270, ExifInterface.ORIENTATION_TRANSPOSE -> THREE_QUARTER_TURN
    else -> 0
}

/** True when the EXIF [orientation] also mirrors the photo, which is flipped horizontally after the rotation. */
internal fun exifIsMirrored(orientation: Int): Boolean = when (orientation) {
    ExifInterface.ORIENTATION_FLIP_HORIZONTAL,
    ExifInterface.ORIENTATION_FLIP_VERTICAL,
    ExifInterface.ORIENTATION_TRANSPOSE,
    ExifInterface.ORIENTATION_TRANSVERSE -> true
    else -> false
}

/** The largest power-of-two `inSampleSize` that keeps both sides of the decoded image at or above [target] pixels. */
internal fun sampleSizeFor(width: Int, height: Int, target: Int): Int {
    var sample = 1
    if (target > 0) {
        while (width / (sample * 2) >= target && height / (sample * 2) >= target) sample *= 2
    }
    return sample
}

/**
 * Decodes the image at [uri], downsampled when it is much bigger than the screen ([target] is the screen's long
 * side) and turned upright by its EXIF orientation, which `BitmapFactory` ignores. Null when it cannot be decoded.
 */
internal fun decodeWallpaperBitmap(resolver: ContentResolver, uri: Uri, target: Int): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
    val options = BitmapFactory.Options().apply {
        inSampleSize = sampleSizeFor(bounds.outWidth, bounds.outHeight, target)
    }
    val decoded = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
    return decoded?.let { applyExifOrientation(it, readExifOrientation(resolver, uri)) }
}

private fun readExifOrientation(resolver: ContentResolver, uri: Uri): Int = runCatching {
    resolver.openInputStream(uri)?.use {
        ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    }
}.getOrNull() ?: ExifInterface.ORIENTATION_NORMAL

private fun applyExifOrientation(bitmap: Bitmap, orientation: Int): Bitmap {
    val degrees = exifRotationDegrees(orientation)
    val mirrored = exifIsMirrored(orientation)
    if (degrees == 0 && !mirrored) return bitmap
    val matrix = Matrix().apply {
        if (degrees != 0) postRotate(degrees.toFloat())
        if (mirrored) postScale(-1f, 1f)
    }
    val oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    if (oriented !== bitmap) bitmap.recycle()
    return oriented
}
