package dev.mnascimentos.aureole.core.image

import kotlin.math.max
import kotlin.math.min

private const val CHANNELS = 4
private const val BITS = 8
private const val MASK = 0xFF

/**
 * Stack-style blur (two box passes per axis, which approximate the stack's triangle kernel) on ARGB pixels in plain
 * Kotlin, used for the blurred wallpaper copy on Android 9-11, which has no RenderEffect. The cost does not depend
 * on the radius and a flat image stays flat.
 */
object StackBlur {

    private fun holds(pixels: IntArray, width: Int, height: Int) =
        width >= 1 && height >= 1 && pixels.size >= width * height

    /** Blurs [pixels] (ARGB_8888, row by row) of [width] x [height] in place with [radius] px and returns it. */
    fun blur(pixels: IntArray, width: Int, height: Int, radius: Int): IntArray {
        if (radius < 1 || !holds(pixels, width, height)) return pixels
        val half = min(max(radius / 2, 1), max(width, height))
        val planes = Array(CHANNELS) { c ->
            val shift = BITS * (CHANNELS - 1 - c)
            IntArray(width * height) { (pixels[it] ushr shift) and MASK }
        }
        planes.forEach { plane -> repeat(2) { blurPlane(plane, width, height, half) } }
        for (i in 0 until width * height) {
            var argb = 0
            for (c in 0 until CHANNELS) argb = (argb shl BITS) or planes[c][i]
            pixels[i] = argb
        }
        return pixels
    }

    private fun blurPlane(plane: IntArray, width: Int, height: Int, radius: Int) {
        val line = IntArray(max(width, height))
        for (y in 0 until height) {
            for (x in 0 until width) line[x] = plane[y * width + x]
            boxBlur(line, width, radius)
            for (x in 0 until width) plane[y * width + x] = line[x]
        }
        for (x in 0 until width) {
            for (y in 0 until height) line[y] = plane[y * width + x]
            boxBlur(line, height, radius)
            for (y in 0 until height) plane[y * width + x] = line[y]
        }
    }

    /** Moving average over a window of 2 * [radius] + 1 samples of the first [length] values, edges repeated. */
    private fun boxBlur(line: IntArray, length: Int, radius: Int) {
        val source = line.copyOf(length)
        val window = 2 * radius + 1
        var sum = source[0] * (radius + 1)
        for (j in 1..radius) sum += source[min(j, length - 1)]
        for (i in 0 until length) {
            line[i] = (sum + window / 2) / window
            sum += source[min(i + radius + 1, length - 1)] - source[max(i - radius, 0)]
        }
    }
}
