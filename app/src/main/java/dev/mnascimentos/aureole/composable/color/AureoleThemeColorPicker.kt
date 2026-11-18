package dev.mnascimentos.aureole.composable.color

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.color.model.ThemeDefaultOption
import dev.mnascimentos.aureole.composable.color.model.ThemeSwatch
import dev.mnascimentos.aureole.composable.color.model.fixedPaletteColor
import dev.mnascimentos.aureole.composable.color.model.isThemeRoleSelected
import dev.mnascimentos.aureole.composable.color.model.themeRoleOf
import dev.mnascimentos.aureole.composable.color.model.themeRoleSwatches
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Color picker whose result follows the theme or not (pasta-form RF-16): "From your theme" offers the five roles
 * of the current theme (saved as a [ThemeColorRef.Role], so they change with the theme) and "From other palettes"
 * one tone of each palette by family (saved as a [ThemeColorRef.Fixed]). Stateless: [selected] is marked and the
 * choice goes to [onSelect]; a null [selected] marks nothing. Resolve the result with [ThemeColorRef.resolve].
 * Its labels are the menu section labels, as the label of the sheet above it (same inset and style).
 */
@Composable
fun AureoleThemeColorPicker(
    selected: ThemeColorRef?,
    onSelect: (ThemeColorRef) -> Unit,
    modifier: Modifier = Modifier,
    defaultOption: ThemeDefaultOption? = null
) {
    // Section label to content 8 dp, between sections 16 dp (clock-colors RF-D8).
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium)) {
        if (defaultOption != null) {
            PickerSection(R.string.theme_color_section_default) {
                AureoleThemeSwatchRow(
                    swatches = listOf(ThemeSwatch(R.string.theme_color_swatch_default, defaultOption.color)),
                    isSelected = { defaultOption.isSelected },
                    onSelect = { defaultOption.onSelect() }
                )
            }
        }
        PickerSection(R.string.settings_background_section_theme) {
            AureoleThemeSwatchRow(
                swatches = themeRoleSwatches(AureoleDS.colors),
                isSelected = { isThemeRoleSelected(it, selected) },
                onSelect = { swatch -> themeRoleOf(swatch)?.let { onSelect(ThemeColorRef.Role(it)) } }
            )
        }
        PickerSection(R.string.theme_color_section_other_palettes) {
            AureolePaletteSwatchRow(
                selected = fixedPaletteColor(selected),
                onSelect = { onSelect(ThemeColorRef.Fixed(it)) }
            )
        }
    }
}

@Composable
private fun PickerSection(@StringRes label: Int, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
        AureoleMenuSectionLabel(text = stringResource(label))
        content()
    }
}

@AureolePreview
@Composable
private fun AureoleThemeColorPickerPreview() {
    AureoleLauncherTheme {
        AureoleThemeColorPicker(
            selected = ThemeColorRef.Role(ThemeColorRole.VARIANT),
            onSelect = {}
        )
    }
}
