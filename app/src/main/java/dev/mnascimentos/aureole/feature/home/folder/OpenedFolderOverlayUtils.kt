package dev.mnascimentos.aureole.feature.home.folder

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private const val POPUP_MAX_OFFSET_SUBTRAHEND = 300
private const val POPUP_MIN_OFFSET_DP = 16f
private val SIDE_PADDING_DP = 88.dp

internal object OpenedFolderOverlayUtils {

    fun resolvePopupAlignment(isGridFolderEnabled: Boolean, isLeftHandedMode: Boolean): Alignment {
        return if (isGridFolderEnabled) {
            Alignment.Center
        } else if (isLeftHandedMode) {
            Alignment.TopStart
        } else {
            Alignment.TopEnd
        }
    }

    fun calculatePopupPaddingModifier(
        isGridFolderEnabled: Boolean,
        isLeftHandedMode: Boolean,
        clampedTopDp: Dp
    ): Modifier {
        return if (isGridFolderEnabled) {
            Modifier
        } else {
            Modifier.padding(
                start = if (isLeftHandedMode) SIDE_PADDING_DP else 0.dp,
                end = if (!isLeftHandedMode) SIDE_PADDING_DP else 0.dp,
                top = clampedTopDp
            )
        }
    }

    fun calculateClampedTopDp(
        topYPx: Float,
        screenHeightPx: Float,
        screenDensity: Float,
        topInsetDp: Dp
    ): Dp {
        val rawTopDp = if (topYPx > 0f) {
            (topYPx / screenDensity).dp - topInsetDp
        } else {
            40.dp
        }
        val maxTopDp = if (screenHeightPx > 0f) {
            ((screenHeightPx / screenDensity) - POPUP_MAX_OFFSET_SUBTRAHEND).coerceAtLeast(
                POPUP_MIN_OFFSET_DP
            ).dp
        } else {
            280.dp
        }
        return rawTopDp.coerceIn(8.dp, maxTopDp)
    }

    fun isGridFolderMode(uiState: MainUiState, folder: AppFolder): Boolean {
        val panelId = folder.panelId
        val activePanel = if (panelId != null) uiState.containers[panelId] else null
        return folder.displayAsGrid || (activePanel?.isGridFolderEnabled == true)
    }
}
