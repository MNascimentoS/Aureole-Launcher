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
import dev.mnascimentos.aureole.composable.sheets.model.ThemePickerGridParams
import dev.mnascimentos.aureole.composable.sheets.model.ThemePickerSheetOptions
import dev.mnascimentos.aureole.composable.sheets.model.ThemePreviewOverlayParams
import dev.mnascimentos.aureole.composable.sheets.model.ThemePreviewState
import dev.mnascimentos.aureole.composable.sheets.model.discard
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.theme.AvailableFonts
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState

private const val FONT_COLUMNS = 2

/**
 * Font picker with a live preview: tapping a font rewrites the mini-home and the sheet in it at once, and
 * nothing is persisted until Apply, which calls [onFontSelected] with the chosen font and closes.
 * Cancel, swiping down and tapping outside only call [onDismissRequest]. [options] adds a footer and the
 * Aureole logo in the header.
 */
@Composable
fun PickFontBottomSheet(
    selectedFontName: String,
    onFontSelected: (String) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    options: ThemePickerSheetOptions = ThemePickerSheetOptions()
) {
    var pending by remember { mutableStateOf(ThemePreviewState()) }
    val base = AureoleThemeConfig(
        darkTheme = isSystemInDarkTheme(),
        aureoleColors = AureoleDS.colors,
        aureoleFontName = selectedFontName,
        aureoleIcons = AureoleDS.icons,
        aureoleDimens = AureoleDS.dimens
    )
    val isLeftHanded = LocalHomeUiState.current.isLeftHandedMode
    val shownFont = pending.fontName ?: selectedFontName

    ThemePreviewOverlay(
        params = ThemePreviewOverlayParams(
            title = stringResource(R.string.settings_appearance_pick_font),
            base = base,
            pending = pending,
            onApply = { pending.fontName?.let(onFontSelected) },
            onDismissRequest = {
                pending = pending.discard()
                onDismissRequest()
            },
            onSettingsClick = options.onSettingsClick
        ),
        modifier = modifier
    ) {
        ThemePickerGrid(
            params = ThemePickerGridParams(
                label = stringResource(R.string.settings_appearance_font_section_label),
                columns = FONT_COLUMNS,
                footer = options.footer,
                isLeftHanded = isLeftHanded
            )
        ) {
            items(items = AvailableFonts.keys.toList(), key = { it }, contentType = { "font_chip" }) { fontName ->
                FontChip(
                    fontName = fontName,
                    isSelected = fontName == shownFont,
                    onSelect = { pending = pending.copy(fontName = fontName) },
                    isLeftHanded = isLeftHanded
                )
            }
        }
    }
}

@AureolePreview
@Composable
private fun PickFontBottomSheetPreview() {
    AureoleLauncherTheme {
        PickFontBottomSheet(
            selectedFontName = "Istok Web",
            onFontSelected = {},
            onDismissRequest = {}
        )
    }
}
