package dev.mnascimentos.aureole.feature.settings.extensions

import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel

/** Offers a 4 s "Undo" for an action that already happened; a new offer replaces the previous one. */
fun SettingsViewModel.showUndo(message: TextResource, undo: () -> Unit) {
    undoController.show(message, undo)
    syncUndoState()
}

fun SettingsViewModel.onUndo() {
    undoController.undo()
    syncUndoState()
}

/** The offer timed out: the change is final, so the custom image kept aside for the undo can go. */
fun SettingsViewModel.onUndoExpired(id: Long) {
    undoController.expire(id)
    syncUndoState()
    if (undoController.state.value == null) settingsRepository.wallpaperStash.discard()
}
