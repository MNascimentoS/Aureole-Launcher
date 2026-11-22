package dev.mnascimentos.aureole.core.designsystem.theme.model

import androidx.annotation.StringRes
import dev.mnascimentos.aureole.R

/**
 * One font of the single catalog shared by the launcher font picker and by every component that has its own font
 * (the clock). The family lives in `AvailableFonts`, under the same [key].
 *
 * @property key Stable string that is persisted; never rename it.
 * @property nameRes Display name when it is not a proper name (Serif, Monospaced, Rounded); null shows the [key].
 */
data class AureoleFontEntry(val key: String, @StringRes val nameRes: Int? = null)

const val FONT_KEY_SERIF = "Serif"
const val FONT_KEY_MONOSPACED = "Monospaced"
const val FONT_KEY_ROUNDED = "Rounded"

/** Every font the user can pick, in picker order. */
val AureoleFontCatalog: List<AureoleFontEntry> = listOf(
    AureoleFontEntry("Istok Web"),
    AureoleFontEntry("Roboto"),
    AureoleFontEntry("Roboto Mono"),
    AureoleFontEntry("Montserrat"),
    AureoleFontEntry("Andika"),
    AureoleFontEntry("Rubik"),
    AureoleFontEntry("Chelsea Market"),
    AureoleFontEntry("Special Elite"),
    AureoleFontEntry("Orbitron"),
    AureoleFontEntry("Space Grotesk"),
    AureoleFontEntry("Space Mono"),
    AureoleFontEntry("Audiowide"),
    AureoleFontEntry(FONT_KEY_SERIF, R.string.settings_appearance_font_serif),
    AureoleFontEntry(FONT_KEY_MONOSPACED, R.string.settings_appearance_font_monospaced),
    AureoleFontEntry(FONT_KEY_ROUNDED, R.string.settings_appearance_font_rounded),
    AureoleFontEntry("Poppins"),
    AureoleFontEntry("Outfit"),
    AureoleFontEntry("Manrope"),
    AureoleFontEntry("Lexend"),
    AureoleFontEntry("Quicksand"),
    AureoleFontEntry("Nunito"),
    AureoleFontEntry("Josefin Sans"),
    AureoleFontEntry("Bebas Neue"),
    AureoleFontEntry("Righteous"),
    AureoleFontEntry("Michroma"),
    AureoleFontEntry("Playfair Display"),
    AureoleFontEntry("DM Serif Display"),
    AureoleFontEntry("Lora"),
    AureoleFontEntry("Abril Fatface"),
    AureoleFontEntry("JetBrains Mono"),
    AureoleFontEntry("Share Tech Mono"),
    AureoleFontEntry("VT323")
)

/** String resource of the display name of [key], or null when the key itself is the (proper) name. */
fun fontNameRes(key: String): Int? = AureoleFontCatalog.firstOrNull { it.key == key }?.nameRes
