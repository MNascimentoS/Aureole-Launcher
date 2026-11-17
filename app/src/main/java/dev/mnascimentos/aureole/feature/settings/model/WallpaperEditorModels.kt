package dev.mnascimentos.aureole.feature.settings.model

import androidx.annotation.StringRes
import androidx.compose.ui.geometry.Offset
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.designsystem.model.OverlayHostSpec
import kotlin.math.roundToInt

/**
 * Inputs of the photo editor.
 *
 * @property photoPath The original photo; null draws a plain placeholder (previews).
 * @property initial Adjustments the editor opens with.
 * @property onFinish Called once when the editor has closed, with the opening and the final adjustments (equal when
 *   nothing changed); the caller saves and offers the undo.
 * @property onDismissRequest Called right after [onFinish]: the sheet under the editor comes back.
 * @property isLeftHanded Mirrors the sheet for the left thumb.
 * @property host What the settings host decides for its sheets.
 * @property initialTab Tab open on the first frame (previews and screenshot tests).
 */
data class WallpaperEditorParams(
    val photoPath: String?,
    val initial: WallpaperAdjustments,
    val onFinish: (before: WallpaperAdjustments, after: WallpaperAdjustments) -> Unit,
    val onDismissRequest: () -> Unit,
    val isLeftHanded: Boolean = false,
    val host: OverlayHostSpec = OverlayHostSpec(),
    val initialTab: WallpaperEditorTab = WallpaperEditorTab.FRAME
)

/**
 * How the parts of the editor change what it shows.
 *
 * @property onChange Shows a new state right away (a gesture, a slider, a tab).
 * @property onAnimateTo Springs the photo to the given adjustments (double tap, "Center", "Reset").
 */
class WallpaperEditorHandlers(
    val onChange: (WallpaperEditorState) -> Unit,
    val onAnimateTo: (WallpaperAdjustments) -> Unit
)

/**
 * The custom image already applied, as the "Custom image" card shows it.
 *
 * @property path The original photo.
 * @property adjustments Its saved adjustments, for the thumbnail.
 * @property onEdit Opens the editor with the current adjustments.
 * @property onChange Opens the gallery to pick another photo.
 */
data class BackgroundPhotoCard(
    val path: String,
    val adjustments: WallpaperAdjustments,
    val onEdit: () -> Unit,
    val onChange: () -> Unit
)

/**
 * What the settings host hands to the sheet an area opened.
 *
 * @property host Width and backdrop decided by the host.
 * @property onOpenEditor Swaps the background sheet for the photo editor, which comes back to it on close.
 */
data class ChildSheetLinks(
    val host: OverlayHostSpec = OverlayHostSpec(),
    val onOpenEditor: () -> Unit = {}
)

/** Name of [tab] in the sheet. */
@StringRes
fun wallpaperEditorTabLabel(tab: WallpaperEditorTab): Int = when (tab) {
    WallpaperEditorTab.FRAME -> R.string.settings_wallpaper_editor_tab_frame
    WallpaperEditorTab.DIM -> R.string.settings_wallpaper_editor_tab_dim
    WallpaperEditorTab.BLUR -> R.string.settings_wallpaper_editor_tab_blur
}

/**
 * A slider of the editor sheet.
 *
 * @property label What it changes, shown above it and read by TalkBack.
 * @property valueText Its value as shown at the end of the label row ("30%", "12 dp") and as the state for TalkBack.
 * @property value Current value, from 0 to [max].
 * @property max Largest value.
 * @property steps Stops between 0 and [max] (whole percents, whole dp).
 */
data class EditorSliderSpec(
    val label: String,
    val valueText: String,
    val value: Float,
    val max: Float,
    val steps: Int
)

/** Stops of a slider from 0 to [max] in steps of [unit] (the ends are not stops). */
fun editorSliderSteps(max: Float, unit: Float): Int = ((max / unit).roundToInt() - 1).coerceAtLeast(0)

/**
 * What the editor's gestures report.
 *
 * @property onTransform One step of a drag or pinch: zoom factor, centroid and pan, in px.
 * @property onEnd All fingers lifted after a drag or pinch.
 * @property onDoubleTap A double tap at the given point.
 */
class EditorGestureCallbacks(
    val onTransform: (zoomBy: Float, centroid: Offset, pan: Offset) -> Unit,
    val onEnd: () -> Unit,
    val onDoubleTap: (Offset) -> Unit
)

/**
 * What TalkBack says about the photo of the editor and its actions.
 *
 * @property photo Name of the photo area.
 * @property zoom The zoom as text ("2.0x"), read as its state.
 * @property zoomIn Name of the "zoom in" action.
 * @property zoomOut Name of the "zoom out" action.
 * @property moves Name of each "move" action.
 */
data class EditorA11yLabels(
    val photo: String,
    val zoom: String,
    val zoomIn: String,
    val zoomOut: String,
    val moves: List<Pair<EditorMove, String>>
)
