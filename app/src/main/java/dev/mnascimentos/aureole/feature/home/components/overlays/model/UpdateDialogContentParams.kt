package dev.mnascimentos.aureole.feature.home.components.overlays.model

/**
 * Parameters for rendering [dev.mnascimentos.aureole.feature.home.components.overlays.InAppUpdateDialog] content.
 *
 * @param title Title of the dialog.
 * @param body Text message body of the dialog.
 * @param confirmLabel Text for the confirm action button.
 * @param dismissLabel Text for the dismiss action button.
 * @param onConfirm Callback executed on confirmation.
 * @param onDismiss Callback executed on dismissal.
 */
data class UpdateDialogContentParams(
    val title: String,
    val body: String,
    val confirmLabel: String,
    val dismissLabel: String,
    val onConfirm: () -> Unit,
    val onDismiss: () -> Unit
)
