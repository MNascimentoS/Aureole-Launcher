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
import dev.mnascimentos.aureole.composable.sheets.model.isFontChanged
import dev.mnascimentos.aureole.composable.sheets.model.resolveAppliedFont
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.theme.model.AureoleFontCatalog
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val FONT_COLUMNS = 2

/**
 * Font picker with a live preview: tapping a font rewrites the mini-home and the sheet in it at once, and
 * nothing is persisted until the sheet closes. Closing with a different font calls [onFontSelected] and then
 * [onDismissRequest]; closing without a change only calls [onDismissRequest]. [options] adds a footer and the
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
    val shownFont = pending.fontName ?: selectedFontName

    ThemePreviewOverlay(
        params = ThemePreviewOverlayParams(
            title = stringResource(R.string.settings_appearance_pick_font),
            base = base,
            pending = pending,
            isChanged = { pending.isFontChanged(selectedFontName) },
            onCommit = { onFontSelected(resolveAppliedFont(pending.fontName, selectedFontName)) },
            onDismissRequest = {
                pending = pending.discard()
                onDismissRequest()
            },
            onSettingsClick = options.onSettingsClick,
            hasExternalBackdrop = options.hasExternalBackdrop
        ),
        modifier = modifier
    ) {
        FontPickerGrid(
            shownFont = shownFont,
            onSelect = { fontName ->
                options.onPreview()
                pending = pending.copy(fontName = fontName)
            },
            footer = options.footer
        )
    }
}

/**
 * Body of the font picker without its sheet: the label and the grid of fonts, [shownFont] marked. The Appearance
 * sheet of the settings shows it inside itself.
 */
@Composable
internal fun FontPickerGrid(
    shownFont: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    footer: (@Composable () -> Unit)? = null
) {
    val isLeftHanded = LocalLeftHanded.current
    ThemePickerGrid(
        params = ThemePickerGridParams(
            label = stringResource(R.string.settings_appearance_font_section_label),
            columns = FONT_COLUMNS,
            footer = footer
        ),
        modifier = modifier
    ) {
        items(items = AureoleFontCatalog.map { it.key }, key = { it }, contentType = { "font_chip" }) { fontName ->
            FontChip(
                fontName = fontName,
                isSelected = fontName == shownFont,
                onSelect = { onSelect(fontName) },
                isLeftHanded = isLeftHanded
            )
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
