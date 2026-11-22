package dev.mnascimentos.aureole.core.data.model

import dev.mnascimentos.aureole.core.designsystem.theme.model.AureoleFontCatalog
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_LAUNCHER
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_MONOSPACED
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_ROUNDED
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_SERIF

/**
 * The font of a clock: a key of `AureoleFontCatalog`, or [FONT_KEY_LAUNCHER] to follow the launcher font, and
 * whether the time is bold.
 */
data class ClockFontChoice(val fontKey: String, val isBold: Boolean) {
    /** True when the clock follows the launcher font instead of a font of its own. */
    val followsLauncher: Boolean get() = fontKey == FONT_KEY_LAUNCHER
}

/**
 * Font keys saved by 0.9.59 and older (the five "Font style" tiles, case-insensitive, also by enum name) and the
 * catalog key each one became. "Bold" was a font and is now the launcher font with the bold switch on.
 */
private val LEGACY_CLOCK_FONTS: Map<String, String> = mapOf(
    "SANS_SERIF" to FONT_KEY_LAUNCHER,
    "THEME" to FONT_KEY_LAUNCHER,
    "DEFAULT" to FONT_KEY_LAUNCHER,
    "BOLD" to FONT_KEY_LAUNCHER,
    "SERIF" to FONT_KEY_SERIF,
    "MONOSPACE" to FONT_KEY_MONOSPACED,
    "MONO" to FONT_KEY_MONOSPACED,
    "ROUNDED" to FONT_KEY_ROUNDED
)

/**
 * Bold of a clock saved with an old key: the old clock always drew the time in bold (`timeTypeface` before
 * 0.9.60), so every old value keeps it on and nobody's clock gets thinner (`seletores-relogio` D-12).
 */
private const val LEGACY_CLOCK_BOLD = true

/**
 * Reads a saved clock font (RF-C1). A catalog key or [FONT_KEY_LAUNCHER] stays as it is, with [storedBold]. An old
 * key becomes the catalog font with the same look (Theme default and Bold → launcher font, Serif, Monospaced,
 * Rounded), bold on; anything unknown becomes the launcher font. Running it again changes nothing.
 */
fun resolveClockFont(storedKey: String, storedBold: Boolean): ClockFontChoice {
    if (storedKey == FONT_KEY_LAUNCHER || AureoleFontCatalog.any { it.key == storedKey }) {
        return ClockFontChoice(storedKey, storedBold)
    }
    val migrated = LEGACY_CLOCK_FONTS[storedKey.uppercase()] ?: FONT_KEY_LAUNCHER
    return ClockFontChoice(migrated, LEGACY_CLOCK_BOLD)
}

/** The font of [this] config, read with [resolveClockFont]. */
val ClockConfig.fontChoice: ClockFontChoice
    get() = resolveClockFont(fontFamily, isFontBold)

/** [this] config with its font read as a catalog key and its bold set ([resolveClockFont]). */
fun ClockConfig.withMigratedFont(): ClockConfig {
    val choice = fontChoice
    return copy(fontFamily = choice.fontKey, isFontBold = choice.isBold)
}
