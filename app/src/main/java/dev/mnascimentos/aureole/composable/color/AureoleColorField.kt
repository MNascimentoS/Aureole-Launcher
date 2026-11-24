package dev.mnascimentos.aureole.composable.color

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.color.model.ColorFieldParams
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.sheets.ComponentColorSheet
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorEntry
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorOptions
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.sheets.model.selectedColorEntry
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * The single way to choose a color (telas-ajustesfundo RF-06): a row with the color dot, its name and a chevron
 * that opens the card-grid picker ([ComponentColorSheet], the "Card color" reference) over the current sheet.
 */
@Composable
fun AureoleColorField(params: ColorFieldParams, modifier: Modifier = Modifier) {
    var isOpen by rememberSaveable { mutableStateOf(false) }
    val entry = selectedColorEntry(params.selected, params.role, isSystemInDarkTheme())
    val shownColor = params.selected?.resolve(AureoleDS.colors) ?: params.themeColor
    AureoleMenuRow(
        params = MenuRowParams(
            title = params.title,
            subtitle = colorEntryName(entry),
            icon = { m, _ -> ColorFieldDot(color = shownColor, modifier = m) },
            showChevron = true,
            onClick = { isOpen = true }
        ),
        modifier = modifier
    )
    if (isOpen) {
        // The picker is a full-screen overlay: its own window keeps it out of the sheet's scrolling content.
        Dialog(
            onDismissRequest = { isOpen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            ComponentColorSheet(
                options = ComponentColorOptions(
                    role = params.role,
                    title = params.title,
                    subtitle = null,
                    selected = params.selected,
                    themeColor = params.themeColor,
                    onSelect = params.onSelect
                ),
                onDismissRequest = { isOpen = false }
            )
        }
    }
}

@Composable
private fun colorEntryName(entry: ComponentColorEntry): String = when (entry) {
    ComponentColorEntry.Theme -> stringResource(R.string.theme_color_swatch_default)
    is ComponentColorEntry.Current -> stringResource(R.string.theme_color_option_current)
    is ComponentColorEntry.Palette -> entry.name
}

@Composable
private fun ColorFieldDot(color: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier.padding(AureoleDS.dimens.xxSmall)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(color)
                .border(AureoleDS.dimens.borderMinimal, AureoleDS.colors.outline, CircleShape)
        )
    }
}

@AureolePreview
@Composable
private fun AureoleColorFieldPreview() {
    AureoleLauncherTheme {
        AureoleColorField(
            params = ColorFieldParams(
                title = "Folder color",
                selected = null,
                themeColor = AureoleDS.colors.surfaceVariant,
                role = ComponentColorRole.CARD,
                onSelect = {}
            )
        )
    }
}
