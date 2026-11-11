package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.model.ThemePickerGridParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Sheet body of the palette and font pickers: a small uppercase section label above a grid with
 * `params.columns` columns, and the optional footer below it. The label stays on the left in both hands.
 * The grid scrolls when it is taller than the sheet.
 */
@Composable
internal fun ThemePickerGrid(
    params: ThemePickerGridParams,
    modifier: Modifier = Modifier,
    items: LazyGridScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AureoleText(
            text = params.label.uppercase(),
            color = AureoleDS.colors.onSurfaceLow,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AureoleDS.dimens.xSmall),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.smallText,
                fontWeight = FontWeight.Bold,
                letterSpacing = SECTION_LABEL_SPACING_SP.sp,
                textAlign = TextAlign.Start
            )
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(params.columns),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
            modifier = Modifier.weight(1f, fill = false),
            content = items
        )
        params.footer?.invoke()
    }
}

private const val SECTION_LABEL_SPACING_SP = 0.8f
private const val PREVIEW_GRID_COLUMNS = 3
private const val PREVIEW_GRID_HEIGHT_MULTIPLIER = 3

@AureolePreview
@Composable
private fun ThemePickerGridPreview() {
    AureoleLauncherTheme {
        Box(modifier = Modifier.height(AureoleDS.dimens.sizeExtraLarge * PREVIEW_GRID_HEIGHT_MULTIPLIER)) {
            ThemePickerGrid(
                params = ThemePickerGridParams(
                    label = stringResource(R.string.settings_appearance_palette_section_label),
                    columns = PREVIEW_GRID_COLUMNS
                )
            ) {
                items(items = allPalettes, key = { it.name }) { palette ->
                    PaletteChip(
                        palette = palette,
                        displayName = palette.name,
                        isSelected = palette == allPalettes.first(),
                        onSelect = {}
                    )
                }
            }
        }
    }
}
