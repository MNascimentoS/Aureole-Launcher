package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private val SIDE_PADDING_DP = 88.dp
private val GRID_FOLDER_HORIZONTAL_PADDING = 24.dp

internal object OpenedFolderOverlayUtils {

    /** Side padding of the opened folder: the list keeps off the other half of the screen, the grid off the edges. */
    fun Modifier.popupPadding(
        isGridFolderEnabled: Boolean,
        isLeftHandedMode: Boolean
    ): Modifier {
        return if (isGridFolderEnabled) {
            this.padding(horizontal = GRID_FOLDER_HORIZONTAL_PADDING)
        } else {
            this.padding(
                start = if (isLeftHandedMode) SIDE_PADDING_DP else 0.dp,
                end = if (!isLeftHandedMode) SIDE_PADDING_DP else 0.dp
            )
        }
    }

    /**
     * Top the list folder asks for, before `folderPopupPlacement` fits it on screen: the folder's top in the window
     * less the status bar, or [fallbackPx] when the folder's position is unknown.
     */
    fun folderAnchorTopPx(topYPx: Float, statusInsetPx: Float, fallbackPx: Float): Float =
        if (topYPx > 0f) topYPx - statusInsetPx else fallbackPx

    fun isGridFolderMode(uiState: MainUiState, folder: AppFolder): Boolean {
        val panelId = folder.panelId
        val activePanel = if (panelId != null) uiState.containers[panelId] else null
        return folder.displayAsGrid || (activePanel?.isGridFolderEnabled == true)
    }
}
