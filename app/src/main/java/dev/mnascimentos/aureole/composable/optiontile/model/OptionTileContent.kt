package dev.mnascimentos.aureole.composable.optiontile.model

import androidx.compose.runtime.Composable

/**
 * What an option tile shows and does: the [sample] on top, the [label] below, and the tap ([onClick] null means
 * disabled).
 */
data class OptionTileContent(
    val sample: @Composable () -> Unit,
    val label: String,
    val selected: Boolean,
    val onClick: (() -> Unit)?
)
