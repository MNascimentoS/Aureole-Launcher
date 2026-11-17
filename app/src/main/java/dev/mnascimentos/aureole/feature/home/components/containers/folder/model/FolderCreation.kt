package dev.mnascimentos.aureole.feature.home.components.containers.folder.model

import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * State right after a folder was created and opened: with [pickApps] ("Create and pick apps", pasta-form RF-11)
 * the app picker of the open folder comes up at once; without it, or when no folder is open, nothing changes.
 */
fun MainUiState.afterFolderCreated(pickApps: Boolean): MainUiState =
    if (pickApps && activeFolder != null) copy(isAddAppToFolderDialogVisible = true) else this
