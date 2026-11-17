package dev.mnascimentos.aureole.core.image

/** Longest side of the small copy the editor blurs on Android 9-11, where there is no live blur effect. */
const val PREVIEW_BLUR_MAX_SIDE = 540

/**
 * How much the editor shrinks the photo before blurring it on Android 9-11: the smallest power of two that brings
 * its longest side to [maxSide] or less (1 for a small photo).
 */
fun previewSampleSize(width: Int, height: Int, maxSide: Int = PREVIEW_BLUR_MAX_SIDE): Int {
    val limit = maxSide.coerceAtLeast(1)
    var sample = 1
    while (maxOf(width, height) / sample > limit) sample *= 2
    return sample
}

/**
 * Blur radius for the shrunk copy. The home's copy is blurred by the radius in photo px, so the editor divides by
 * its own [sample] to look the same; any blur at all stays at least 1 px.
 */
fun previewBlurRadius(blurPx: Int, sample: Int): Int =
    if (blurPx < 1) 0 else (blurPx / sample.coerceAtLeast(1)).coerceAtLeast(1)
