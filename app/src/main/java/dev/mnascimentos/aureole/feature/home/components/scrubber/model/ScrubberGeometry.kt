package dev.mnascimentos.aureole.feature.home.components.scrubber.model

import androidx.compose.ui.unit.Density
import kotlin.math.exp
import kotlin.math.roundToInt

private const val LETTER_CENTER_FACTOR = 0.5f
private const val GAUSSIAN_DIVISOR = 2f

/**
 * Pixel heights of the scrubber column on a parent [parentHeightPx] tall: [outerPx] is what the column takes from
 * the bottom of the parent ([ScrubberOptions.heightFraction]) and [lettersPx] the part the letters use, above the
 * [ScrubberOptions.bottomPadding]. Rounded the same way the layout rounds them, so the home and the scrubber agree to
 * the pixel.
 */
data class ScrubberBand(val outerPx: Int, val lettersPx: Int)

/** Single source of the column heights; the scrubber layout and [scrubberBoundsFor] both go through it. */
fun scrubberBand(parentHeightPx: Int, heightFraction: Float, bottomPaddingPx: Int): ScrubberBand {
    val parent = parentHeightPx.coerceAtLeast(0)
    val outer = (parent * heightFraction).roundToInt().coerceIn(0, parent)
    val letters = (outer - bottomPaddingPx.coerceAtLeast(0)).coerceAtLeast(0)
    return ScrubberBand(outerPx = outer, lettersPx = letters)
}

/** Vertical span of the letters in the coordinates of the parent the scrubber is bottom-aligned in. */
data class ScrubberBounds(val topPx: Float, val bottomPx: Float) {
    val heightPx: Float get() = (bottomPx - topPx).coerceAtLeast(0f)

    fun contains(yPx: Float): Boolean = heightPx > 0f && yPx in topPx..bottomPx

    /** [yPx] of the parent as a y inside the letters column, clamped to it. */
    fun toLocalY(yPx: Float): Float = (yPx - topPx).coerceIn(0f, heightPx)
}

/** Where the letters of a scrubber bottom-aligned in a parent [parentHeightPx] tall are, for [options]. */
fun scrubberBoundsFor(
    parentHeightPx: Float,
    density: Density,
    options: ScrubberOptions = ScrubberOptions()
): ScrubberBounds {
    val parent = parentHeightPx.roundToInt().coerceAtLeast(0)
    val bottomPaddingPx = with(density) { options.bottomPadding.roundToPx() }
    val band = scrubberBand(parent, options.heightFraction, bottomPaddingPx)
    val top = (parent - band.outerPx).toFloat()
    return ScrubberBounds(topPx = top, bottomPx = top + band.lettersPx)
}

/** Index of the letter under [localYPx] of a column [heightPx] tall holding [alphabetSize] letters, or -1. */
fun letterIndexAt(localYPx: Float, heightPx: Float, alphabetSize: Int): Int {
    if (heightPx <= 0f || alphabetSize <= 0) return -1
    val itemHeight = heightPx / alphabetSize
    return (localYPx.coerceIn(0f, heightPx) / itemHeight).toInt().coerceIn(0, alphabetSize - 1)
}

/** Center of the letter at [index] in a column [heightPx] tall holding [alphabetSize] letters. */
fun letterCenterY(index: Int, heightPx: Float, alphabetSize: Int): Float =
    if (alphabetSize <= 0) 0f else (index + LETTER_CENTER_FACTOR) * heightPx / alphabetSize

/** Gaussian weight (1 at the finger, fading out) of a letter [distancePx] away from the finger. */
fun scrubberBulgeWeight(distancePx: Float, spreadPx: Float): Float =
    if (spreadPx <= 0f) 0f else exp(-(distancePx * distancePx) / (GAUSSIAN_DIVISOR * spreadPx * spreadPx))

/**
 * Strip of the screen edge on the alphabet side where a touch starts the home → drawer scrub: [edgeWidthPx] wide on
 * the right (left when [isLeftHanded]) of a screen [screenWidthPx] wide, and only as tall as the letters ([bounds]).
 */
data class ScrubberEdgeZone(
    val screenWidthPx: Float,
    val edgeWidthPx: Float,
    val bounds: ScrubberBounds,
    val isLeftHanded: Boolean
) {
    fun contains(xPx: Float, yPx: Float): Boolean {
        val isOnEdge = if (isLeftHanded) xPx < edgeWidthPx else xPx > screenWidthPx - edgeWidthPx
        return screenWidthPx > 0f && isOnEdge && bounds.contains(yPx)
    }
}

/**
 * Reports each letter once while a scrub runs: the scrubber's own drag reports from the pointer callbacks, the finger
 * handed over by the home reports from the first layout, and both go through [select], so no letter is sent twice.
 */
class LetterSelectionTracker {
    private var lastChar: Char? = null

    /** True when [char] is a new letter under the finger and must be reported. */
    fun select(char: Char?): Boolean {
        if (char == null || char == lastChar) return false
        lastChar = char
        return true
    }

    /** The scrub ended: the next one reports its first letter even if it is the same. */
    fun reset() {
        lastChar = null
    }
}
