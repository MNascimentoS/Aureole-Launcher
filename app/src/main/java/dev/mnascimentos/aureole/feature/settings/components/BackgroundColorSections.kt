package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import dev.mnascimentos.aureole.composable.color.AureoleColorSwatch
import dev.mnascimentos.aureole.composable.color.AureoleColorSwatches
import dev.mnascimentos.aureole.composable.color.AureoleHueSlider
import dev.mnascimentos.aureole.composable.color.model.PaletteSwatch
import dev.mnascimentos.aureole.composable.color.model.RecommendedColors
import dev.mnascimentos.aureole.composable.color.model.hueOf
import dev.mnascimentos.aureole.composable.color.model.initialPaletteCategory
import dev.mnascimentos.aureole.composable.color.model.isLightBackground
import dev.mnascimentos.aureole.composable.color.model.paletteSwatches
import dev.mnascimentos.aureole.composable.color.model.themeSwatches
import dev.mnascimentos.aureole.composable.color.model.toneRamp
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.palette.PaletteCategory
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Solid color choices of the background sheet, in order: the tones of the current theme, one tone of each palette
 * (filtered by family), a ramp of seven tones of one hue with a slider to move it, and the vivid presets. A short
 * note shows when the chosen color makes a light theme.
 */
@Composable
internal fun BackgroundColorSections(
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    isLeftHanded: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        SheetSectionLabel(text = stringResource(R.string.settings_background_section_theme))
        ThemeSwatchRow(selected = selected, onSelect = onSelect)
        SheetSectionLabel(text = stringResource(R.string.settings_background_section_palettes))
        PaletteSwatchRow(selected = selected, onSelect = onSelect)
        SheetSectionLabel(text = stringResource(R.string.settings_background_section_tones))
        ToneRamp(selected = selected, onSelect = onSelect)
        SheetSectionLabel(text = stringResource(R.string.settings_background_section_vivid))
        LeftHandMirror(isLeftHanded = isLeftHanded) {
            AureoleColorSwatches(colors = RecommendedColors, selected = selected, onSelect = onSelect)
        }
        if (isLightBackground(selected)) {
            AureoleText(
                text = stringResource(R.string.settings_background_light_warning),
                color = AureoleDS.colors.onSurfaceLow,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ThemeSwatchRow(selected: Int, onSelect: (Int) -> Unit) {
    val swatches = themeSwatches(AureoleDS.colors)
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        swatches.forEach { swatch ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AureoleColorSwatch(
                    colorInt = swatch.color,
                    isSelected = selected == swatch.color,
                    onSelect = { onSelect(swatch.color) }
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

@Composable
private fun PaletteSwatchRow(selected: Int, onSelect: (Int) -> Unit) {
    var category by remember { mutableStateOf(initialPaletteCategory(selected)) }
    val swatches = remember(category) { paletteSwatches(category) }
    val shownName = swatches.firstOrNull { it.color == selected }?.paletteName
    Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
        CategoryChips(selected = category, onSelect = { category = it })
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
private fun CategoryChips(selected: PaletteCategory, onSelect: (PaletteCategory) -> Unit) {
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

@Composable
private fun ToneRamp(selected: Int, onSelect: (Int) -> Unit) {
    var hue by remember { mutableFloatStateOf(hueOf(selected)) }
    Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)) {
            items(items = toneRamp(hue)) { tone ->
                AureoleColorSwatch(colorInt = tone, isSelected = selected == tone, onSelect = { onSelect(tone) })
            }
        }
        AureoleHueSlider(hue = hue, onHueChange = { hue = it })
    }
}

@AureolePreview
@Composable
private fun BackgroundColorSectionsPreview() {
    AureoleLauncherTheme {
        BackgroundColorSections(selected = RecommendedColors[1], onSelect = {})
    }
}
