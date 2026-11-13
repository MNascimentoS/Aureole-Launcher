package dev.mnascimentos.aureole.feature.home.components.containers.model

import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * Saves and closes the container editor exactly once, however many dismiss paths fire (drag, scrim tap, back
 * from the main screen, the overlay exit effect). The close step runs even when the save fails.
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
 * Back (key, gesture or the Back button): a sub screen goes to the main screen, the main screen saves and closes.
 * Dismissing (drag, tap outside) never goes through here: it always saves and closes, on any screen.
 */
fun EditContainerScreenMode.handleBack(
    closer: EditContainerCloser,
    goTo: (EditContainerScreenMode) -> Unit
) {
    val target = backTarget()
    if (target != null) goTo(target) else closer.closeOnce()
}

/** Opens the editor for [panelId]; the counter changes on every open so a stale editor is always recreated. */
fun MainUiState.withEditContainerOpened(panelId: String): MainUiState = copy(
    editingContainerId = panelId,
    isEditContainerDialogVisible = true,
    editContainerOpenCount = editContainerOpenCount + 1
)

/** Closes the editor; a late close for [panelId] must not close an editor reopened for another container. */
fun MainUiState.withEditContainerClosed(panelId: String?): MainUiState =
    if (panelId != null && editingContainerId != panelId) {
        this
    } else {
        copy(isEditContainerDialogVisible = false, editingContainerId = null)
    }
