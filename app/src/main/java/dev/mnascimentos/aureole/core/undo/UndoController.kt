package dev.mnascimentos.aureole.core.undo

import dev.mnascimentos.aureole.composable.undo.model.UndoSnackbarState
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Holds at most one pending undo. Pure Kotlin: the timer lives in the UI, which calls [expire].
 * A new [show] replaces the previous offer, which stays applied with no way back.
 */
class UndoController {
    private val _state = MutableStateFlow<UndoSnackbarState?>(null)
    val state: StateFlow<UndoSnackbarState?> = _state.asStateFlow()

    private var pendingUndo: (() -> Unit)? = null
    private var lastId = 0L

    fun show(message: TextResource, undo: () -> Unit) {
        lastId += 1
        pendingUndo = undo
        _state.value = UndoSnackbarState(id = lastId, message = message)
    }

    /** Runs the pending undo, if any, and clears the offer. */
    fun undo() {
        val action = pendingUndo ?: return
        pendingUndo = null
        _state.value = null
        action()
    }

    /** Clears the offer only when [id] is still the current one; stale timers are ignored. */
    fun expire(id: Long) {
        if (_state.value?.id != id) return
        pendingUndo = null
        _state.value = null
    }
}
