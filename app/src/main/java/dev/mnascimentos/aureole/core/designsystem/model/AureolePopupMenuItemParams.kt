package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Configuration parameters for [dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItem].
 *
 * @param badge Optional text label displayed on the right side of the item.
 * @param onTrailingClick Optional callback for clicking the trailing icon separately from item click.
 * @param isDestructive When true, applies error tint styling to indicate destructive actions.
 * @param icon Slot for rendering a standard icon provided with a [Modifier].
 * @param customIcon Custom `@Composable` slot for rendering custom leading icon content.
 * @param trailingIcon Slot for rendering a trailing icon provided with a [Modifier].
 */
class AureolePopupMenuItemParams(
    val badge: String? = null,
    val onTrailingClick: (() -> Unit)? = null,
    val isDestructive: Boolean = false,
    val icon: @Composable ((Modifier) -> Unit)? = null,
    val customIcon: (@Composable () -> Unit)? = null,
    val trailingIcon: @Composable ((Modifier) -> Unit)? = null
)
