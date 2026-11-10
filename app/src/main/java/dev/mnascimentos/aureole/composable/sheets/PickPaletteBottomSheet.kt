package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import dev.mnascimentos.aureole.core.designsystem.palette.toLight
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val SWATCH_WIDTH_DP = 140

@Composable
fun PickPaletteBottomSheet(
    selectedPaletteName: String,
    onPaletteSelected: (ThemePalette) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    footer: (@Composable () -> Unit)? = null
) {
    val isDarkTheme = isSystemInDarkTheme()
    val cleanSelectedName = selectedPaletteName.removeSuffix(" Light").trim()

    SettingsBottomSheet(
        title = stringResource(R.string.settings_appearance_pick_palette_title),
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        PickerSheetBody(footer = footer) { listModifier ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
                modifier = listModifier
            ) {
                items(
                    items = allPalettes,
                    key = { it.name },
                    contentType = { "palette_row" }
                ) { basePalette ->
                    val isSelected = basePalette.name.equals(cleanSelectedName, ignoreCase = true)
                    val activePalette = if (isDarkTheme) basePalette else basePalette.toLight()
                    PaletteRowItem(
                        palette = activePalette,
                        displayName = basePalette.name,
                        isSelected = isSelected,
                        onSelect = { onPaletteSelected(basePalette) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PaletteRowItem(
    palette: ThemePalette,
    displayName: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val rowBg = if (isSelected) {
        AureoleDS.colors.surfaceVariant.copy(alpha = 0.5f)
    } else {
        Color.Transparent
    }
    val textColor = if (isSelected) {
        AureoleDS.colors.onSurfaceHigh
    } else {
        AureoleDS.colors.onSurfaceMedium
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(rowBg)
            .clickable(
                role = Role.RadioButton,
                onClick = onSelect
            )
            .padding(vertical = AureoleDS.dimens.small, horizontal = AureoleDS.dimens.xSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleText(
            text = displayName,
            color = textColor,
            modifier = Modifier.weight(1f),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.titleMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        )

        PaletteColorSwatches(palette = palette)
    }
}

@Composable
private fun PaletteColorSwatches(palette: ThemePalette) {
    Row(
        modifier = Modifier
            .width(SWATCH_WIDTH_DP.dp)
            .height(AureoleDS.dimens.iconLargePlus)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            .border(
                AureoleDS.dimens.borderMinimal,
                AureoleDS.colors.outline,
                RoundedCornerShape(AureoleDS.dimens.radiusXSmall)
            )
    ) {
        val colorList = listOf(
            palette.colors.background,
            palette.colors.surface,
            palette.colors.surfaceVariant,
            palette.colors.outline,
            palette.colors.onSurfaceLow,
            palette.colors.onSurfaceMedium,
            palette.colors.onSurfaceHigh
        )
        colorList.forEach { color ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(color)
            )
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
