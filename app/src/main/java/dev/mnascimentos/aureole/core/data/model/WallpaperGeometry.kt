package dev.mnascimentos.aureole.core.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size

private const val QUARTER_TURN = 90
private const val HALF = 0.5f

/**
 * Pure framing math shared by the background drawing and the editor gestures.
 *
 * The photo is first rotated by `rotation`, then scaled by `minCoverZoom * zoom` (zoom 1 just covers the view) and the
 * view looks at the part of it centered on the focus. All fractions are of the rotated photo.
 */
object WallpaperGeometry {

    /** Size of the photo after being turned by [rotation] degrees. */
    fun rotatedSize(imageSize: Size, rotation: Int): Size =
        if (rotation / QUARTER_TURN % 2 != 0) Size(imageSize.height, imageSize.width) else imageSize

    /** Pixels of view per pixel of photo when the photo just covers [viewSize]; this is zoom 1. */
    fun minCoverZoom(imageSize: Size, viewSize: Size, rotation: Int): Float {
        val rotated = rotatedSize(imageSize, rotation)
        if (rotated.width <= 0f || rotated.height <= 0f) return 1f
        return maxOf(viewSize.width / rotated.width, viewSize.height / rotated.height)
    }

    /** The visible part as fractions (0..1) of the rotated photo, always inside it; the focus is clamped first. */
    fun visibleRect(imageSize: Size, viewSize: Size, adjustments: WallpaperAdjustments): Rect {
        val a = adjustments.coerced()
        val rotated = rotatedSize(imageSize, a.rotation)
        if (rotated.width <= 0f || rotated.height <= 0f) return Rect(0f, 0f, 1f, 1f)
        val scale = minCoverZoom(imageSize, viewSize, a.rotation) * a.zoom
        val fw = (viewSize.width / scale / rotated.width).coerceAtMost(1f)
        val fh = (viewSize.height / scale / rotated.height).coerceAtMost(1f)
        val focus = clampFocus(imageSize, viewSize, a)
        val left = focus.x - fw * HALF
        val top = focus.y - fh * HALF
        return Rect(left, top, left + fw, top + fh)
    }

    /** The focus of [adjustments] moved to the nearest point where the visible part stays inside the photo. */
    fun clampFocus(imageSize: Size, viewSize: Size, adjustments: WallpaperAdjustments): Offset {
        val a = adjustments.coerced()
        val rotated = rotatedSize(imageSize, a.rotation)
        if (rotated.width <= 0f || rotated.height <= 0f) return Offset(HALF, HALF)
        val scale = minCoverZoom(imageSize, viewSize, a.rotation) * a.zoom
        val halfW = (viewSize.width / scale / rotated.width).coerceAtMost(1f) * HALF
        val halfH = (viewSize.height / scale / rotated.height).coerceAtMost(1f) * HALF
        return Offset(a.focusX.coerceIn(halfW, 1f - halfW), a.focusY.coerceIn(halfH, 1f - halfH))
    }
}
