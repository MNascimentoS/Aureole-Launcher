package dev.mnascimentos.aureole.composable.menu

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.menu.model.RadioOption
import dev.mnascimentos.aureole.composable.menu.model.RadioSheetParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Menu sheet with one radio row per option (56 dp each). The marker sits at the thumb side: the right end for
 * right-handed users and the left end for left-handed users. Picking a row calls [onSelect] and closes the sheet
 * with the menu exit animation (300 ms); [onDismiss] runs when the animation ends.
 */
@Composable
fun AureoleRadioSheet(
    params: RadioSheetParams,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AureoleMenuSheet(title = params.title, onDismiss = onDismiss, modifier = modifier) { dismiss ->
        params.options.forEach { option ->
            AureoleRadioRow(
                label = option.label,
                isSelected = option.value == params.selectedValue,
                onClick = {
                    onSelect(option.value)
                    dismiss()
                }
            )
        }
        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
    }
}

@AureolePreview
@Composable
private fun AureoleRadioSheetPreview() {
    AureoleLauncherTheme {
        AureoleRadioSheet(
            params = RadioSheetParams(
                title = "Settings Button Position",
                options = listOf(RadioOption("Left", "Left"), RadioOption("Right", "Right")),
                selectedValue = "Right"
            ),
            onSelect = {},
            onDismiss = {}
        )
    }
}
