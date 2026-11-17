package dev.mnascimentos.aureole.feature.settings.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments

/** What the "Undo" offered after the photo editor closes brings back. */
enum class WallpaperEditorUndo {
    /** Nothing changed: no snackbar. */
    NONE,

    /** The adjustments the editor opened with. */
    ADJUSTMENTS,

    /** The photo (and its adjustments, or the solid color) from before the photo picked right before the editor. */
    PHOTO
}

/**
 * A photo just picked in the gallery, whose "Undo" waits for the editor that opens right after it to close.
 *
 * @property previousPath The custom photo it replaced, or null when the background was a solid color.
 * @property kept Whether that photo was kept aside in the wallpaper stash, so it can come back.
 */
data class PendingPhotoSwap(val previousPath: String?, val kept: Boolean)

/** The swap a just-picked photo can still undo: none when it replaced a photo that could not be kept aside. */
fun pendingPhotoSwapOf(previousPath: String?, kept: Boolean): PendingPhotoSwap? =
    PendingPhotoSwap(previousPath, kept).takeIf { previousPath == null || kept }

/**
 * What closing the photo editor does.
 *
 * @property save Whether the new adjustments are saved.
 * @property undo What the snackbar brings back.
 * @property message Text of the snackbar, or null for no snackbar.
 */
data class WallpaperEditorClose(
    val save: Boolean,
    val undo: WallpaperEditorUndo,
    @StringRes val message: Int?
)

/**
 * Closing the editor saves only when something changed. The "Undo" brings back the opening adjustments; when the
 * photo was picked right before the editor (D-06), it brings back the previous photo instead, and it is offered even
 * if the new photo was left as it came ("Background changed"), so the swap can always be undone.
 */
fun wallpaperEditorCloseOf(
    before: WallpaperAdjustments,
    after: WallpaperAdjustments,
    photoSwapped: Boolean
): WallpaperEditorClose {
    val changed = before != after
    return when {
        photoSwapped -> WallpaperEditorClose(
            save = changed,
            undo = WallpaperEditorUndo.PHOTO,
            message = if (changed) R.string.undo_wallpaper_adjusted else R.string.undo_background_changed
        )
        changed -> WallpaperEditorClose(save = true, WallpaperEditorUndo.ADJUSTMENTS, R.string.undo_wallpaper_adjusted)
        else -> WallpaperEditorClose(save = false, undo = WallpaperEditorUndo.NONE, message = null)
    }
}
