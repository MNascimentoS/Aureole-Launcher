package dev.mnascimentos.aureole.feature.settings.extensions

import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorUndo
import dev.mnascimentos.aureole.feature.settings.model.wallpaperEditorCloseOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** The settings opened the photo editor that a picked photo asked for. */
fun SettingsViewModel.wallpaperEditorOpened() {
    updateUiState { it.copy(isWallpaperEditorRequested = false) }
}

/**
 * The photo editor closed (RF-06): saves [after] when it differs from [before] and offers the undo that
 * [wallpaperEditorCloseOf] picks (the opening adjustments, or the previous photo when one was just picked).
 */
fun SettingsViewModel.finishWallpaperEditor(before: WallpaperAdjustments, after: WallpaperAdjustments) {
    val swap = pendingPhotoSwap
    pendingPhotoSwap = null
    val close = wallpaperEditorCloseOf(before, after, photoSwapped = swap != null)
    if (close.save) saveWallpaperAdjustments(after) else if (swap != null) applyLockScreen()
    val message = close.message?.let { TextResource.fromResId(it) } ?: return
    when (close.undo) {
        WallpaperEditorUndo.ADJUSTMENTS -> showUndo(message) { saveWallpaperAdjustments(before, isUndo = true) }
        WallpaperEditorUndo.PHOTO -> swap?.let { offerCustomWallpaperUndo(it.previousPath, it.kept, message) }
        WallpaperEditorUndo.NONE -> Unit
    }
}

private fun SettingsViewModel.saveWallpaperAdjustments(adjustments: WallpaperAdjustments, isUndo: Boolean = false) {
    settingsRepository.wallpaperAdjustments = adjustments
    updateUiState { it.copy(wallpaperAdjustments = settingsRepository.wallpaperAdjustments) }
    viewModelScope.launch(Dispatchers.IO) { settingsRepository.prepareBlurredWallpaper() }
    applyLockScreen(undo = isUndo)
}
