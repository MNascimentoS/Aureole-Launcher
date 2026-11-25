package dev.mnascimentos.aureole.feature.home.components.containers.model

import androidx.compose.ui.unit.DpSize
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * Saves and closes the container editor exactly once, however many dismiss paths fire (drag, scrim tap, back
 * from the tabs, the overlay exit effect). The close step runs even when the save fails.
 *
 * @property save Persists the draft.
 * @property close Starts hiding the editor.
 */
class EditContainerCloser(
    private val save: () -> Unit,
    private val close: () -> Unit
) {
    var isClosing: Boolean = false
        private set

    fun closeOnce() {
        if (isClosing) return
        isClosing = true
        try {
            save()
        } finally {
            close()
        }
    }
}

/**
 * Opens the editor for [panelId]; the counter changes on every open so a stale editor is always recreated. [isNew]
 * for a group just added from "Add component" (RF-A4).
 */
fun MainUiState.withEditContainerOpened(panelId: String, isNew: Boolean = false): MainUiState = copy(
    editingContainerId = panelId,
    isEditContainerDialogVisible = true,
    editContainerOpenCount = editContainerOpenCount + 1,
    editingContainerIsNew = isNew
)

/** Closes the editor; a late close for [panelId] must not close an editor reopened for another container. */
fun MainUiState.withEditContainerClosed(panelId: String?): MainUiState =
    if (panelId != null && editingContainerId != panelId) {
        this
    } else {
        copy(isEditContainerDialogVisible = false, editingContainerId = null, editingContainerIsNew = false)
    }

/** [this] state with the group [panelId] measured on the home at [size] (RF-B3). */
fun MainUiState.withContainerSize(panelId: String, size: DpSize): MainUiState =
    copy(containerSizes = containerSizes + (panelId to size))
