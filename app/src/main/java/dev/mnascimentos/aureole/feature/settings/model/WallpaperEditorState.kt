package dev.mnascimentos.aureole.feature.settings.model

import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import dev.mnascimentos.aureole.core.data.model.WALLPAPER_MAX_BLUR_DP
import dev.mnascimentos.aureole.core.data.model.WALLPAPER_MAX_DIM
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.data.model.WallpaperGeometry
import kotlin.math.roundToInt

private const val QUARTER_TURN = 90
private const val FULL_TURN = 360
private const val PERCENT = 100f
private const val SAVER_SEPARATOR = "|"
private const val SAVER_PARTS = 3

/** The three tabs of the photo editor sheet. */
enum class WallpaperEditorTab { FRAME, DIM, BLUR }

/**
 * Sizes the framing math needs.
 *
 * @property imageSize The photo as stored (not rotated), in px; unspecified or empty until it is loaded.
 * @property viewSize The editor view, which is the whole screen, in px.
 */
data class WallpaperEditorFrame(val imageSize: Size, val viewSize: Size) {
    /** Both sizes are known, so gestures can move the photo. */
    val isReady: Boolean
        get() = imageSize.hasArea() && viewSize.hasArea()

    /** Middle of the view, where the buttons and TalkBack zoom around. */
    val viewCenter: Offset get() = Offset(viewSize.width / 2f, viewSize.height / 2f)
}

private fun Size.hasArea(): Boolean = this != Size.Unspecified && width > 0f && height > 0f

/**
 * What the photo editor is showing.
 *
 * @property initial Adjustments the editor opened with; closing compares against them.
 * @property current Adjustments shown now (the focus is always clamped, so the photo never leaves a gap).
 * @property tab The tab open in the sheet.
 * @property overshoot How far a pinch went past the maximum zoom (1 = not past it); drawn as a stretch that
 *   springs back when the fingers lift. Not saved.
 */
data class WallpaperEditorState(
    val initial: WallpaperAdjustments,
    val current: WallpaperAdjustments = initial,
    val tab: WallpaperEditorTab = WallpaperEditorTab.FRAME,
    val overshoot: Float = 1f
) {
    /** Something differs from what the editor opened with, so closing saves and offers an undo. */
    val isChanged: Boolean get() = current != initial

    /** Dim as a whole percentage, as the slider shows it. */
    val dimPercent: Int get() = (current.dim * PERCENT).roundToInt()

    /** Blur in whole dp, as the slider shows it. */
    val blurDpValue: Int get() = current.blurDp.roundToInt()
}

/** Same editor showing [adjustments] (an animation step or a gesture result). */
fun WallpaperEditorState.withAdjustments(adjustments: WallpaperAdjustments): WallpaperEditorState =
    copy(current = adjustments)

/** Dim from the slider, rounded to whole percents and kept in 0..70%. */
fun WallpaperEditorState.withDim(dim: Float): WallpaperEditorState =
    copy(current = current.copy(dim = ((dim * PERCENT).roundToInt() / PERCENT).coerceIn(0f, WALLPAPER_MAX_DIM)))

/** Blur from the slider, rounded to whole dp and kept in 0..25 dp. */
fun WallpaperEditorState.withBlur(blurDp: Float): WallpaperEditorState =
    copy(current = current.copy(blurDp = blurDp.roundToInt().toFloat().coerceIn(0f, WALLPAPER_MAX_BLUR_DP)))

/** "Center": the focus back to the middle of the photo, same zoom and rotation. */
fun WallpaperEditorState.centeredAdjustments(): WallpaperAdjustments =
    current.copy(focusX = WallpaperAdjustments.DEFAULT.focusX, focusY = WallpaperAdjustments.DEFAULT.focusY)

/**
 * "Rotate 90°": the photo turns a quarter clockwise around what is in the middle of the screen. A point at (x, y) of
 * the turned photo moves to (1 - y, x), so the same part of the picture stays in view; then the focus is clamped.
 */
fun WallpaperEditorState.rotated(frame: WallpaperEditorFrame): WallpaperEditorState {
    val turned = current.copy(
        rotation = (current.rotation + QUARTER_TURN) % FULL_TURN,
        focusX = 1f - current.focusY,
        focusY = current.focusX
    )
    return copy(current = clampedTo(turned, frame))
}

/** "Reset": everything back to the classic look (cover, centered, 30% dim, no blur). */
fun WallpaperEditorState.resetAdjustments(): WallpaperAdjustments = WallpaperAdjustments.DEFAULT

/** [adjustments] with the focus moved to the nearest place where the photo still covers the view. */
internal fun clampedTo(adjustments: WallpaperAdjustments, frame: WallpaperEditorFrame): WallpaperAdjustments {
    if (!frame.isReady) return adjustments
    val focus = WallpaperGeometry.clampFocus(frame.imageSize, frame.viewSize, adjustments)
    return adjustments.copy(focusX = focus.x, focusY = focus.y)
}

/** Keeps the editor across a rotation: the tab and both adjustments as text. */
val WallpaperEditorStateSaver: Saver<WallpaperEditorState, String> = Saver(
    save = { listOf(it.tab.name, it.initial.toJson(), it.current.toJson()).joinToString(SAVER_SEPARATOR) },
    restore = { text ->
        val parts = text.split(SAVER_SEPARATOR, limit = SAVER_PARTS)
        val initial = WallpaperAdjustments.fromJson(parts.getOrNull(1))
        WallpaperEditorState(
            initial = initial,
            current = parts.getOrNull(2)?.let { WallpaperAdjustments.fromJson(it) } ?: initial,
            tab = WallpaperEditorTab.entries.firstOrNull { it.name == parts.firstOrNull() } ?: WallpaperEditorTab.FRAME
        )
    }
)
