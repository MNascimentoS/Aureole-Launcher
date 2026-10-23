package dev.mnascimentos.aureole.core.designsystem.model

/**
 * Configuration options for [dev.mnascimentos.aureole.core.designsystem.components.AureoleDialog].
 *
 * @param title Optional title displayed in the dialog header.
 * @param onLogoClick Optional callback triggered when clicking the header logo.
 */
data class AureoleDialogConfig(
    val title: String? = null,
    val onLogoClick: (() -> Unit)? = null,
)
