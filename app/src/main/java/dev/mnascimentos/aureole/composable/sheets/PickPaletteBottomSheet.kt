package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.model.ThemePreviewOverlayParams
import dev.mnascimentos.aureole.composable.sheets.model.ThemePreviewState
import dev.mnascimentos.aureole.composable.sheets.model.basePaletteName
import dev.mnascimentos.aureole.composable.sheets.model.discard
import dev.mnascimentos.aureole.composable.sheets.model.findBasePalette
import dev.mnascimentos.aureole.composable.sheets.model.fontNameOf
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import dev.mnascimentos.aureole.core.designsystem.palette.toLight
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val PALETTE_COLUMNS = 3

/**
 * Palette picker with a live preview: tapping a palette repaints the mini-home and the sheet at once, and
 * nothing is persisted until Apply, which calls [onPaletteSelected] with the chosen palette and closes.
 * Cancel, swiping down and tapping outside only call [onDismissRequest].
 */
@Composable
fun PickPaletteBottomSheet(
    selectedPaletteName: String,
    onPaletteSelected: (ThemePalette) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    footer: (@Composable () -> Unit)? = null
) {
    val isDarkTheme = isSystemInDarkTheme()
    var pending by remember { mutableStateOf(ThemePreviewState()) }
    val base = AureoleThemeConfig(
        darkTheme = isDarkTheme,
        aureoleColors = AureoleDS.colors,
        aureoleFontName = fontNameOf(AureoleDS.typography.fontFamily),
        aureoleIcons = AureoleDS.icons,
        aureoleDimens = AureoleDS.dimens
    )
    val shownName = pending.palette?.name?.basePaletteName() ?: selectedPaletteName.basePaletteName()

    ThemePreviewOverlay(
        params = ThemePreviewOverlayParams(
            title = stringResource(R.string.settings_appearance_pick_palette_title),
            base = base,
            pending = pending,
            onApply = { pending.palette?.name?.let(::findBasePalette)?.let(onPaletteSelected) },
            onDismissRequest = {
                pending = pending.discard()
                onDismissRequest()
            }
        ),
        modifier = modifier
    ) {
        ThemePickerGrid(
            label = stringResource(R.string.settings_appearance_palette_section_label),
            columns = PALETTE_COLUMNS,
            footer = footer
        ) {
            items(items = allPalettes, key = { it.name }, contentType = { "palette_chip" }) { basePalette ->
                val active = if (isDarkTheme) basePalette else basePalette.toLight()
                PaletteChip(
                    palette = active,
                    displayName = basePalette.name,
                    isSelected = basePalette.name.equals(shownName, ignoreCase = true),
                    onSelect = { pending = pending.copy(palette = active) }
                )
            }
        }
    }
}

@AureolePreview
@Composable
private fun PickPaletteBottomSheetPreview() {
    AureoleLauncherTheme {
        PickPaletteBottomSheet(
            selectedPaletteName = "Frostbite",
            onPaletteSelected = {},
            onDismissRequest = {}
        )
    }
}
