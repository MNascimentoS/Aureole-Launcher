package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Body of a picker sheet: the [list] fills the sheet, or shrinks to leave room for a [footer] pinned
 * below it.
 */
@Composable
fun PickerSheetBody(
    footer: (@Composable () -> Unit)?,
    list: @Composable (Modifier) -> Unit
) {
    if (footer == null) {
        list(Modifier.fillMaxSize())
        return
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        list(Modifier.weight(1f, fill = false))
        footer()
    }
}
