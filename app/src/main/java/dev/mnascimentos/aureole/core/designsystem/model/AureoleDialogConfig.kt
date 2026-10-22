package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Configuration options for [dev.mnascimentos.aureole.core.designsystem.components.AureoleDialog].
 *
 * @param title Optional title displayed in the dialog header.
 * @param icon Optional leading icon composable for the header title.
 * @param onLogoClick Optional callback triggered when clicking the header logo.
 */
data class AureoleDialogConfig(
    val title: String? = null,
    val icon: (@Composable (Modifier) -> Unit)? = null,
    val onLogoClick: (() -> Unit)? = null,
)
