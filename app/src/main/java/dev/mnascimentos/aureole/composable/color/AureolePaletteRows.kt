package dev.mnascimentos.aureole.composable.color

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.color.model.PaletteSwatch
import dev.mnascimentos.aureole.composable.color.model.ThemeSwatch
import dev.mnascimentos.aureole.composable.color.model.initialPaletteCategory
import dev.mnascimentos.aureole.composable.color.model.paletteSwatches
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.palette.PaletteCategory
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Labeled round swatches of the current theme (the "From your theme" row of the background sheet and of the
 * theme color picker). Each swatch has a thin outline because theme tones can match the sheet itself.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AureoleThemeSwatchRow(
    swatches: List<ThemeSwatch>,
    isSelected: (ThemeSwatch) -> Boolean,
    onSelect: (ThemeSwatch) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        swatches.forEach { swatch ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AureoleColorSwatch(
                    colorInt = swatch.color,
                    isSelected = isSelected(swatch),
                    onSelect = { onSelect(swatch) },
                    modifier = Modifier.border(AureoleDS.dimens.borderMinimal, AureoleDS.colors.outline, CircleShape)
                )
                AureoleText(
                    text = stringResource(swatch.labelRes),
                    color = AureoleDS.colors.onSurfaceMedium,
                    format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall, maxLines = 1)
                )
            }
        }
    }
}

/**
 * One tone of every palette of a family: the family chips (Cold, Cool, Neutral, Warm), the swatches of the open
 * family and the name of the palette whose swatch is [selected]. The family opens on the one of [selected].
 */
@Composable
fun AureolePaletteSwatchRow(
    selected: Int?,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var category by remember { mutableStateOf(selected?.let(::initialPaletteCategory) ?: PaletteCategory.COLD) }
    val swatches = remember(category) { paletteSwatches(category) }
    val shownName = swatches.firstOrNull { it.color == selected }?.paletteName
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
        PaletteCategoryChips(selected = category, onSelect = { category = it })
        LazyRow(horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)) {
            items(items = swatches, key = PaletteSwatch::paletteName) { swatch ->
                AureoleColorSwatch(
                    colorInt = swatch.color,
                    isSelected = selected == swatch.color,
                    onSelect = { onSelect(swatch.color) }
                )
            }
        }
        AureoleText(
            text = shownName.orEmpty(),
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall, maxLines = 1)
        )
    }
}

private fun PaletteCategory.labelRes(): Int = when (this) {
    PaletteCategory.COLD -> R.string.settings_background_category_cold
    PaletteCategory.COOL -> R.string.settings_background_category_cool
    PaletteCategory.NEUTRAL -> R.string.settings_background_category_neutral
    PaletteCategory.WARM -> R.string.settings_background_category_warm
}

@Composable
private fun PaletteCategoryChips(selected: PaletteCategory, onSelect: (PaletteCategory) -> Unit) {
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium)
    Row(
        modifier = Modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        PaletteCategory.entries.forEach { category ->
            val isSelected = category == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(AureoleDS.dimens.minTouchTarget)
                    .clip(shape)
                    .background(
                        if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.surfaceVariant
                    )
                    .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(category) }),
                contentAlignment = Alignment.Center
            ) {
                AureoleText(
                    text = stringResource(category.labelRes()),
                    color = if (isSelected) AureoleDS.colors.surface else AureoleDS.colors.onSurfaceMedium,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.bodySmall,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                )
            }
        }
    }
}

@AureolePreview
@Composable
private fun AureolePaletteRowsPreview() {
    AureoleLauncherTheme {
        Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium)) {
            AureoleThemeSwatchRow(
                swatches = listOf(ThemeSwatch(R.string.settings_background_swatch_surface, 0xFF1A1C25.toInt())),
                isSelected = { true },
                onSelect = {}
            )
            AureolePaletteSwatchRow(selected = null, onSelect = {})
        }
    }
}
