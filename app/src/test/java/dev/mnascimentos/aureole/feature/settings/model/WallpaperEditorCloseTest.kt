package dev.mnascimentos.aureole.feature.settings.model

import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class WallpaperEditorCloseTest {

    private val before = WallpaperAdjustments.DEFAULT
    private val after = before.copy(dim = 0.5f)

    @Test
    fun `no change and no photo just picked closes quietly`() {
        val close = wallpaperEditorCloseOf(before, before, photoSwapped = false)
        assertEquals(WallpaperEditorClose(save = false, undo = WallpaperEditorUndo.NONE, message = null), close)
    }

    @Test
    fun `a change saves and offers to bring the adjustments back`() {
        val close = wallpaperEditorCloseOf(before, after, photoSwapped = false)
        assertEquals(true, close.save)
        assertEquals(WallpaperEditorUndo.ADJUSTMENTS, close.undo)
        assertEquals(R.string.undo_wallpaper_adjusted, close.message)
    }

    @Test
    fun `after a photo was picked the undo brings the previous photo back, changed or not`() {
        val changed = wallpaperEditorCloseOf(before, after, photoSwapped = true)
        assertEquals(WallpaperEditorClose(true, WallpaperEditorUndo.PHOTO, R.string.undo_wallpaper_adjusted), changed)

        val untouched = wallpaperEditorCloseOf(before, before, photoSwapped = true)
        val expected = WallpaperEditorClose(false, WallpaperEditorUndo.PHOTO, R.string.undo_background_changed)
        assertEquals(expected, untouched)
    }

    @Test
    fun `a picked photo can be undone unless the one it replaced could not be kept`() {
        assertEquals(PendingPhotoSwap(null, kept = false), pendingPhotoSwapOf(null, kept = false))
        assertEquals(PendingPhotoSwap("/old.jpg", kept = true), pendingPhotoSwapOf("/old.jpg", kept = true))
        assertNull(pendingPhotoSwapOf("/old.jpg", kept = false))
    }
}
