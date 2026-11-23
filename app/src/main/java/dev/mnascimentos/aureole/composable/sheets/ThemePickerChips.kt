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
import dev.mnascimentos.aureole.composable.sheets.model.FontChipLook
import dev.mnascimentos.aureole.composable.sheets.model.PaletteChipLabel
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AvailableFonts
import dev.mnascimentos.aureole.core.designsystem.theme.model.fontNameRes
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val FONT_SAMPLE_WIDTH_DP = 30
private const val UNSELECTED_FONT_ALPHA = 0.35f
private const val SELECTED_FONT_ALPHA = 0.5f

/**
 * Palette chip: the palette's own background, a strip of its colors and its name written in its own
 * highlight color, outlined when [isSelected]. The colors come from [palette], not from the current theme.
 * With [strip] (the palette picker in color mode) the strip is that one color instead of all of them.
 */
@Composable
internal fun PaletteChip(
    palette: ThemePalette,
    label: PaletteChipLabel,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayName = label.name
    val strip = label.strip
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
        PaletteColorStrip(
            strip?.let(::listOf) ?: listOf(
                palette.colors.surface,
                palette.colors.surfaceVariant,
                palette.colors.onSurfaceLow,
                palette.colors.onSurfaceMedium,
                palette.colors.onSurfaceHigh
            )
        )
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
private fun PaletteColorStrip(colors: List<Color>) {
    val stripShape = RoundedCornerShape(AureoleDS.dimens.radiusMinimal)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(AureoleDS.dimens.iconMediumSmall)
            .clip(stripShape)
    ) {
        colors.forEach { color ->
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
 * Font chip: a sample ("Aa", or the text of [look]) and the font name, both written in the font itself, outlined
 * when [isSelected]. Left-handed ([FontChipLook.isLeftHanded]) the chip is mirrored: the name on the left and the
 * sample on the right. A key with no family of its own (the launcher font) is written in the theme font.
 */
@Composable
internal fun FontChip(
    fontName: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
    look: FontChipLook = FontChipLook()
) {
    val isLeftHanded = look.isLeftHanded
    val shape = RoundedCornerShape(AureoleDS.dimens.radiusSmall)
    val family = AvailableFonts[fontName]
    val displayName = fontNameRes(fontName)?.let { stringResource(it) } ?: fontName
    val description = stringResource(R.string.settings_appearance_font_chip_description, displayName)
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
            FontChipName(displayName, family, isLeftHanded, Modifier.weight(1f))
            FontChipSample(family, look)
        } else {
            FontChipSample(family, look)
            FontChipName(displayName, family, isLeftHanded, Modifier.weight(1f))
        }
    }
}

/** "Aa" in a fixed width, or the text of [look] (the time) at its own width, bold when [look] asks. */
@Composable
private fun FontChipSample(family: FontFamily?, look: FontChipLook) {
    AureoleText(
        text = look.text ?: stringResource(R.string.settings_appearance_font_chip_sample),
        color = AureoleDS.colors.onSurfaceHigh,
        modifier = if (look.text == null) Modifier.width(FONT_SAMPLE_WIDTH_DP.dp) else Modifier,
        format = AureoleTextFormatParams(
            style = AureoleDS.typography.titleMedium,
            fontFamily = family,
            fontWeight = if (look.isBold) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
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
            PaletteChip(palette = palette, label = PaletteChipLabel(palette.name), isSelected = true, onSelect = {})
            FontChip(fontName = "Istok Web", isSelected = false, onSelect = {})
            FontChip(fontName = "Istok Web", isSelected = true, onSelect = {}, look = FontChipLook(isLeftHanded = true))
            FontChip(fontName = "Serif", isSelected = false, onSelect = {}, look = FontChipLook("16:16", isBold = true))
        }
    }
}
