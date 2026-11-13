package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import java.io.File

private const val STASHED_WALLPAPER_NAME = "custom_wallpaper_undo.jpg"
private const val STASHED_SYSTEM_NAME = "system_wallpaper_undo.jpg"

/**
 * Lets "Restore background" be undone: the custom wallpaper file is moved aside instead of deleted, and moved back
 * on undo or deleted when the undo window ends.
 */
class WallpaperUndoStash(
    private val context: Context,
    private val settings: SettingsRepository,
    private val systemWallpaper: () -> SystemWallpaper
) {
    private val stashedFile get() = File(context.filesDir, STASHED_WALLPAPER_NAME)
    private val stashedSystemFile get() = File(context.filesDir, STASHED_SYSTEM_NAME)

    /**
     * Restores the default background: moves the custom wallpaper file aside, turns the custom wallpaper off and
     * gives the system wallpaper back to the device default, after saving the current one for the undo. Returns the
     * path the file had, or null when there was nothing to move (then the system wallpaper is left alone).
     */
    fun restoreDefaultKeepingUndo(): String? {
        val path = settings.customWallpaperPath?.takeIf { it.isNotEmpty() }
        val file = path?.let(::File)?.takeIf { it.exists() }
        val moved = file != null && file.renameTo(stashedFile)
        if (moved) {
            stashedSystemFile.delete()
            if (!systemWallpaper().save(stashedSystemFile)) stashedSystemFile.delete()
            systemWallpaper().clear()
        }
        settings.isCustomWallpaperSet = false
        settings.customWallpaperPath = null
        return if (moved) path else null
    }

    /** Puts the file kept aside back at [path], reapplies the system wallpaper and turns the custom one on again. */
    fun undo(path: String): Boolean {
        val moved = stashedFile.renameTo(File(path))
        if (moved) {
            if (stashedSystemFile.exists()) systemWallpaper().apply(stashedSystemFile)
            stashedSystemFile.delete()
            settings.customWallpaperPath = path
            settings.isCustomWallpaperSet = true
        }
        return moved
    }

    /** Deletes the file kept aside; the undo window is over. */
    fun discard() {
        stashedFile.delete()
        stashedSystemFile.delete()
    }
}
