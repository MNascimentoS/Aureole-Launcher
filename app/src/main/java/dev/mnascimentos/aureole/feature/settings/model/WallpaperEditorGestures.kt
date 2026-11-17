package dev.mnascimentos.aureole.feature.settings.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.util.lerp
import dev.mnascimentos.aureole.core.data.model.WALLPAPER_MAX_ZOOM
import dev.mnascimentos.aureole.core.data.model.WALLPAPER_MIN_ZOOM
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.data.model.WallpaperGeometry

/** Zoom a double tap goes to from 1x, and back. */
const val EDITOR_DOUBLE_TAP_ZOOM = 2f

/** Below this zoom a double tap zooms in; from it on, it zooms back out to 1x. */
private const val DOUBLE_TAP_THRESHOLD = 1.5f

/** Most a pinch can go past the maximum zoom before it stops stretching. */
private const val MAX_OVERSHOOT = 1.5f

/** How much of the pinch past the maximum shows as a stretch (the rest is resistance). */
private const val OVERSHOOT_GIVE = 0.3f

/** Zoom step of the TalkBack "zoom in" / "zoom out" actions. */
const val EDITOR_ZOOM_STEP = 1.25f

/** Share of the view one TalkBack "move" action pushes the photo. */
private const val MOVE_STEP = 0.1f

/** Directions of the TalkBack "move" actions: the photo moves that way, like a drag. */
enum class EditorMove { UP, DOWN, LEFT, RIGHT }

/** View px per photo px with [adjustments] (zoom 1 just covers the view). */
private fun scaleOf(adjustments: WallpaperAdjustments, frame: WallpaperEditorFrame): Float =
    WallpaperGeometry.minCoverZoom(frame.imageSize, frame.viewSize, adjustments.rotation) * adjustments.zoom

/**
 * [adjustments] zoomed to [zoom] (kept in 1x..5x) so the part of the photo under [point] stays under it, then
 * clamped so the photo still covers the view.
 */
fun zoomedAt(
    adjustments: WallpaperAdjustments,
    frame: WallpaperEditorFrame,
    zoom: Float,
    point: Offset
): WallpaperAdjustments {
    if (!frame.isReady) return adjustments
    val start = clampedTo(adjustments, frame)
    val target = start.copy(zoom = zoom.coerceIn(WALLPAPER_MIN_ZOOM, WALLPAPER_MAX_ZOOM))
    val rotated = WallpaperGeometry.rotatedSize(frame.imageSize, start.rotation)
    val dx = point.x - frame.viewCenter.x
    val dy = point.y - frame.viewCenter.y
    val before = scaleOf(start, frame)
    val after = scaleOf(target, frame)
    val photoX = start.focusX + dx / (rotated.width * before)
    val photoY = start.focusY + dy / (rotated.height * before)
    return clampedTo(
        target.copy(focusX = photoX - dx / (rotated.width * after), focusY = photoY - dy / (rotated.height * after)),
        frame
    )
}

/** The photo follows a drag of [pan] px; it stops at the edges, so no gap ever shows. */
fun WallpaperEditorState.dragged(frame: WallpaperEditorFrame, pan: Offset): WallpaperEditorState {
    if (!frame.isReady || pan == Offset.Zero) return this
    val rotated = WallpaperGeometry.rotatedSize(frame.imageSize, current.rotation)
    val scale = scaleOf(current, frame)
    val moved = current.copy(
        focusX = current.focusX - pan.x / (rotated.width * scale),
        focusY = current.focusY - pan.y / (rotated.height * scale)
    )
    return copy(current = clampedTo(moved, frame))
}

/**
 * One step of a pinch: zoom by [zoomBy] around [centroid], then move by [pan]. Zooming out stops at 1x (the photo
 * still covers the screen); zooming in past 5x only grows [WallpaperEditorState.overshoot], up to a limit.
 */
fun WallpaperEditorState.pinched(
    frame: WallpaperEditorFrame,
    zoomBy: Float,
    centroid: Offset,
    pan: Offset = Offset.Zero
): WallpaperEditorState {
    if (!frame.isReady) return this
    val proposed = current.zoom * overshoot * zoomBy
    val zoomed = copy(
        current = zoomedAt(current, frame, proposed, centroid),
        overshoot = (proposed / WALLPAPER_MAX_ZOOM).coerceIn(1f, MAX_OVERSHOOT)
    )
    return zoomed.dragged(frame, pan)
}

/** The fingers lifted: the stretch past the maximum zoom goes away (the view springs it back). */
fun WallpaperEditorState.settled(): WallpaperEditorState = copy(overshoot = 1f)

/** Extra scale drawn for [overshoot]: only part of the pinch past the maximum shows, as resistance. */
fun overshootScale(overshoot: Float): Float = 1f + (overshoot.coerceIn(1f, MAX_OVERSHOOT) - 1f) * OVERSHOOT_GIVE

/** Where a double tap at [point] goes: from below 1.5x to 2x around the point, otherwise back to 1x. */
fun WallpaperEditorState.doubleTapTarget(frame: WallpaperEditorFrame, point: Offset): WallpaperAdjustments {
    val zoom = if (current.zoom < DOUBLE_TAP_THRESHOLD) EDITOR_DOUBLE_TAP_ZOOM else WALLPAPER_MIN_ZOOM
    return zoomedAt(current, frame, zoom, point)
}

/** TalkBack zoom: [factor] around the middle of the screen. */
fun WallpaperEditorState.zoomedBy(frame: WallpaperEditorFrame, factor: Float): WallpaperEditorState =
    copy(current = zoomedAt(current, frame, current.zoom * factor, frame.viewCenter))

/** TalkBack move: the photo moves a tenth of the screen toward [direction], like a short drag. */
fun WallpaperEditorState.moved(frame: WallpaperEditorFrame, direction: EditorMove): WallpaperEditorState {
    val stepX = frame.viewSize.width * MOVE_STEP
    val stepY = frame.viewSize.height * MOVE_STEP
    val pan = when (direction) {
        EditorMove.UP -> Offset(0f, -stepY)
        EditorMove.DOWN -> Offset(0f, stepY)
        EditorMove.LEFT -> Offset(-stepX, 0f)
        EditorMove.RIGHT -> Offset(stepX, 0f)
    }
    return dragged(frame, pan)
}

/**
 * One frame of the spring between [from] and [to] ([fraction] 0..1, a little past 1 while it settles). The rotation
 * jumps to [to]'s at once; the drawing clamps the focus of every frame, so no gap shows on the way.
 */
fun lerpAdjustments(from: WallpaperAdjustments, to: WallpaperAdjustments, fraction: Float): WallpaperAdjustments =
    WallpaperAdjustments(
        zoom = lerp(from.zoom, to.zoom, fraction),
        focusX = lerp(from.focusX, to.focusX, fraction),
        focusY = lerp(from.focusY, to.focusY, fraction),
        rotation = to.rotation,
        dim = lerp(from.dim, to.dim, fraction),
        blurDp = lerp(from.blurDp, to.blurDp, fraction)
    )
