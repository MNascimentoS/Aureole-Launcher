package dev.mnascimentos.aureole.composable.undo.model

import dev.mnascimentos.aureole.core.designsystem.model.TextResource

const val UNDO_SNACKBAR_DURATION_MS = 4000L

/** One pending "Undo" offer. A new [id] means a new offer, even when the text is the same. */
data class UndoSnackbarState(
    val id: Long,
    val message: TextResource,
    val durationMs: Long = UNDO_SNACKBAR_DURATION_MS,
)
