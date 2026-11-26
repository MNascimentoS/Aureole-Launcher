package dev.mnascimentos.aureole.feature.home.extensions

import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.home.HomeViewModel

/**
 * Offers a 4 s "Undo" for an action that already happened; a new offer replaces the previous one. The onboarding
 * is busy teaching the user, so no offer shows until it is over.
 */
fun HomeViewModel.showUndo(message: TextResource, undo: () -> Unit) {
    if (uiState.value.onboarding.stage != OnboardingStage.COMPLETED) return
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
