package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AvailableFonts
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val FONT_SAMPLE_WIDTH_DP = 30
private const val UNSELECTED_FONT_ALPHA = 0.35f
private const val SELECTED_FONT_ALPHA = 0.5f

/**
 * Palette chip: the palette's own background, a strip of its colors and its name written in its own
 * highlight color, outlined when [isSelected]. The colors come from [palette], not from the current theme.
 */
@Composable
internal fun PaletteChip(
    palette: ThemePalette,
    displayName: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusSmall)
    val description = stringResource(R.string.settings_appearance_palette_chip_description, displayName)
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = AureoleDS.dimens.sizeLarge)
            .clip(shape)
            .background(palette.colors.background)
            .border(
                AureoleDS.dimens.borderMax,
                if (isSelected) AureoleDS.colors.onSurfaceHigh else Color.Transparent,
                shape
            )
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onSelect)
            .semantics { contentDescription = description }
            .padding(AureoleDS.dimens.xSmall),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        PaletteColorStrip(palette)
        AureoleText(
            text = displayName,
            color = palette.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.smallTitle,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        )
    }
}

@Composable
private fun PaletteColorStrip(palette: ThemePalette) {
    val stripShape = RoundedCornerShape(AureoleDS.dimens.radiusMinimal)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(AureoleDS.dimens.iconMediumSmall)
            .clip(stripShape)
    ) {
        listOf(
            palette.colors.surface,
            palette.colors.surfaceVariant,
            palette.colors.onSurfaceLow,
            palette.colors.onSurfaceMedium,
            palette.colors.onSurfaceHigh
        ).forEach { color ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(color)
            )
        }
    }
}

/**
 * Font chip: "Aa" and the font name, both written in the font itself, outlined when [isSelected].
 * With [isLeftHanded] the chip is mirrored: the name on the left and the sample on the right.
 */
@Composable
internal fun FontChip(
    fontName: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    isLeftHanded: Boolean = false
) {
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusSmall)
    val family = AvailableFonts[fontName]
    val description = stringResource(R.string.settings_appearance_font_chip_description, fontName)
    val background = AureoleDS.colors.surfaceVariant.copy(
        alpha = if (isSelected) SELECTED_FONT_ALPHA else UNSELECTED_FONT_ALPHA
    )
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = AureoleDS.dimens.itemHeightExtraLarge)
            .clip(shape)
            .background(background)
            .border(
                AureoleDS.dimens.borderMax,
                if (isSelected) AureoleDS.colors.onSurfaceHigh else Color.Transparent,
                shape
            )
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onSelect)
            .semantics { contentDescription = description }
            .padding(horizontal = AureoleDS.dimens.small),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.smallCompact),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLeftHanded) {
            FontChipName(fontName, family, isLeftHanded, Modifier.weight(1f))
            FontChipSample(family)
        } else {
            FontChipSample(family)
            FontChipName(fontName, family, isLeftHanded, Modifier.weight(1f))
        }
    }
}

@Composable
private fun FontChipSample(family: FontFamily?) {
    AureoleText(
        text = stringResource(R.string.settings_appearance_font_chip_sample),
        color = AureoleDS.colors.onSurfaceHigh,
        modifier = Modifier.width(FONT_SAMPLE_WIDTH_DP.dp),
        format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontFamily = family)
    )
}

/** Font name in the font itself; right-aligned in the left-handed mode, where the chip is mirrored. */
@Composable
private fun FontChipName(fontName: String, family: FontFamily?, isLeftHanded: Boolean, modifier: Modifier = Modifier) {
    AureoleText(
        text = fontName,
        color = AureoleDS.colors.onSurfaceMedium,
        modifier = modifier,
        format = AureoleTextFormatParams(
            style = AureoleDS.typography.smallText,
            fontFamily = family,
            maxLines = 1,
            textAlign = if (isLeftHanded) TextAlign.End else TextAlign.Start
        )
    )
}

@AureolePreview
@Composable
private fun ThemePickerChipsPreview() {
    AureoleLauncherTheme {
        Column(
            modifier = Modifier.padding(AureoleDS.dimens.medium),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            val palette = ThemePalette(name = "Preview", colors = AureoleDS.colors)
            PaletteChip(palette = palette, displayName = palette.name, isSelected = true, onSelect = {})
            FontChip(fontName = "Istok Web", isSelected = false, onSelect = {})
            FontChip(fontName = "Istok Web", isSelected = true, onSelect = {}, isLeftHanded = true)
        }
    }
}
