package dev.mnascimentos.aureole.composable.sheets.model

/**
 * Parameters of `AureoleConfirmDialog`.
 *
 * @property title Question in the header ("Reset layout?").
 * @property message Short explanation under the title.
 * @property confirmLabel Text of the confirm button.
 * @property onConfirm Called when the user confirms; the dialog then closes.
 * @property onDismiss Called when the dialog has closed, whichever way it closed.
 * @property cancelLabel Text of the Cancel button; null uses "Cancel".
 * @property holdMs When set, confirming needs holding the (error colored) button this long, and the button moves
 * to the side opposite to the thumb. For actions that cannot be undone.
 */
data class ConfirmDialogParams(
    val title: String,
    val message: String,
    val confirmLabel: String,
    val onConfirm: () -> Unit,
    val onDismiss: () -> Unit,
    val cancelLabel: String? = null,
    val holdMs: Long? = null
)
