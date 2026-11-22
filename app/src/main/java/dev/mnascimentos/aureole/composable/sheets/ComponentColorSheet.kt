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
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorEntry
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorOptions
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.sheets.model.PaletteChipLabel
import dev.mnascimentos.aureole.composable.sheets.model.ThemePickerGridParams
import dev.mnascimentos.aureole.composable.sheets.model.colorIn
import dev.mnascimentos.aureole.composable.sheets.model.componentColorEntries
import dev.mnascimentos.aureole.composable.sheets.model.selectedColorEntry
import dev.mnascimentos.aureole.composable.sheets.model.toColorRef
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.model.rememberAureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val COLOR_COLUMNS = 3

/**
 * The palette picker in color mode, opened over the sheet of a component that picks one color: "Theme", the
 * saved color as "Current" when no palette has it, then the palettes, each tile showing only the tone of
 * [ComponentColorOptions.role]. The saved color is always marked. Tapping a tile calls
 * [ComponentColorOptions.onSelect] at once and closes the sheet; [onDismissRequest] runs once it is gone.
 */
@Composable
internal fun ComponentColorSheet(
    options: ComponentColorOptions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val overlayState = rememberAureoleEditOverlayState()
    AureoleEditOverlay(
        modifier = modifier,
        state = overlayState,
        onDismissRequest = onDismissRequest,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = options.title,
                subtitle = options.subtitle,
                isLeftHanded = LocalLeftHanded.current,
                showHeaderAction = false
            ),
            previewContent = options.preview,
            sheetContent = {
                ColorPickerGrid(
                    options = options,
                    onSelect = { entry ->
                        options.onSelect(entry.toColorRef(options.role))
                        overlayState.internalVisible = false
                    }
                )
            }
        )
    )
}

@Composable
private fun ColorPickerGrid(options: ComponentColorOptions, onSelect: (ComponentColorEntry) -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()
    val entries = remember(options.selected, options.role, isDarkTheme) {
        componentColorEntries(options.selected, options.role, isDarkTheme)
    }
    val marked = selectedColorEntry(options.selected, options.role, isDarkTheme).key
    val theme = ThemePalette(name = options.title, colors = AureoleDS.colors)
    ThemePickerGrid(params = ThemePickerGridParams(label = options.title, columns = COLOR_COLUMNS)) {
        items(items = entries, key = { it.key }, contentType = { "color_chip" }) { entry ->
            when (entry) {
                ComponentColorEntry.Theme -> PaletteChip(
                    palette = theme,
                    label = PaletteChipLabel(stringResource(R.string.theme_color_swatch_default), options.themeColor),
                    isSelected = entry.key == marked,
                    onSelect = { onSelect(entry) }
                )
                is ComponentColorEntry.Current -> PaletteChip(
                    palette = theme,
                    label = PaletteChipLabel(
                        stringResource(R.string.theme_color_option_current),
                        entry.ref.resolve(AureoleDS.colors)
                    ),
                    isSelected = entry.key == marked,
                    onSelect = { onSelect(entry) }
                )
                is ComponentColorEntry.Palette -> PaletteChip(
                    palette = entry.palette,
                    label = PaletteChipLabel(entry.name, options.role.colorIn(entry.palette)),
                    isSelected = entry.key == marked,
                    onSelect = { onSelect(entry) }
                )
            }
        }
    }
}

@AureolePreview
@Composable
private fun ComponentColorSheetPreview() {
    var selected by remember { mutableStateOf<ThemeColorRef?>(ThemeColorRef.Fixed(PREVIEW_OLD_COLOR)) }
    AureoleLauncherTheme {
        ComponentColorSheet(
            options = ComponentColorOptions(
                role = ComponentColorRole.TEXT,
                title = "Text color",
                subtitle = "Only for this clock",
                selected = selected,
                themeColor = AureoleDS.colors.onSurfaceHigh,
                onSelect = { selected = it }
            ),
            onDismissRequest = {}
        )
    }
}

private const val PREVIEW_OLD_COLOR = 0xFF90CAF9.toInt()
