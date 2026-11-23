package dev.mnascimentos.aureole.composable.sheets.model

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import dev.mnascimentos.aureole.core.designsystem.palette.toLight

/** What a color picked in the palette picker paints, so each tile shows the tone of that role (D-09). */
enum class ComponentColorRole {
    /** Text drawn by the component: the accent of each palette (`onSurfaceMedium`). */
    TEXT,

    /** Card behind the component: the variant of each palette (`surfaceVariant`), the clock card's own tone. */
    CARD
}

/** The tone of [palette] this role takes. */
fun ComponentColorRole.colorIn(palette: ThemePalette): Color = when (this) {
    ComponentColorRole.TEXT -> palette.colors.onSurfaceMedium
    ComponentColorRole.CARD -> palette.colors.surfaceVariant
}

/**
 * `PickPaletteBottomSheet` in color mode: opened by a component that picks one color (the clock text or card)
 * instead of by Customize › Palette. The sheet offers "Theme" first, then the saved color as "Current" when no
 * palette has it, then every palette as a tile with only the tone of [role]. Every choice applies at once.
 *
 * @property role What the color paints; picks the tone of each palette.
 * @property title Title of the sheet ("Text color").
 * @property subtitle Line under the title ("Only for this clock").
 * @property selected The saved color; null follows the theme.
 * @property themeColor What "Theme" gives under the current theme (the component's default color).
 * @property onSelect Called with the chosen color: null for "Theme", else a [ThemeColorRef].
 * @property preview The component drawn above the sheet, so every choice shows on it at once.
 */
data class ComponentColorOptions(
    val role: ComponentColorRole,
    val title: String,
    val subtitle: String,
    val selected: ThemeColorRef?,
    val themeColor: Color,
    val onSelect: (ThemeColorRef?) -> Unit,
    val preview: (@Composable BoxScope.() -> Unit)? = null
)

/** One tile of the palette picker in color mode. */
sealed interface ComponentColorEntry {
    /** Stable key of the tile in the grid. */
    val key: String

    /** "Theme": follows the current theme (saved as null). */
    data object Theme : ComponentColorEntry {
        override val key: String = "color_theme"
    }

    /** The saved [ref], kept because no palette has it (an old fixed color or a theme role). */
    data class Current(val ref: ThemeColorRef) : ComponentColorEntry {
        override val key: String = CURRENT_KEY
    }

    /** A palette as displayed ([palette], the light variant on a light system) under its base [name]. */
    data class Palette(val palette: ThemePalette, val name: String) : ComponentColorEntry {
        override val key: String = "color_palette_$name"
    }
}

private const val CURRENT_KEY = "color_current"

/**
 * Base name of the palette whose [role] tone is [ref], in its dark or light variant; null when [ref] is not a
 * fixed color or no palette has it. Palettes that share the tone resolve to the first one.
 */
fun paletteNameOf(
    ref: ThemeColorRef?,
    role: ComponentColorRole,
    palettes: List<ThemePalette> = allPalettes
): String? {
    val argb = (ref as? ThemeColorRef.Fixed)?.argb ?: return null
    return palettes.firstOrNull { palette ->
        role.colorIn(palette).toArgb() == argb || role.colorIn(palette.toLight()).toArgb() == argb
    }?.name
}

/**
 * The tile that stands for [selected]: "Theme" for null, the palette that has it, else "Current". [isDarkTheme]
 * picks the displayed variant of the palette.
 */
fun selectedColorEntry(
    selected: ThemeColorRef?,
    role: ComponentColorRole,
    isDarkTheme: Boolean,
    palettes: List<ThemePalette> = allPalettes
): ComponentColorEntry {
    if (selected == null) return ComponentColorEntry.Theme
    val name = paletteNameOf(selected, role, palettes) ?: return ComponentColorEntry.Current(selected)
    return palettes.first { it.name == name }.toEntry(isDarkTheme)
}

/**
 * Tiles of the picker in color mode, in order: "Theme", "Current" when [selected] is in no palette (RF-F4), then
 * every palette once, displayed as the variant of [isDarkTheme].
 */
fun componentColorEntries(
    selected: ThemeColorRef?,
    role: ComponentColorRole,
    isDarkTheme: Boolean,
    palettes: List<ThemePalette> = allPalettes
): List<ComponentColorEntry> {
    val current = selectedColorEntry(selected, role, isDarkTheme, palettes) as? ComponentColorEntry.Current
    val shown = palettes.distinctBy { it.name }.map { it.toEntry(isDarkTheme) }
    return listOf(ComponentColorEntry.Theme) + listOfNotNull(current) + shown
}

/** What picking this tile saves: null for "Theme", the kept ref for "Current", the palette's [role] tone. */
fun ComponentColorEntry.toColorRef(role: ComponentColorRole): ThemeColorRef? = when (this) {
    ComponentColorEntry.Theme -> null
    is ComponentColorEntry.Current -> ref
    is ComponentColorEntry.Palette -> ThemeColorRef.Fixed(role.colorIn(palette).toArgb())
}

private fun ThemePalette.toEntry(isDarkTheme: Boolean): ComponentColorEntry.Palette =
    ComponentColorEntry.Palette(palette = if (isDarkTheme) this else toLight(), name = name)
