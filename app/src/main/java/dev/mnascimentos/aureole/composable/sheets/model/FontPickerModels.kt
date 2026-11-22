package dev.mnascimentos.aureole.composable.sheets.model

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import dev.mnascimentos.aureole.core.designsystem.theme.model.AureoleFontCatalog
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_LAUNCHER

/**
 * `PickFontBottomSheet` opened by a component that has its own font (the clock), instead of by Customize › Font.
 * The sheet then offers "Launcher font" first ([FONT_KEY_LAUNCHER]), shows [sample] in each font in place of "Aa",
 * has a bold switch, and applies every choice at once (a tap on a font also closes it).
 *
 * @property title Title of the sheet ("Clock font").
 * @property subtitle Line under the title ("Only for this clock").
 * @property sectionLabel Label above the grid ("CLOCK FONT").
 * @property sample Text each tile writes in its font (the current time, in the clock's format).
 * @property isBold Whether the bold switch is on; the samples follow it.
 * @property onBoldChange Called when the bold switch is flipped.
 * @property preview The component drawn above the sheet, so every choice shows on it at once.
 */
data class ComponentFontOptions(
    val title: String,
    val subtitle: String,
    val sectionLabel: String,
    val sample: String,
    val isBold: Boolean,
    val onBoldChange: (Boolean) -> Unit,
    val preview: (@Composable BoxScope.() -> Unit)? = null
)

/**
 * How a font chip writes its sample: "Aa" when [text] is null (Customize), or [text] (the time), in bold when
 * [isBold]. [isLeftHanded] mirrors the chip.
 */
data class FontChipLook(
    val text: String? = null,
    val isBold: Boolean = false,
    val isLeftHanded: Boolean = false
)

/** Keys the font picker lists, in order: the whole catalog, after "Launcher font" when [withLauncherFont]. */
fun fontPickerKeys(withLauncherFont: Boolean): List<String> {
    val catalog = AureoleFontCatalog.map { it.key }
    return if (withLauncherFont) listOf(FONT_KEY_LAUNCHER) + catalog else catalog
}

/**
 * The key the picker marks for the saved [selected] font: itself when listed, otherwise the first key, the font
 * an unknown key falls back to (Istok Web for the launcher, the launcher font for a component). So the current
 * font is always marked.
 */
fun markedFontKey(selected: String, keys: List<String>): String = if (selected in keys) selected else keys.first()
