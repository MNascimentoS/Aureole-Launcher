package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.home.HomeViewModel

/** Offers a 4 s "Undo" for an action that already happened; a new offer replaces the previous one. */
fun HomeViewModel.showUndo(message: TextResource, undo: () -> Unit) {
    undoController.show(message, undo)
    syncUndoState()
}

fun HomeViewModel.onUndo() {
    undoController.undo()
    syncUndoState()
}

fun HomeViewModel.onUndoExpired(id: Long) {
    undoController.expire(id)
    syncUndoState()
}
