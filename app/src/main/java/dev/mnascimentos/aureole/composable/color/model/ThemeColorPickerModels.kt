package dev.mnascimentos.aureole.composable.color.model

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.toArgb
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleColors

@StringRes
private fun ThemeColorRole.labelRes(): Int = when (this) {
    ThemeColorRole.BACKGROUND -> R.string.settings_background_swatch_background
    ThemeColorRole.SURFACE -> R.string.settings_background_swatch_surface
    ThemeColorRole.VARIANT -> R.string.settings_background_swatch_variant
    ThemeColorRole.OUTLINE -> R.string.settings_background_swatch_outline
    ThemeColorRole.ACCENT -> R.string.settings_background_swatch_accent
}

/** The five roles of the current theme as labeled swatches, in [ThemeColorRole] order (Fundo ... Destaque). */
fun themeRoleSwatches(colors: AureoleColors): List<ThemeSwatch> = ThemeColorRole.entries.map { role ->
    ThemeSwatch(role.labelRes(), ThemeColorRef.Role(role).resolve(colors).toArgb())
}

/** The role behind [swatch], one of [themeRoleSwatches]; null when it is not one of them. */
fun themeRoleOf(swatch: ThemeSwatch): ThemeColorRole? =
    ThemeColorRole.entries.firstOrNull { it.labelRes() == swatch.labelRes }

/** True when [swatch] stands for the theme role [selected] points to. */
fun isThemeRoleSelected(swatch: ThemeSwatch, selected: ThemeColorRef?): Boolean =
    selected is ThemeColorRef.Role && themeRoleOf(swatch) == selected.role

/** The ARGB color of [selected] when it is a fixed color of another palette, else null (a role or nothing). */
fun fixedPaletteColor(selected: ThemeColorRef?): Int? = (selected as? ThemeColorRef.Fixed)?.argb
