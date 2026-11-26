package dev.mnascimentos.aureole.composable.color

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.mnascimentos.aureole.composable.sheets.ComponentColorSheet
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorOptions

/**
 * The card-grid color picker ([ComponentColorSheet], the "Card color" reference) in a full-screen window of its own,
 * which keeps it out of the scrolling content of the sheet it opens over. Shared by [AureoleColorField] and by the
 * screens that open the picker from something other than a row (the "Solid color" card of the background sheet).
 */
@Composable
fun AureoleColorPickerDialog(options: ComponentColorOptions, onDismissRequest: () -> Unit) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        ComponentColorSheet(options = options, onDismissRequest = onDismissRequest)
    }
}
